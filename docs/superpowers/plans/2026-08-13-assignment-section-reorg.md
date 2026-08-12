# 프로그램 실습 과제 섹션 재구성 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 13개 강의 페이지에서 "따라 하기 실습"과 "최종 프로그램 작성 문제"(+ 정답 해설)를 "프로그램 실습 과제"로 재구성해 확인 퀴즈 뒤로 옮기고, 과제 전용 색상으로 시각적으로 구분한다.

**Architecture:** 13개 HTML 파일이 동일한 섹션 마커(`<!-- N. 제목 -->`, `id="sec-*"`, `<span class="section-no">`)를 공유하므로, 하나의 Node 마이그레이션 스크립트로 문자열 슬라이싱 기반 재배치를 수행한다. 색상은 기존 CSS 커스텀 프로퍼티 패턴(`--brand`류)을 그대로 따라 `--assignment`/`--assignment-bg` 토큰을 추가한다. 검증은 이 저장소의 유일한 테스트인 `node scripts/validate.mjs`로 한다.

**Tech Stack:** 순수 HTML/CSS/Vanilla JS(ES5, 브라우저 코드), Node.js(ESM, `scripts/`용 빌드·검증 도구). 프레임워크 없음.

## Global Constraints

- `data-locked-until-complete` 정답 잠금 동작은 100% 그대로 유지한다 (스펙: 위치만 이동, 로직 불변).
- `sec-application`(실제 데이터 응용 예제), `sec-modernize`(람다·스트림 수정 문제, 존재하는 강의만)는 현재 위치 그대로 — 손대지 않는다.
- 브라우저에 실리는 JS는 ES5 문법만 허용 (`scripts/validate.mjs`가 강제). 이번 작업은 브라우저 JS를 건드리지 않으므로 해당 없음이지만, CSS/HTML만 수정한다는 원칙 유지.
- `:root`에 새로 추가하는 모든 색 토큰은 `assets/css/print.css`의 `@media print` 블록에도 재정의해야 한다 (`scripts/validate.mjs`가 검사).
- 새 색 토큰 값 (스펙 확정값):
  - 라이트: `--assignment: #6d28d9`, `--assignment-bg: #efe7fe`
  - 다크: `--assignment: #c4a3ff`, `--assignment-bg: #2c2350`
  - 인쇄: `--assignment: #4c1d95`, `--assignment-bg: #efe7fe`
- 최종 커밋 후 `git push`로 배포하되, push 직전 사용자에게 재확인한다.
- 참고 스펙: `docs/superpowers/specs/2026-08-13-assignment-section-reorg-design.md`

---

### Task 1: 과제 색상 토큰 추가 (`assets/css/common.css`, `assets/css/print.css`)

**Files:**
- Modify: `assets/css/common.css:11-113` (`:root` 기본 블록), `:117-163`(다크 미디어 쿼리), `:165-209`(`[data-theme="light"]`), `:211-255`(`[data-theme="dark"]`)
- Modify: `assets/css/print.css:19-76` (`@media print` 블록)
- Test: `node scripts/validate.mjs` (색 토큰 인쇄 재정의 검사)

**Interfaces:**
- Produces: CSS 커스텀 프로퍼티 `--assignment`, `--assignment-bg` — Task 2가 이 토큰을 소비한다.

- [ ] **Step 1: 토큰 추가 전 검증 실행 (기준선 확인)**

Run: `node scripts/validate.mjs`
Expected: 현재 통과 상태(에러 0). 이후 단계와 비교할 기준선을 남긴다.

- [ ] **Step 2: `common.css`의 4개 블록에 토큰 추가**

`:root` 블록(11번째 줄 근처, `--state-visit-bg: #e7ecfd;` 다음 줄)에 추가:

```css
    /* 프로그램 실습 과제(따라 하기 실습·최종 프로그램 문제) 전용 액센트 —
       확인 퀴즈의 브랜드 블루와 구분되는 보라 계열 */
    --assignment: #6d28d9;
    --assignment-bg: #efe7fe;
```

`@media (prefers-color-scheme: dark) { :root { ... } }` 블록(`--state-visit-bg: #202c52;` 다음 줄)에 추가:

```css
        --assignment: #c4a3ff;
        --assignment-bg: #2c2350;
```

`html[data-theme="light"] { ... }` 블록(`--state-visit-bg: #e7ecfd;` 다음 줄)에 추가:

```css
    --assignment: #6d28d9;
    --assignment-bg: #efe7fe;
```

`html[data-theme="dark"] { ... }` 블록(`--state-visit-bg: #202c52;` 다음 줄)에 추가:

```css
    --assignment: #c4a3ff;
    --assignment-bg: #2c2350;
```

- [ ] **Step 3: `print.css`에 인쇄용 재정의 추가**

`@media print { :root, html[data-theme] { ... } }` 블록의 `--state-visit-bg: #e8ecf8;` 다음 줄에 추가:

```css
        --assignment: #4c1d95;
        --assignment-bg: #efe7fe;
```

- [ ] **Step 4: 검증 실행**

Run: `node scripts/validate.mjs`
Expected: 에러 0 (print.css 색 토큰 검사가 새 토큰 2개를 찾아 통과).

- [ ] **Step 5: Commit**

```bash
git add assets/css/common.css assets/css/print.css
git commit -m "style: 프로그램 실습 과제 전용 색 토큰(--assignment) 추가"
```

---

### Task 2: 과제 섹션·배너 CSS 작성 (`assets/css/lesson.css`)

**Files:**
- Modify: `assets/css/lesson.css` (섹션 466행 `.lesson-section > h2 .section-no` 규칙 근처에 추가)
- Test: 수동 grep 확인 (자동 테스트 없음 — 순수 시각 스타일)

**Interfaces:**
- Consumes: Task 1의 `--assignment`, `--assignment-bg` 토큰.
- Produces: CSS 클래스 `.lesson-section--assignment`, `.assignment-banner`, `.assignment-banner__title` — Task 4(마이그레이션 스크립트)가 HTML에 부여하는 클래스/마크업과 이름이 정확히 일치해야 한다.

- [ ] **Step 1: `.lesson-section--assignment` 규칙 추가**

`assets/css/lesson.css`의 `.lesson-section > h2 .section-no { ... }` 규칙(466~475행) 바로 다음에 추가:

```css
/* ---------- 프로그램 실습 과제 (확인 퀴즈 뒤, 따라 하기 실습 + 최종 프로그램 문제) ---------- */
.lesson-section--assignment {
    border-left: 4px solid var(--assignment);
}

.lesson-section--assignment > h2 .section-no {
    color: var(--assignment);
    background: var(--assignment-bg);
}

.assignment-banner {
    position: relative;
    border-radius: var(--radius-m);
    border: 1px solid var(--line);
    border-left: 4px solid var(--assignment);
    background: var(--assignment-bg);
    padding: 16px 20px;
    margin-bottom: 20px;
}

.assignment-banner__title {
    display: block;
    font-weight: 800;
    font-size: 1.05rem;
    color: var(--assignment);
    margin-bottom: 6px;
}

.assignment-banner p {
    margin: 0;
    color: var(--ink-soft);
}
```

- [ ] **Step 2: 클래스 존재 확인**

Run: `node -e "const c=require('fs').readFileSync('assets/css/lesson.css','utf8'); if(!c.includes('.lesson-section--assignment')||!c.includes('.assignment-banner')) { console.error('missing classes'); process.exit(1);} console.log('ok')"`
Expected: `ok` 출력.

- [ ] **Step 3: Commit**

```bash
git add assets/css/lesson.css
git commit -m "style: 프로그램 실습 과제 섹션·안내 배너 스타일 추가"
```

---

### Task 3: 마이그레이션 스크립트 작성 (`scripts/scratch-restructure-assignment.mjs`, 저장소에 커밋하지 않는 1회성 도구)

**Files:**
- Create (스크래치 패드, 커밋 대상 아님): `C:\Users\PC\AppData\Local\Temp\claude\D--Github-Algorithm-WS-Algorithm26\4e4ccbce-d1ed-4f43-a9cd-d139741488f1\scratchpad\restructure-assignment.mjs`

**Interfaces:**
- Consumes: `algorithms/NN-*.html` 원본 파일 (섹션 id `sec-practice`, `sec-application`, `sec-final`, `sec-answer`, `sec-quiz` 필수 존재, `sec-modernize`는 선택).
- Produces: 같은 경로에 재작성된 HTML. 부작용으로 파일을 직접 덮어쓴다.

- [ ] **Step 1: 스크립트 작성**

```js
import { readFileSync, writeFileSync } from "node:fs";
import { resolve } from "node:path";

const ROOT = resolve(process.argv[2] ?? ".");
const files = process.argv.slice(3);
if (files.length === 0) {
    console.error("usage: node restructure-assignment.mjs <repo-root> <file1.html> [file2.html ...]");
    process.exit(1);
}

function extractSection(html, id) {
    const openTagRe = new RegExp(`<section class="lesson-section[^"]*" id="${id}"[^>]*>`);
    const openMatch = openTagRe.exec(html);
    if (!openMatch) throw new Error(`section not found: ${id}`);
    const sectionStart = openMatch.index;

    const before = html.slice(0, sectionStart);
    const commentRe = /<!--\s*\d+\.[^>]*-->\n[ \t]*$/;
    const commentMatch = commentRe.exec(before);
    if (!commentMatch) throw new Error(`leading comment not found before section: ${id}`);
    const blockStart = commentMatch.index;

    const closeRe = /<\/section>/;
    const afterOpen = html.slice(sectionStart);
    const closeMatch = closeRe.exec(afterOpen);
    if (!closeMatch) throw new Error(`closing </section> not found: ${id}`);
    const blockEnd = sectionStart + closeMatch.index + "</section>".length;

    const afterClose = html.slice(blockEnd);
    const trailingBlank = /^\n+/.exec(afterClose);
    const fullEnd = blockEnd + (trailingBlank ? trailingBlank[0].length : 0);

    return { blockStart, blockEnd, fullEnd, text: html.slice(blockStart, blockEnd) };
}

function addAssignmentClass(sectionText, id) {
    const needle = `<section class="lesson-section" id="${id}"`;
    if (!sectionText.includes(needle)) throw new Error(`unexpected section tag shape for ${id}`);
    return sectionText.replace(needle, `<section class="lesson-section lesson-section--assignment" id="${id}"`);
}

function mergeAnswerIntoFinal(finalText, answerText) {
    const detailsRe = /<details class="answer-box" data-locked-until-complete>[\s\S]*<\/details>/;
    const detailsMatch = detailsRe.exec(answerText);
    if (!detailsMatch) throw new Error("answer-box details not found in sec-answer");
    const answerDetails = detailsMatch[0];

    const closeRe = /\n( {12})<\/section>\s*$/;
    const closeMatch = closeRe.exec(finalText);
    if (!closeMatch) throw new Error("could not locate closing </section> in sec-final block");
    const indent = closeMatch[1];
    const contentIndent = indent + "    ";

    const appendix =
        `\n${contentIndent}<h3>정답과 해설</h3>\n` +
        `${contentIndent}<p>스스로 충분히 시도한 뒤에 여세요. 막혔다면 "구현 단계 안내"를 다시 읽고, ` +
        `한 단계씩 진행해 보세요.\n${contentIndent}이 강의를 완료로 표시해야 정답이 열립니다.</p>\n\n` +
        `${contentIndent}${answerDetails}\n`;

    return finalText.slice(0, closeMatch.index) + appendix + closeMatch[0];
}

function buildBanner(indent) {
    return (
        `${indent}<div class="assignment-banner">\n` +
        `${indent}    <span class="assignment-banner__title">📝 프로그램 실습 과제</span>\n` +
        `${indent}    <p>지금까지 배운 내용을 직접 코드로 적용해 보는 과제입니다. ` +
        `두 과제로 구성되며, 각 과제의 정답은 이 강의를 완료로 표시해야 열립니다.</p>\n` +
        `${indent}</div>`
    );
}

function renumberSections(html) {
    const pattern =
        /(<!--\s*)(\d+)(\.[^\n]*-->\n\s*<section class="lesson-section[^"]*"[^>]*>\s*\n\s*<h2[^>]*><span class="section-no">)(\d+)(<\/span>)/g;
    let n = 0;
    return html.replace(pattern, (full, p1, oldCommentNum, mid, oldBadge, p5) => {
        n += 1;
        const badge = String(n).padStart(2, "0");
        return `${p1}${n}${mid}${badge}${p5}`;
    });
}

function restructure(html) {
    const practice = extractSection(html, "sec-practice");
    const final = extractSection(html, "sec-final");
    const answer = extractSection(html, "sec-answer");

    let finalText = mergeAnswerIntoFinal(final.text, answer.text);
    finalText = addAssignmentClass(finalText, "sec-final");
    const practiceText = addAssignmentClass(practice.text, "sec-practice");

    const indent = "            "; // 12 spaces, matches existing section indentation
    const banner = buildBanner(indent);

    // Remove practice/final/answer blocks from their original location (descending order to keep offsets valid).
    let out = html;
    for (const r of [practice, final, answer].sort((a, b) => b.blockStart - a.blockStart)) {
        out = out.slice(0, r.blockStart) + out.slice(r.fullEnd);
    }

    // Re-locate sec-quiz in the trimmed string and insert the assignment group right after it.
    const quiz = extractSection(out, "sec-quiz");
    const insertion = [banner, practiceText, finalText].join("\n\n") + "\n\n";
    out = out.slice(0, quiz.fullEnd) + insertion + out.slice(quiz.fullEnd);

    out = renumberSections(out);
    return out;
}

for (const rel of files) {
    const path = resolve(ROOT, rel);
    const original = readFileSync(path, "utf8");
    const updated = restructure(original);
    writeFileSync(path, updated, "utf8");
    console.log(`rewrote ${rel}`);
}
```

- [ ] **Step 2: 파일 위치 확인**

Run: `ls "C:\Users\PC\AppData\Local\Temp\claude\D--Github-Algorithm-WS-Algorithm26\4e4ccbce-d1ed-4f43-a9cd-d139741488f1\scratchpad\"`
Expected: `restructure-assignment.mjs` 존재.

이 스크립트는 저장소에 커밋하지 않는다 (1회성 마이그레이션 도구). 커밋 단계 없음 — Task 4로 진행.

---

### Task 4: 파일 1개(02강)에 시험 적용 후 수동 검수

**Files:**
- Modify: `algorithms/02-arrays-and-lists.html` (스크립트가 덮어씀)
- Test: 육안 diff 검수 + `node scripts/validate.mjs`(REQUIRED_SECTIONS는 아직 Task 6에서 갱신 전이므로 `sec-answer` 관련 실패는 없고, 순서 경고만 참고용으로 확인)

02강을 파일럿으로 고른 이유: `sec-modernize`를 포함하는 6개 강의 중 하나라 가장 복잡한 케이스를 먼저 검증할 수 있다.

- [ ] **Step 1: git으로 원본 스냅샷 확보 (되돌릴 수 있도록)**

Run: `git status --porcelain algorithms/02-arrays-and-lists.html`
Expected: 빈 출력(깨끗한 워킹트리 — 문제 생기면 `git checkout -- algorithms/02-arrays-and-lists.html`로 되돌릴 수 있음을 확인).

- [ ] **Step 2: 스크립트를 02강에 적용**

Run (저장소 루트에서):
```bash
node "C:\Users\PC\AppData\Local\Temp\claude\D--Github-Algorithm-WS-Algorithm26\4e4ccbce-d1ed-4f43-a9cd-d139741488f1\scratchpad\restructure-assignment.mjs" . algorithms/02-arrays-and-lists.html
```
Expected: `rewrote algorithms/02-arrays-and-lists.html` 출력, 에러 없이 종료.

- [ ] **Step 3: diff 육안 검수**

Run: `git diff algorithms/02-arrays-and-lists.html`
확인할 것:
- `sec-quiz`의 `</section>` 바로 뒤에 `assignment-banner` → `sec-practice`(class에 `lesson-section--assignment` 포함) → `sec-final`(마찬가지, 끝에 "정답과 해설" h3 + 잠금 details 포함) 순서로 삽입됐는가.
- 원래 위치(`sec-complexity` 다음)에서 `sec-practice`/`sec-final`/`sec-answer`가 사라지고, `sec-application`이 `sec-complexity` 바로 다음으로 붙었는가.
- `id="sec-answer"`가 파일에서 완전히 사라졌는가 (`grep -c 'id="sec-answer"' algorithms/02-arrays-and-lists.html` → 0).
- `<span class="section-no">` 값들이 새 DOM 순서로 01부터 빠짐없이 연속인가 (`grep -o 'class="section-no">[0-9]*' algorithms/02-arrays-and-lists.html`로 나열해 확인).
- `GradeAnalyzerSolution.java` 코드와 `code-solution`/`out-solution` id, `data-copy-target="code-solution"` 등 손실 없이 그대로 옮겨졌는가.
- HTML이 깨지지 않았는가 (닫는 태그 짝 확인 — 간단히는 `</section>` 개수가 원본과 동일한지 `git diff --stat`이 아니라 `grep -c '</section>' algorithms/02-arrays-and-lists.html`로 전/후 비교. 섹션 개수가 20→19로 줄었으므로 `</section>` 개수도 정확히 1개 줄어야 함).

문제가 있으면 `git checkout -- algorithms/02-arrays-and-lists.html`로 되돌리고 Task 3의 스크립트를 수정한 뒤 이 Task를 재시도한다.

- [ ] **Step 4: 문제없으면 커밋하지 않고 다음 태스크로 (13개 파일을 한 번에 검증 후 일괄 커밋)**

02강 결과가 정상이면 나머지 12개 파일에도 같은 스크립트를 적용할 준비가 된 것이다.

---

### Task 5: 나머지 12개 파일에 적용 + 전체 육안 스팟체크

**Files:**
- Modify: `algorithms/01-algorithm-basics.html`, `03-brute-force-string-hash.html`, `04-sorting-algorithms.html`, `05-search-algorithms.html`, `06-stack-and-queue.html`, `07-recursion-and-backtracking.html`, `08-tree-structures.html`, `09-graph-search.html`, `10-greedy-algorithms.html`, `11-dynamic-programming.html`, `12-shortest-path.html`, `13-algorithm-project.html`

- [ ] **Step 1: 나머지 12개 파일에 스크립트 적용**

Run (저장소 루트에서):
```bash
node "C:\Users\PC\AppData\Local\Temp\claude\D--Github-Algorithm-WS-Algorithm26\4e4ccbce-d1ed-4f43-a9cd-d139741488f1\scratchpad\restructure-assignment.mjs" . \
  algorithms/01-algorithm-basics.html \
  algorithms/03-brute-force-string-hash.html \
  algorithms/04-sorting-algorithms.html \
  algorithms/05-search-algorithms.html \
  algorithms/06-stack-and-queue.html \
  algorithms/07-recursion-and-backtracking.html \
  algorithms/08-tree-structures.html \
  algorithms/09-graph-search.html \
  algorithms/10-greedy-algorithms.html \
  algorithms/11-dynamic-programming.html \
  algorithms/12-shortest-path.html \
  algorithms/13-algorithm-project.html
```
Expected: 12줄의 `rewrote ...` 출력, 에러 없이 종료. 하나라도 에러가 나면(예: 특정 강의만 마커 형식이 다른 경우) 그 파일만 `git checkout --`로 되돌리고 원인을 확인 후 스크립트를 보정한다.

- [ ] **Step 2: 전체 파일에서 `id="sec-answer"` 잔존 여부 확인**

Run: `grep -rl 'id="sec-answer"' algorithms/`
Expected: 출력 없음 (13개 파일 모두 병합 완료).

- [ ] **Step 3: 전체 파일에서 섹션 번호 배지가 파일마다 연속인지 확인**

Run:
```bash
for f in algorithms/*.html; do
  echo "== $f =="
  grep -o 'class="section-no">[0-9]*' "$f" | sed 's/.*>//' 
done
```
Expected: 각 파일마다 `01`부터 시작해 끊김 없이 연속(모더나이즈 있는 파일은 19까지, 없는 파일은 18까지 등 — 파일별 총 섹션 수만큼).

- [ ] **Step 4: `</section>` 개수 검증 (섹션 유실 방지)**

Run: `for f in algorithms/*.html; do echo "$f: $(grep -c '</section>' "$f")"; done`
02강처럼 sec-modernize가 있던 파일(02,03,04,05,10,13)은 원래 21개 섹션 중 하나 병합되어 20개, 없던 파일(01,06,07,08,09,11,12)은 원래 20개 중 19개가 되어야 한다. (직접 22/21에서 각각 1 뺀 값과 비교해 확인.)

- [ ] **Step 5: 스팟체크 — 03강(sec-modernize 있음), 07강(sec-modernize 없음) diff 육안 확인**

Run: `git diff algorithms/03-brute-force-string-hash.html algorithms/07-recursion-and-backtracking.html`
Task 4의 Step 3와 같은 체크리스트로 확인.

---

### Task 6: `scripts/validate.mjs` 갱신 (새 구조 반영)

**Files:**
- Modify: `scripts/validate.mjs:78-83`
- Test: `node scripts/validate.mjs`

**Interfaces:**
- Consumes: Task 5까지 재구성된 13개 HTML 파일.

- [ ] **Step 1: `REQUIRED_SECTIONS` 배열을 새 순서로 교체**

`scripts/validate.mjs:78-83`의 현재 코드:

```js
const REQUIRED_SECTIONS = [
    "sec-intro", "sec-objectives", "sec-prereq", "sec-problem",
    "sec-hand", "sec-concepts", "sec-steps", "sec-pseudo", "sec-impl",
    "sec-complete", "sec-trace", "sec-bugs", "sec-complexity", "sec-practice",
    "sec-application", "sec-final", "sec-answer", "sec-quiz", "sec-summary", "sec-next"
];
```

다음으로 교체:

```js
const REQUIRED_SECTIONS = [
    "sec-intro", "sec-objectives", "sec-prereq", "sec-problem",
    "sec-hand", "sec-concepts", "sec-steps", "sec-pseudo", "sec-impl",
    "sec-complete", "sec-trace", "sec-bugs", "sec-complexity",
    "sec-application", "sec-quiz", "sec-practice", "sec-final",
    "sec-summary", "sec-next"
];
```

(`sec-answer`는 `sec-final`에 병합되어 더 이상 별도 필수 섹션이 아니다. `sec-modernize`는 원래도 `REQUIRED_SECTIONS`에 없었고 별도 검사(6번 섹션, `MODERNIZE_LESSONS`)로 다뤄지므로 그대로 둔다.)

- [ ] **Step 2: 검증 실행**

Run: `node scripts/validate.mjs`
Expected: 에러 0, 경고 0 (섹션 순서 경고 포함). 만약 순서 경고가 남아 있다면 Task 5의 재배치 결과가 예상 순서와 다르다는 뜻이므로 해당 파일을 다시 확인한다.

- [ ] **Step 3: Commit (HTML 13개 파일 + validate.mjs 함께)**

```bash
git add algorithms/*.html scripts/validate.mjs
git commit -m "$(cat <<'EOF'
feat: 프로그램 실습 과제(따라 하기 실습·최종 프로그램 문제)를 확인 퀴즈 뒤로 재배치

정답이 잠긴 실습 콘텐츠를 "프로그램 실습 과제"로 묶어 확인 퀴즈 다음으로
옮기고, 최종 문제와 정답 해설 섹션을 하나로 합쳤다. 정답 잠금 동작은
그대로 유지된다.

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 7: 문서 갱신 (`docs/adding-lessons.md`, `docs/curriculum.md`)

**Files:**
- Modify: `docs/adding-lessons.md:31-59`
- Modify: `docs/curriculum.md:55-69`
- Test: 없음(문서) — 육안 검수만.

- [ ] **Step 1: `docs/adding-lessons.md` — "20개 섹션" → "19개 섹션" 및 표 갱신**

`docs/adding-lessons.md:31`의 `- \`<div class="lesson-body">\` 안에 20개 섹션`을
`- \`<div class="lesson-body">\` 안에 19개 섹션`으로 교체.

`docs/adding-lessons.md:34`의 `### 20개 섹션 (id와 순서 고정)`을
`### 19개 섹션 (id와 순서 고정)`으로 교체.

`docs/adding-lessons.md:38-59`의 표 전체를 다음으로 교체:

```markdown
| # | id | 내용 |
|---|----|------|
| 1 | `sec-intro` | 강의 소개 (문단 2개, 표 없음) |
| 2 | `sec-objectives` | 학습 목표 4~6개 (`ul.objective-list`, 행동 동사로) |
| 3 | `sec-prereq` | 선수 지식 표 |
| 4 | `sec-problem` | 실제 문제 상황 (3~6문장 스토리) |
| 5 | `sec-hand` | 사람이 직접 해결 (`div.hand-step` + 질문은 `details.reveal-box`로 접기) |
| 6 | `sec-concepts` | 핵심 개념 |
| 7 | `sec-steps` | 단계별 동작 과정 — AlgoViz 시각화 마운트 `<div id="viz-..."></div>` |
| 8 | `sec-pseudo` | 의사코드 (code-card, 언어 배지 `Pseudo`) |
| 9 | `sec-impl` | 점진적 Java 구현 (구현 1~4단계, 각 단계 code-card + 출력 card) |
| 10 | `sec-complete` | 완성 Java 코드 + 실행 결과 |
| 11 | `sec-trace` | 실행 과정 추적 코드 + 실행 결과 |
| 12 | `sec-bugs` | 잘못된 코드 2개 이상 (`article.bug-card`, 해설은 `details.answer-box`) |
| 13 | `sec-complexity` | 복잡도 및 특성 (입력 크기·주요 연산·시간·공간·특성) |
| 14 | `sec-application` | 실제 데이터 응용 예제 (문제 상황→데이터 모델→전체 코드→출력→해설→복잡도) |
| — | `sec-modernize` | (2·3·4·5·10·13강만) 람다·스트림 수정 문제 |
| 15 | `sec-quiz` | `<div id="quiz-root"></div>` 만 두고 인라인 스크립트에서 초기화 |
| 16 | `sec-practice` | **프로그램 실습 과제 ①** — 따라 하기 실습 2~3개 (실습3은 TODO 코드 + `details.answer-box` 정답). 앞에 `.assignment-banner` 안내 배너, `lesson-section--assignment` 클래스로 확인 퀴즈와 색상 구분 |
| 17 | `sec-final` | **프로그램 실습 과제 ②** — 최종 프로그램 작성 문제(제목·배경·목표·필수 요구사항·입력·예상 출력·제한·구현 단계 안내·시작 코드·테스트 3종 표·자기 점검표 `ul.checklist`·추가 도전) + 섹션 끝에 정답과 해설(`details.answer-box`, 정답 코드·예상 출력·구현 순서·핵심 적용부·복잡도·자주 나는 오류·다른 방법). `lesson-section--assignment` 클래스 |
| 18 | `sec-summary` | 오늘의 핵심 정리 + `<div id="lesson-complete-slot"></div>` |
| 19 | `sec-next` | 다음 강의 연결 + `<div id="lesson-pager"></div>` |
```

(번호 열의 `sec-modernize`는 있는 강의에서만 15번 앞에 끼어들어 전체 번호가 하나씩 밀린다 — 다른 19개 섹션과 달리 조건부이므로 `—`로 표시.)

- [ ] **Step 2: `docs/curriculum.md` — 섹션 순서 설명 갱신**

`docs/curriculum.md:55-61`의 현재 내용:

```markdown
## 강의 공통 구성 (20개 섹션)

모든 강의 페이지는 같은 20개 섹션으로 구성된다:

... → 실행 과정 추적 → 오류 찾기 → 복잡도 분석 → 따라 하기 실습 → 실제 데이터 응용
→ 최종 프로그램 문제 → 정답과 해설 → 확인 퀴즈 → 핵심 정리 → 다음 강의 연결.
```

다음으로 교체:

```markdown
## 강의 공통 구성 (19개 섹션)

모든 강의 페이지는 같은 19개 섹션으로 구성된다:

... → 실행 과정 추적 → 오류 찾기 → 복잡도 분석 → 실제 데이터 응용 → 확인 퀴즈 →
프로그램 실습 과제(따라 하기 실습 → 최종 프로그램 문제 + 정답과 해설) → 핵심 정리 →
다음 강의 연결.

프로그램 실습 과제는 확인 퀴즈 뒤에 배치되어 "이론 학습 → 응용 감상 → 자기 점검(퀴즈) →
직접 실습(과제)" 순서를 이룬다. 정답은 강의를 완료로 표시해야 열린다.
```

`docs/curriculum.md:65-69`의 흐름 설명 중 `5. 실습 → 최종 프로그램 문제를 직접 구현한다 (정답은 막힌 뒤에만 연다).`처럼 옛 순서를 전제한 문장이 있으면 새 순서("퀴즈 이후 과제")에 맞게 고친다.

- [ ] **Step 3: Commit**

```bash
git add docs/adding-lessons.md docs/curriculum.md
git commit -m "docs: 19개 섹션 구조·프로그램 실습 과제 배치 반영"
```

---

### Task 8: 브라우저 수동 검증

**Files:** 없음 (검증만)

- [ ] **Step 1: 로컬 서버 기동**

Run: `npm run dev` (백그라운드로 실행 — `npx serve . -l 4173`)
Expected: `http://localhost:4173` 에서 서빙 시작.

- [ ] **Step 2: 02강(sec-modernize 있음) 확인**

`http://localhost:4173/algorithms/02-arrays-and-lists.html` 접속 후:
- 좌측 목차 순서가 `... 복잡도 분석 → 실제 데이터 응용 예제 → 람다·스트림 수정 문제 → 확인 퀴즈 → 따라 하기 실습 → 최종 프로그램 작성 문제 → 오늘의 핵심 정리 → 다음 강의 연결` 인지, 번호가 01~19로 끊김 없는지.
- "따라 하기 실습" 섹션 진입 직전에 보라색 배너("📝 프로그램 실습 과제")가 보이는지.
- "따라 하기 실습"·"최종 프로그램 작성 문제" 두 섹션 좌측에 보라색 강조선과 보라색 번호 배지가 보이는지 (확인 퀴즈는 기존 파랑 그대로인지 비교).
- 완료 버튼을 누르기 전, 실습 3번 정답 상자와 최종 문제 정답 상자가 "🔒 이 강의를 완료로 표시하면 정답을 확인할 수 있습니다"로 잠겨 있는지.
- "이 강의를 완료로 표시" 버튼 클릭 후, 두 정답 상자가 열람 가능해지는지(클릭하면 펼쳐지는지).
- 라이트/다크 테마 토글로 색상이 양쪽 다 자연스러운지.

- [ ] **Step 3: 07강(sec-modernize 없음) 확인**

`http://localhost:4173/algorithms/07-recursion-and-backtracking.html`에서 Step 2와 동일한 항목을 확인(람다·스트림 섹션이 없다는 점만 다름).

- [ ] **Step 4: 서버 종료**

로컬 서버 프로세스를 종료한다.

---

### Task 9: 최종 확인 및 배포

**Files:** 없음

- [ ] **Step 1: 전체 검증 재실행**

Run: `node scripts/validate.mjs`
Expected: 에러 0.

- [ ] **Step 2: 작업 트리 상태 확인**

Run: `git status`
Expected: Task 1·2·6·7에서 이미 커밋했으므로 클린 상태(스크래치 패드 스크립트는 저장소 밖이라 나타나지 않음).

- [ ] **Step 3: 사용자에게 push 여부 재확인 후 배포**

사용자 확인을 받은 뒤:

```bash
git push origin main
```

Expected: 원격 `main`에 반영, Vercel이 자동으로 정적 배포를 시작한다(별도 빌드 단계 없음 — `vercel.json`의 `buildCommand: null`).
