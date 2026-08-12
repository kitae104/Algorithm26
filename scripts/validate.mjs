/*
 * 사이트 정합성 검증 스크립트 (npm run build / npm test 로 실행)
 *
 * 검사 항목
 *  1. 강의 데이터: algorithms-data.js와 data/algorithms.json 동기화, 13개 강좌, 필수 필드
 *  2. 강의 HTML: 파일 존재, 20개 필수 섹션 id, data-lesson-id 일치, 필수 스크립트/CSS 링크,
 *     quiz-root / lesson-pager / lesson-complete-slot / lesson-toc-list 존재
 *  3. 내부 링크: HTML 안의 상대 링크(href/src)가 실제 파일을 가리키는지
 *  4. code-card: data-copy-target이 가리키는 id 존재 여부, 페이지 내 id 중복
 *  5. Java 예제: 각 강의 폴더 존재 + .java 파일 존재
 * 실패가 하나라도 있으면 exit code 1.
 */
import { readFileSync, existsSync, readdirSync } from "node:fs";
import { resolve, dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");

let errors = [];
let warnings = [];

function fail(msg) { errors.push(msg); }
function warn(msg) { warnings.push(msg); }

/* ---------- 1. 강의 데이터 ---------- */
const dataJsSource = readFileSync(join(ROOT, "assets/js/algorithms-data.js"), "utf8");
const sandbox = {};
new Function("window", dataJsSource)(sandbox);
const lessons = sandbox.ALGORITHMS || [];

if (lessons.length !== 13) fail(`강좌 수가 13개가 아님: ${lessons.length}개`);

const json = JSON.parse(readFileSync(join(ROOT, "data/algorithms.json"), "utf8"));
if (JSON.stringify(json) !== JSON.stringify(lessons)) {
    fail("data/algorithms.json이 assets/js/algorithms-data.js와 동기화되지 않음");
}

const REQUIRED_FIELDS = ["order", "id", "title", "englishTitle", "category", "difficulty", "examples", "language", "description", "path"];
for (const lesson of lessons) {
    for (const field of REQUIRED_FIELDS) {
        if (lesson[field] === undefined || lesson[field] === "") {
            fail(`강의 ${lesson.id || "?"}: 필드 누락 — ${field}`);
        }
    }
}

/* ---------- 1-b. 보충 자료(추가 정보) 데이터 ----------
   커리큘럼 밖의 자료지만 데이터 구조는 강의와 같은 규율을 지킨다.
   특히 relatedLessons가 실제 강의 번호를 가리키는지 확인한다 —
   랜딩 카드에 "바꿔 볼 코드 4강 · 5강"으로 그대로 찍히기 때문에
   없는 번호가 들어가면 학생을 헛걸음시킨다. */
const supplements = sandbox.SUPPLEMENTS || [];
if (supplements.length === 0) fail("보충 자료(SUPPLEMENTS)가 비어 있음");

const SUPPLEMENT_FIELDS = REQUIRED_FIELDS.concat(["summary", "relatedLessons"]);
const lessonOrders = new Set(lessons.map(l => l.order));
for (const item of supplements) {
    for (const field of SUPPLEMENT_FIELDS) {
        if (item[field] === undefined || item[field] === "") {
            fail(`보충 자료 ${item.id || "?"}: 필드 누락 — ${field}`);
        }
    }
    /* 강의와 id가 겹치면 진도 저장소(all-progress-v1)에서 같은 칸을 쓴다 */
    if (lessons.some(l => l.id === item.id)) {
        fail(`보충 자료 ${item.id}: 강의와 id가 겹침 — 진도 저장이 섞인다`);
    }
    for (const order of item.relatedLessons || []) {
        if (!lessonOrders.has(order)) {
            fail(`보충 자료 ${item.id}: relatedLessons에 없는 강의 번호 — ${order}강`);
        }
    }
    if (!(sandbox.CATEGORY_KEYS || {})[item.category]) {
        fail(`보충 자료 ${item.id}: CATEGORY_KEYS에 없는 분류 — ${item.category}`);
    }
}

/* ---------- 2. 강의 HTML 구조 ---------- */
const REQUIRED_SECTIONS = [
    "sec-intro", "sec-objectives", "sec-prereq", "sec-problem",
    "sec-hand", "sec-concepts", "sec-steps", "sec-pseudo", "sec-impl",
    "sec-complete", "sec-trace", "sec-bugs", "sec-complexity",
    "sec-application", "sec-quiz", "sec-practice", "sec-final",
    "sec-summary", "sec-next"
];

const REQUIRED_MARKERS = [
    ["data-site-header", "상단 내비게이션 자리"],
    ["lesson-toc-list", "내부 목차"],
    ["lesson-pager", "이전/다음 강의"],
    ["lesson-complete-slot", "완료 버튼 자리"],
    ["quiz-root", "퀴즈 루트"],
    ["assets/js/common.js", "common.js 로드"],
    ["assets/js/visualization.js", "visualization.js 로드"],
    ["assets/js/quiz.js", "quiz.js 로드"],
    ["assets/css/print.css", "인쇄 CSS"],
    ["AlgoQuiz.init", "퀴즈 초기화"],
    ["AlgoViz.create", "시각화 초기화"],
    ["details class=\"answer-box\"", "정답 접기"]
];

/* 화면에서 제거한 기능이 다시 들어오지 않게 (인쇄 버튼) */
const FORBIDDEN_MARKERS = [
    ["data-print", "인쇄 버튼(현재 인쇄 기능은 화면에서 제공하지 않음)"],
    ["lesson-hero__actions", "인쇄 버튼 래퍼(빈 껍데기)"]
];

for (const lesson of lessons) {
    const filePath = join(ROOT, lesson.path);
    if (!existsSync(filePath)) {
        fail(`강의 파일 없음: ${lesson.path}`);
        continue;
    }
    const html = readFileSync(filePath, "utf8");

    for (const id of REQUIRED_SECTIONS) {
        if (!html.includes(`id="${id}"`)) fail(`${lesson.path}: 필수 섹션 누락 — ${id}`);
    }
    if (!html.includes(`data-lesson-id="${lesson.id}"`)) {
        fail(`${lesson.path}: body data-lesson-id가 "${lesson.id}"가 아님`);
    }
    for (const [marker, label] of REQUIRED_MARKERS) {
        if (!html.includes(marker)) fail(`${lesson.path}: ${label}(${marker}) 누락`);
    }
    for (const [marker, label] of FORBIDDEN_MARKERS) {
        if (html.includes(marker)) fail(`${lesson.path}: 제거된 마크업이 남아 있음 — ${label}(${marker})`);
    }

    /* 4. code-card 복사 대상 검증 + id 중복 */
    const copyTargets = [...html.matchAll(/data-copy-target="([^"]+)"/g)].map(m => m[1]);
    const ids = [...html.matchAll(/\sid="([^"]+)"/g)].map(m => m[1]);
    const idSet = new Set();
    for (const id of ids) {
        if (idSet.has(id)) fail(`${lesson.path}: id 중복 — ${id}`);
        idSet.add(id);
    }
    for (const target of copyTargets) {
        if (!idSet.has(target)) fail(`${lesson.path}: 복사 대상 id 없음 — ${target}`);
    }

    /* 3. 내부 링크 검증 (http/#/data: 제외) */
    const refs = [...html.matchAll(/(?:href|src)="([^"#]+?)"/g)].map(m => m[1])
        .filter(u => !/^(https?:|data:|mailto:|javascript:)/.test(u));
    for (const ref of refs) {
        const target = resolve(dirname(filePath), ref.split("?")[0]);
        if (!existsSync(target)) fail(`${lesson.path}: 깨진 링크 — ${ref}`);
    }

    /* 20개 섹션 순서 검증 */
    let lastIndex = -1;
    for (const id of REQUIRED_SECTIONS) {
        const idx = html.indexOf(`id="${id}"`);
        if (idx !== -1 && idx < lastIndex) warn(`${lesson.path}: 섹션 순서 어긋남 — ${id}`);
        if (idx !== -1) lastIndex = idx;
    }

    /* 핵심 정리 섹션이 전용 카드 클래스를 쓰는지 */
    const summaryMatch = html.match(/id="sec-summary"[\s\S]*?<\/section>/);
    if (!summaryMatch) {
        fail(`${lesson.path}: sec-summary 섹션을 찾을 수 없음`);
    } else if (!summaryMatch[0].includes('class="summary-list"')) {
        fail(`${lesson.path}: 핵심 정리 목록에 summary-list 클래스 없음`);
    }
}

/* ---------- 2-b. 보충 자료 HTML 구조 ----------
   강의의 20개 섹션 규격은 적용하지 않는다(커리큘럼이 아니다).
   대신 "강의가 아니라는 것"이 마크업에서도 지켜지는지를 본다:
   data-lesson-id를 달면 common.js가 진도를 기록하고 이전/다음 강의를
   붙여 13강 목록에 끼어든 것처럼 보인다. */
const SUPPLEMENT_MARKERS = [
    ["data-site-header", "상단 내비게이션 자리"],
    ["lesson-toc-list", "내부 목차"],
    ["lesson-pager", "문서 이동"],
    ["quiz-root", "퀴즈 루트"],
    ["assets/js/common.js", "common.js 로드"],
    ["assets/js/code-copy.js", "코드 복사 로드"],
    ["assets/js/quiz.js", "quiz.js 로드"],
    ["assets/css/print.css", "인쇄 CSS"],
    ["AlgoQuiz.init", "퀴즈 초기화"]
];

for (const item of supplements) {
    const filePath = join(ROOT, item.path);
    if (!existsSync(filePath)) {
        fail(`보충 자료 파일 없음: ${item.path}`);
        continue;
    }
    const html = readFileSync(filePath, "utf8");

    if (!html.includes(`data-supplement-id="${item.id}"`)) {
        fail(`${item.path}: body data-supplement-id가 "${item.id}"가 아님`);
    }
    if (html.includes("data-lesson-id=")) {
        fail(`${item.path}: data-lesson-id가 있음 — 보충 자료가 13강 진도에 섞인다`);
    }
    if (html.includes("lesson-complete-slot")) {
        fail(`${item.path}: 완료 버튼 자리가 있음 — 보충 자료에는 완료 표시를 두지 않는다`);
    }
    for (const [marker, label] of SUPPLEMENT_MARKERS) {
        if (!html.includes(marker)) fail(`${item.path}: ${label}(${marker}) 누락`);
    }

    const copyTargets = [...html.matchAll(/data-copy-target="([^"]+)"/g)].map(m => m[1]);
    const ids = [...html.matchAll(/\sid="([^"]+)"/g)].map(m => m[1]);
    const idSet = new Set();
    for (const id of ids) {
        if (idSet.has(id)) fail(`${item.path}: id 중복 — ${id}`);
        idSet.add(id);
    }
    for (const target of copyTargets) {
        if (!idSet.has(target)) fail(`${item.path}: 복사 대상 id 없음 — ${target}`);
    }

    const refs = [...html.matchAll(/(?:href|src)="([^"#]+?)"/g)].map(m => m[1])
        .filter(u => !/^(https?:|data:|mailto:|javascript:)/.test(u));
    for (const ref of refs) {
        const target = resolve(dirname(filePath), ref.split("?")[0]);
        if (!existsSync(target)) fail(`${item.path}: 깨진 링크 — ${ref}`);
    }
}

/* ---------- CSS 정적 불변식 ---------- */
const commonCss = readFileSync(join(ROOT, "assets/css/common.css"), "utf8");

const MOTION_TOKENS = [
    "--dur-fast", "--dur-base", "--dur-slow",
    "--ease-out", "--ease-settle", "--ease-in-out"
];
for (const token of MOTION_TOKENS) {
    if (!commonCss.includes(token + ":")) {
        fail(`common.css: 모션 토큰 정의 누락 — ${token}`);
    }
}

/* reduced-motion 블록이 duration 토큰을 무력화하는지 */
const reducedBlock = commonCss.match(/@media \(prefers-reduced-motion: reduce\)\s*\{[\s\S]*?\n\}/);
if (!reducedBlock) {
    fail("common.css: prefers-reduced-motion 블록을 찾을 수 없음");
} else {
    for (const token of ["--dur-fast", "--dur-base", "--dur-slow"]) {
        if (!reducedBlock[0].includes(token)) {
            fail(`common.css: reduced-motion에서 ${token}을 무력화하지 않음`);
        }
    }
}

/* 사용하지 않는 규칙이 다시 들어오지 않게 (assets/css 전체를 스캔) */
const DEAD_SELECTORS = [
    ".inline-array", ".lesson-hero__actions", ".print-button",
    /* 랜딩에서 걷어낸 진도 UI — 전체 진행률 배너와 카드의 학습 상태 글자 */
    ".overall-progress", ".course-card__status"
];
const cssDir = join(ROOT, "assets/css");
for (const file of readdirSync(cssDir)) {
    if (!file.endsWith(".css")) continue;
    const css = readFileSync(join(cssDir, file), "utf8");
    for (const selector of DEAD_SELECTORS) {
        if (css.includes(selector)) {
            fail(`${file}: 사용처가 없는 ${selector} 규칙이 남아 있음`);
        }
    }
}

/* 인쇄용 색 재정의가 빠짐없는지.
   common.css의 :root가 정의한 색 토큰 중 하나라도 @media print에서 덮이지
   않으면 다크 테마 인쇄에서 그 토큰만 어두운 값으로 남아 잉크와 충돌한다.
   (실제로 --brand-soft가 빠져 본문 강조가 검정 on 남색 1.54:1이 된 적 있음) */
const NON_COLOR_TOKEN = /^--(dur|ease|font|radius|shadow|header|space|z)-/;
const rootBlock = commonCss.match(/^:root\s*\{[\s\S]*?^\}/m);
if (!rootBlock) {
    fail("common.css: :root 토큰 블록을 찾을 수 없음");
} else {
    const printCss = readFileSync(join(ROOT, "assets/css/print.css"), "utf8");
    const printBlock = printCss.match(/@media print\s*\{[\s\S]*\}/);
    const colorTokens = [...rootBlock[0].matchAll(/(--[a-z0-9-]+)\s*:/g)]
        .map(m => m[1])
        .filter(t => !NON_COLOR_TOKEN.test(t));
    for (const token of colorTokens) {
        if (!printBlock || !new RegExp(token + "\\s*:").test(printBlock[0])) {
            fail(`print.css: 색 토큰 ${token}을 인쇄용으로 재정의하지 않음 — 다크 테마 인쇄에서 잉크와 충돌한다`);
        }
    }
}

/* 브라우저 JS는 ES5 문법만 쓴다 (scripts/는 Node ESM이라 제외).
   주석을 걷어낸 뒤 검사한다 — "async/await를 쓰지 않는다" 같은 설명문에
   걸리면 안 된다. 문자열 리터럴 안의 우연한 일치를 피하려고 문법으로만
   등장하는 형태(async function / async ( / await <식>)로 좁힌다.

   예외는 하나뿐이다: hero-3d.js. three.js가 ES 모듈로만 배포되어 이 파일은
   import를 써야 하고, 따라서 <script type="module">로 실려야 한다.
   예외가 조용히 늘지 않도록 목록으로 못박고, index.html이 실제로 module로
   싣고 있는지도 함께 확인한다. */
const ES_MODULE_FILES = new Set(["hero-3d.js"]);
const jsDir = join(ROOT, "assets/js");
for (const file of readdirSync(jsDir)) {
    if (!file.endsWith(".js")) continue;
    if (ES_MODULE_FILES.has(file)) continue;
    const src = readFileSync(join(jsDir, file), "utf8")
        .replace(/\/\*[\s\S]*?\*\//g, "")
        .replace(/(^|[^:])\/\/[^\n]*/g, "$1");
    if (/\basync\s+function\b|\basync\s*\(/.test(src)) {
        fail(`assets/js/${file}: async 함수 — 브라우저 JS는 ES5 문법만 허용`);
    }
    if (/\bawait\s+[\w$([]/.test(src)) {
        fail(`assets/js/${file}: await — 브라우저 JS는 ES5 문법만 허용`);
    }
}

/* 인쇄 진입점(window.print 호출)이 다시 들어오지 않게 */
const commonJs = readFileSync(join(ROOT, "assets/js/common.js"), "utf8");

/* 히어로 모티프가 13개 강의 전부에 있는지.
   빠지면 그 강의만 히어로 우측이 조용히 비므로 눈으로는 놓치기 쉽다. */
const motifBlock = commonJs.match(/var MOTIFS = \{[\s\S]*?\n {4}\};/);
if (!motifBlock) {
    fail("common.js: MOTIFS 정의를 찾을 수 없음");
} else {
    for (const page of [...lessons, ...supplements]) {
        if (!motifBlock[0].includes(`"${page.id}":`)) {
            fail(`common.js: ${page.id}의 히어로 모티프가 없음`);
        }
    }
}

/* 보충 자료 페이지가 강의 페이지와 같은 골격을 쓰려면 common.js가
   data-supplement-id를 알아야 한다. 이 분기가 사라지면 보충 자료의
   헤더 링크가 ../ 없이 깨지고 목차도 생성되지 않는다. */
if (!commonJs.includes("dataset.supplementId")) {
    fail("common.js: data-supplement-id 처리가 없음 — 보충 자료 페이지 골격이 깨진다");
}
if (/window\.print\s*\(/.test(commonJs)) {
    fail("common.js: 인쇄 진입점(window.print)이 남아 있음 — 인쇄 기능은 현재 제공하지 않음");
}

/* ---------- index.html 링크 검증 ---------- */
const indexPath = join(ROOT, "index.html");
if (!existsSync(indexPath)) {
    fail("index.html 없음");
} else {
    const html = readFileSync(indexPath, "utf8");
    const refs = [...html.matchAll(/(?:href|src)="([^"#]+?)"/g)].map(m => m[1])
        .filter(u => !/^(https?:|data:|mailto:)/.test(u));
    for (const ref of refs) {
        const target = resolve(ROOT, ref.split("?")[0]);
        if (!existsSync(target)) fail(`index.html: 깨진 링크 — ${ref}`);
    }
    for (const marker of [
        "hero-viz-stage",       /* 3D 무대 자리 */
        "hero-viz-bars",        /* 3D가 못 뜰 때의 2D 막대 대체물 */
        "course-grid", "filter-category", "filter-difficulty", "course-search",
        "extra-grid"            /* 추가 정보 카드 자리 */
    ]) {
        if (!html.includes(marker)) fail(`index.html: 필수 요소 누락 — ${marker}`);
    }

    /* 화면에서 걷어낸 진도 UI가 다시 들어오지 않게 */
    for (const [marker, label] of [
        ["overall-progress", "전체 진행률 배너(랜딩에서 제거됨)"],
        ["course-card__status", "카드의 학습 상태 글자(랜딩에서 제거됨)"],
        ["filter-status", "학습 상태 필터(랜딩에서 제거됨)"]
    ]) {
        if (html.includes(marker)) fail(`index.html: 제거된 마크업이 남아 있음 — ${label}`);
    }

    /* three.js는 ES 모듈로만 배포된다 — import map과 module 로드가 짝이다.
       둘 중 하나만 남으면 3D 무대는 조용히 뜨지 않고 2D로만 떨어진다. */
    if (!/<script type="importmap">/.test(html)) {
        fail("index.html: three.js import map이 없음 — 3D 히어로가 로드되지 않는다");
    }
    if (!/<script type="module" src="assets\/js\/hero-3d\.js"><\/script>/.test(html)) {
        fail("index.html: hero-3d.js를 type=\"module\"로 싣지 않음");
    }
}

/* 랜딩 JS에 3D 실패 시의 2D 경로가 남아 있는지 — CDN이 막힌 환경에서
   히어로가 빈 상자로 남지 않게 하는 유일한 안전망이다. */
const landingJs = readFileSync(join(ROOT, "assets/js/landing.js"), "utf8");
if (!landingJs.includes("window.AllHero3D")) {
    fail("landing.js: 3D 무대 진입점(window.AllHero3D)을 찾을 수 없음");
}
if (!/if\s*\(stage\)/.test(landingJs)) {
    fail("landing.js: 3D 실패 시 2D 막대로 떨어지는 분기가 없음");
}

/* ---------- 5. Java 예제 폴더 ---------- */
for (const lesson of lessons) {
    const num = String(lesson.order).padStart(2, "0");
    const dir = join(ROOT, "examples", "java", `${num}-${lesson.id}`);
    if (!existsSync(dir)) {
        fail(`Java 예제 폴더 없음: examples/java/${num}-${lesson.id}`);
        continue;
    }
    const javaFiles = readdirSync(dir).filter(f => f.endsWith(".java"));
    if (javaFiles.length < 4) warn(`examples/java/${num}-${lesson.id}: Java 파일이 ${javaFiles.length}개뿐`);

    /* 강의 카드에 찍히는 예제 수는 실제 파일 수와 같아야 한다.
       수정 문제로 ModernizeSolution.java가 늘면서 어긋나기 쉬워졌다. */
    if (javaFiles.length !== lesson.examples) {
        fail(`examples/java/${num}-${lesson.id}: .java 파일이 ${javaFiles.length}개인데 ` +
            `데이터에는 examples: ${lesson.examples}로 적혀 있음`);
    }
}

/* ---------- 6. 람다·스트림 수정 문제 ----------
   커리큘럼 밖 문법(람다·스트림)을 끌어오는 자리이므로 규칙이 셋 있다.
   (1) 정해진 6개 강의에만 있어야 한다 — 재귀·DP 강의로 번지면
       "람다를 쓰면 무조건 좋다"는 오해를 준다.
   (2) 정답을 실제로 돌려 볼 수 있어야 한다(ModernizeSolution.java).
   (3) 정답에 쓰는 API는 추가 정보 문서가 가르친 것뿐이어야 한다.
       가르치지 않은 API가 정답에 나오면 학생은 풀 수가 없다. */
const MODERNIZE_LESSONS = new Set([
    "arrays-and-lists",
    "brute-force-string-hash",
    "sorting-algorithms",
    "search-algorithms",
    "greedy-algorithms",
    "algorithm-project"
]);

/* 추가 정보(람다식·자바 스트림) 문서가 다루지 않는 API */
const UNTAUGHT_APIS = [
    ".merge(", "computeIfAbsent", "averagingInt", "averagingDouble",
    "summingInt", "summingLong", "summarizingInt",
    "IntStream.of", "flatMap", "takeWhile", "dropWhile",
    "parallelStream", "naturalOrder", "mapToLong"
];

for (const lesson of lessons) {
    const num = String(lesson.order).padStart(2, "0");
    const html = existsSync(join(ROOT, lesson.path))
        ? readFileSync(join(ROOT, lesson.path), "utf8")
        : "";
    const solutionPath = join(ROOT, "examples", "java", `${num}-${lesson.id}`,
        "ModernizeSolution.java");
    const hasSection = html.includes('id="sec-modernize"');
    const hasSolution = existsSync(solutionPath);

    if (MODERNIZE_LESSONS.has(lesson.id)) {
        if (!hasSection) {
            fail(`${lesson.path}: 수정 문제 섹션(sec-modernize) 누락`);
        }
        if (!hasSolution) {
            fail(`examples/java/${num}-${lesson.id}: ModernizeSolution.java 없음`);
        }

        /* 어휘 잠금 — 섹션 안의 코드와 정답 파일 양쪽을 본다 */
        const sectionMatch = html.match(/id="sec-modernize"[\s\S]*?\n {12}<\/section>/);
        const sources = [];
        if (sectionMatch) sources.push([`${lesson.path} (sec-modernize)`, sectionMatch[0]]);
        if (hasSolution) {
            sources.push([`examples/java/${num}-${lesson.id}/ModernizeSolution.java`,
                readFileSync(solutionPath, "utf8")]);
        }
        for (const [label, source] of sources) {
            for (const api of UNTAUGHT_APIS) {
                if (source.includes(api)) {
                    fail(`${label}: 추가 정보 문서가 가르치지 않는 API — ${api}`);
                }
            }
        }
    } else {
        if (hasSection) {
            fail(`${lesson.path}: 수정 문제 대상 강의가 아닌데 sec-modernize가 있음`);
        }
        if (hasSolution) {
            fail(`examples/java/${num}-${lesson.id}: 대상 강의가 아닌데 ModernizeSolution.java가 있음`);
        }
    }
}

/* 보충 자료 예제 — 강의 폴더와 섞이지 않게 s1/s2 접두사를 쓴다.
   examples 필드에 적은 수와 실제 파일 수가 어긋나면 카드가 거짓말을 한다. */
for (const item of supplements) {
    const dir = join(ROOT, "examples", "java", `s${item.order}-${item.id}`);
    if (!existsSync(dir)) {
        fail(`보충 자료 Java 예제 폴더 없음: examples/java/s${item.order}-${item.id}`);
        continue;
    }
    const javaFiles = readdirSync(dir).filter(f => f.endsWith(".java"));
    if (javaFiles.length !== item.examples) {
        fail(`examples/java/s${item.order}-${item.id}: .java 파일이 ${javaFiles.length}개인데 ` +
            `데이터에는 examples: ${item.examples}로 적혀 있음`);
    }
}

/* ---------- 결과 ---------- */
console.log(`검사한 강의: ${lessons.length}개, 보충 자료: ${supplements.length}개`);
if (warnings.length) {
    console.log(`\n경고 ${warnings.length}건:`);
    for (const w of warnings) console.log("  [warn] " + w);
}
if (errors.length) {
    console.error(`\n오류 ${errors.length}건:`);
    for (const e of errors) console.error("  [FAIL] " + e);
    process.exit(1);
}
console.log("\n모든 검증 통과 ✓");
