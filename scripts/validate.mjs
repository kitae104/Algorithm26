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

/* ---------- 2. 강의 HTML 구조 ---------- */
const REQUIRED_SECTIONS = [
    "sec-intro", "sec-objectives", "sec-prereq", "sec-problem",
    "sec-hand", "sec-concepts", "sec-steps", "sec-pseudo", "sec-impl",
    "sec-complete", "sec-trace", "sec-bugs", "sec-complexity", "sec-practice",
    "sec-application", "sec-final", "sec-answer", "sec-quiz", "sec-summary", "sec-next"
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
const DEAD_SELECTORS = [".inline-array", ".lesson-hero__actions", ".print-button"];
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
   등장하는 형태(async function / async ( / await <식>)로 좁힌다. */
const jsDir = join(ROOT, "assets/js");
for (const file of readdirSync(jsDir)) {
    if (!file.endsWith(".js")) continue;
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
    for (const marker of ["hero-viz-bars", "course-grid", "filter-category", "filter-difficulty", "course-search", "overall-progress-fill"]) {
        if (!html.includes(marker)) fail(`index.html: 필수 요소 누락 — ${marker}`);
    }
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
}

/* ---------- 결과 ---------- */
console.log(`검사한 강의: ${lessons.length}개`);
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
