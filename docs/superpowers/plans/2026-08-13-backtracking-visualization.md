# 7강 백트래킹 이해 보조 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `algorithms/07-recursion-and-backtracking.html`의 "핵심 개념" 섹션에 선택·진행·취소를
보여주는 자동 재생 미니 다이어그램을 추가하고, "단계별 동작 과정" 섹션의 부분집합 백트래킹
시각화를 개선(누적 발견 목록 + 결정 트리 시각화 추가)한다.

**Architecture:** 기존 `AlgoViz` 단계 기반 시각화 엔진(`assets/js/visualization.js`)은 수정하지
않고 그대로 재사용한다(`graph` 뷰 타입으로 결정 트리를 그린다). 핵심 개념의 미니 다이어그램은
엔진 밖의 순수 SVG + CSS `@keyframes` 컴포넌트로, 기존 `.lesson-hero__motif` 재생 제어
(IntersectionObserver로 뷰포트 진입 시에만 재생) 패턴을 그대로 따른다.

**Tech Stack:** 순수 HTML/CSS/ES5 자바스크립트, 빌드 스텝 없음. 검증은 `node scripts/validate.mjs`.

## Global Constraints

- 브라우저에서 실행되는 모든 JS(`assets/js/*.js` 및 강의 페이지 인라인 스크립트)는 ES5
  문법만 사용한다 — `var`만 쓰고 `let`/`const`/화살표 함수/템플릿 리터럴 금지(기존 코드 전체가
  이 스타일이다).
- 애니메이션은 `transform`/`opacity`/`fill`/`stroke`만 사용한다(레이아웃 속성 애니메이션 금지).
- 장식용 시각 요소는 `aria-hidden="true"`로 표시하고 `prefers-reduced-motion: reduce`에서
  정지 프레임으로 고정하며, `assets/css/print.css`의 장식 요소 숨김 목록에 추가한다.
- 이 작업은 `algorithms/07-recursion-and-backtracking.html`, `assets/css/lesson.css`,
  `assets/css/print.css`, `assets/js/common.js` 4개 파일만 건드린다. 다른 12개 강의 페이지,
  `assets/js/visualization.js`(엔진 자체)는 수정하지 않는다.
- 이 저장소는 `main`에 직접 커밋하는 1인 유지보수 워크플로다(브랜치/PR 없음). 작업 단위마다
  커밋하고, 마지막 검증까지 끝난 뒤 `origin/main`에 push한다.

---

## Task 1: 핵심 개념 미니 다이어그램 (선택·진행·취소)

**Files:**
- Modify: `algorithms/07-recursion-and-backtracking.html:260-261` (섹션 06, `<figure>` 삽입)
- Modify: `assets/css/lesson.css:363` (신규 CSS 블록 삽입, "모티프 시각 문법" 섹션 끝난 직후)
- Modify: `assets/css/print.css:98` (`.concept-loop`를 장식 요소 숨김 목록에 추가)
- Modify: `assets/js/common.js:800` (재생 제어 IIFE 추가, 히어로 모티프 IIFE 바로 다음)

**Interfaces:**
- Consumes: 없음(독립 컴포넌트). 기존 CSS 토큰 `--state-visit`, `--state-visit-bg`,
  `--ink-faint`, `--surface-2`, `--line-strong`, `--surface`, `--line`, `--radius-m`,
  `--font-mono`, `--ease-in-out`만 사용한다.
- Produces: `.concept-loop` 클래스(다른 강의에서도 같은 패턴으로 재사용 가능하지만 지금은
  7강에만 마운트한다). `assets/js/common.js`에 모든 페이지에서 실행되는 제네릭 재생 제어
  블록이 생기지만, `.concept-loop` 요소가 없는 페이지에서는 즉시 반환되어 아무 영향이 없다.

- [ ] **Step 1: 섹션 06에 다이어그램 마크업 삽입**

`algorithms/07-recursion-and-backtracking.html`에서 아래 블록(원래 260번째 줄
`</p>` 바로 다음, 261번째 줄 `<div class="table-scroll">` 바로 앞)에 삽입:

```html
                <figure class="concept-loop" aria-hidden="true">
                    <svg class="concept-loop__svg" viewBox="0 0 320 130" role="presentation">
                        <rect class="concept-loop__bracket" x="212" y="28" width="78" height="58" rx="10"></rect>
                        <text class="concept-loop__bracket-label" x="251" y="20">chosen</text>
                        <g class="concept-loop__token cl-anim-move">
                            <circle class="concept-loop__token-circle cl-anim-fill" cx="45" cy="57" r="16"></circle>
                            <text class="concept-loop__token-label" x="45" y="57">x</text>
                        </g>
                        <text class="concept-loop__frame-label cl-anim-frame" x="251" y="112">↓ 재귀 호출</text>
                    </svg>
                    <div class="concept-loop__steps">
                        <span class="concept-loop__step concept-loop__step--1 cl-anim-step1">① 선택</span>
                        <span class="concept-loop__step concept-loop__step--2 cl-anim-step2">② 진행</span>
                        <span class="concept-loop__step concept-loop__step--3 cl-anim-step3">③ 취소</span>
                    </div>
                    <figcaption class="concept-loop__caption">
                        원소 x를 chosen에 넣었다가(선택), 재귀 호출로 내려갔다가(진행),
                        되돌아오며 다시 빼는(취소) 한 사이클이 반복 재생됩니다.
                    </figcaption>
                </figure>
```

- [ ] **Step 2: CSS 컴포넌트 + 키프레임 추가**

`assets/css/lesson.css`의 363번째 줄(`.lesson-hero__motif .m-arrow-head { fill: var(--brand); }`
규칙이 끝나는 지점, `/* ---------- 내부 목차 (사이드바) ---------- */` 주석 바로 앞)에 삽입:

```css
/* ---------- 선택·진행·취소 미니 다이어그램 (7강 핵심 개념) ----------
   장식용 자동 재생 루프. transform/opacity/fill/stroke만 애니메이션한다.
   재생 제어(뷰포트 진입 시에만 재생)는 common.js가 담당한다 — 위 히어로
   모티프와 같은 근거(본문을 읽는 동안 무관한 곳에서 움직이지 않게). */
.concept-loop {
    margin: 18px 0 24px;
    padding: 16px;
    border: 1px solid var(--line);
    border-radius: var(--radius-m);
    background: var(--surface);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.concept-loop__svg {
    width: 100%;
    max-width: 340px;
    height: auto;
}

.concept-loop__bracket {
    fill: var(--surface-2);
    stroke: var(--line-strong);
    stroke-width: 1.5;
}

.concept-loop__bracket-label {
    font-family: var(--font-mono);
    font-size: 11px;
    fill: var(--ink-faint);
    text-anchor: middle;
}

.concept-loop__token-circle {
    stroke-width: 2;
}

.concept-loop__token-label {
    font-family: var(--font-mono);
    font-size: 13px;
    font-weight: 700;
    text-anchor: middle;
    dominant-baseline: central;
    fill: var(--ink);
}

.concept-loop__frame-label {
    font-family: var(--font-mono);
    font-size: 11px;
    font-weight: 700;
    fill: var(--state-visit);
    text-anchor: middle;
    opacity: 0;
}

.concept-loop__steps {
    display: flex;
    gap: 10px;
    font-family: var(--font-mono);
    font-size: 0.82rem;
}

.concept-loop__step {
    font-weight: 700;
    color: var(--ink-faint);
    opacity: 0.55;
}

.concept-loop__caption {
    margin: 0;
    font-size: 0.8rem;
    color: var(--ink-faint);
    text-align: center;
}

/* 재생 제어 — common.js가 뷰포트 진입 시 .is-playing을 토글한다.
   기본은 정지라서 스크롤해서 보기 전에는 아무것도 움직이지 않는다. */
.concept-loop [class*="cl-anim"] {
    animation-duration: 6s;
    animation-iteration-count: infinite;
    animation-timing-function: var(--ease-in-out);
    animation-play-state: paused;
}

.concept-loop.is-playing [class*="cl-anim"] {
    animation-play-state: running;
}

@media (prefers-reduced-motion: reduce) {
    .concept-loop [class*="cl-anim"] {
        animation: none !important;
    }
    /* 정지 프레임 = "① 선택" 직후: 토큰이 chosen 안, ①만 밝음 */
    .concept-loop__token {
        transform: translateX(206px);
    }
    .concept-loop__token-circle {
        fill: var(--state-visit-bg);
        stroke: var(--state-visit);
    }
    .concept-loop__step--1 {
        color: var(--state-visit);
        opacity: 1;
    }
}

.concept-loop__token.cl-anim-move { animation-name: cl-token-move; }
.concept-loop__token-circle.cl-anim-fill { animation-name: cl-token-fill; }
.concept-loop__frame-label.cl-anim-frame { animation-name: cl-frame; }
.concept-loop__step--1.cl-anim-step1 { animation-name: cl-step-1; }
.concept-loop__step--2.cl-anim-step2 { animation-name: cl-step-2; }
.concept-loop__step--3.cl-anim-step3 { animation-name: cl-step-3; }

@keyframes cl-token-move {
    0%, 2%    { transform: translateX(0); }
    20%, 53%  { transform: translateX(206px); }
    76%, 100% { transform: translateX(0); }
}

@keyframes cl-token-fill {
    0%, 2%    { fill: var(--surface-2); stroke: var(--line-strong); }
    20%, 53%  { fill: var(--state-visit-bg); stroke: var(--state-visit); }
    76%, 100% { fill: var(--surface-2); stroke: var(--line-strong); }
}

@keyframes cl-frame {
    0%, 20%   { opacity: 0; }
    30%, 45%  { opacity: 1; }
    53%, 100% { opacity: 0; }
}

@keyframes cl-step-1 {
    0%, 20%   { color: var(--state-visit); opacity: 1; }
    22%, 100% { color: var(--ink-faint); opacity: 0.55; }
}

@keyframes cl-step-2 {
    0%, 20%   { color: var(--ink-faint); opacity: 0.55; }
    22%, 53%  { color: var(--state-visit); opacity: 1; }
    55%, 100% { color: var(--ink-faint); opacity: 0.55; }
}

@keyframes cl-step-3 {
    0%, 53%   { color: var(--ink-faint); opacity: 0.55; }
    55%, 76%  { color: var(--state-visit); opacity: 1; }
    78%, 100% { color: var(--ink-faint); opacity: 0.55; }
}
```

- [ ] **Step 3: 인쇄 시 숨김 처리**

`assets/css/print.css`에서 `.hero__actions,` 다음 줄(98번째 줄)에 삽입:

```css
        .concept-loop,
```

(전체 맥락: `.hero__actions,` 다음, 기존 `.hero-viz,` 앞에 새 줄로 추가한다.)

- [ ] **Step 4: 재생 제어 스크립트 추가**

`assets/js/common.js`에서 "히어로 개념 모티프" IIFE가 끝나는 지점(800번째 줄,
`})();` 다음, "내부 목차 자동 생성 + 스크롤스파이" 주석 앞)에 삽입:

```js
        /* ---------- 개념 다이어그램 재생 제어 (.concept-loop) ----------
           히어로 모티프와 같은 근거: 뷰포트에 들어와 있을 때만 재생해
           본문을 읽는 동안 무관한 곳에서 움직이지 않게 한다. */
        (function () {
            var loops = document.querySelectorAll(".concept-loop");
            if (!loops.length) return;
            if ("IntersectionObserver" in window) {
                var loopObserver = new IntersectionObserver(function (entries) {
                    entries.forEach(function (entry) {
                        entry.target.classList.toggle("is-playing", entry.isIntersecting);
                    });
                }, { threshold: 0 });
                Array.prototype.forEach.call(loops, function (loop) {
                    loopObserver.observe(loop);
                });
            } else {
                Array.prototype.forEach.call(loops, function (loop) {
                    loop.classList.add("is-playing");
                });
            }
        })();

```

- [ ] **Step 5: 검증**

Run: `node scripts/validate.mjs`
Expected: `모든 검증 통과 ✓` (섹션 마커나 링크 검사에 새 요소가 걸리지 않아야 한다 — 이
스텝은 구조 검증이며 애니메이션 자체를 확인하지는 않는다).

- [ ] **Step 6: 커밋**

```bash
git add algorithms/07-recursion-and-backtracking.html assets/css/lesson.css assets/css/print.css assets/js/common.js
git commit -m "feat: 7강 핵심 개념에 선택·진행·취소 미니 다이어그램 추가"
```

---

## Task 2: 부분집합 시각화 — 누적 발견 목록

**Files:**
- Modify: `algorithms/07-recursion-and-backtracking.html` (인라인 스크립트, "시각화 2:
  {1,2,3} 부분집합 백트래킹" IIFE 안 `view` 함수 — 현재 소스 기준 약 1851-1880번째 줄,
  `AlgoViz.create({ mount: "#viz-subsets", ...` 직전 블록)

**Interfaces:**
- Consumes: 같은 IIFE 안의 `found`(부분집합 라벨 문자열 배열, 종료 조건에서 `push`됨)
  — 이미 존재하는 클로저 변수, 새로 만들지 않는다.
- Produces: 없음(이 IIFE 밖에서 쓰이지 않는다).

- [ ] **Step 1: `view()` 함수에 누적 목록 그룹 추가**

기존 코드:

```js
                function view(currentIndex, noteText) {
                    var groups = [
                        {
                            type: "array",
                            label: "원소 (index 순서로 넣을지/뺄지 결정)",
                            cells: numbers.map(function (v, i) {
                                var cls = "";
                                if (i < currentIndex) cls = "done";
                                if (i === currentIndex) cls = "current";
                                return { v: v, sub: "i=" + i, cls: cls };
                            })
                        },
                        {
                            type: "stack",
                            label: "chosen — 지금까지의 선택 (공유 상태)",
                            items: chosen.map(function (v) { return { v: v, cls: "current" }; }),
                            emptyText: "(아직 선택 없음)"
                        },
                        {
                            type: "frames",
                            label: "호출 스택",
                            items: frames.map(function (name, i) {
                                return { v: name, cls: i === frames.length - 1 ? "current" : "" };
                            }),
                            emptyText: "(호출 스택이 비어 있음)"
                        }
                    ];
                    if (noteText) groups.push({ type: "note", text: noteText });
                    return groups;
                }
```

이걸 아래로 교체(`frames` 그룹 다음에 새 그룹 하나 추가, 나머지는 동일):

```js
                function view(currentIndex, noteText) {
                    var groups = [
                        {
                            type: "array",
                            label: "원소 (index 순서로 넣을지/뺄지 결정)",
                            cells: numbers.map(function (v, i) {
                                var cls = "";
                                if (i < currentIndex) cls = "done";
                                if (i === currentIndex) cls = "current";
                                return { v: v, sub: "i=" + i, cls: cls };
                            })
                        },
                        {
                            type: "stack",
                            label: "chosen — 지금까지의 선택 (공유 상태)",
                            items: chosen.map(function (v) { return { v: v, cls: "current" }; }),
                            emptyText: "(아직 선택 없음)"
                        },
                        {
                            type: "frames",
                            label: "호출 스택",
                            items: frames.map(function (name, i) {
                                return { v: name, cls: i === frames.length - 1 ? "current" : "" };
                            }),
                            emptyText: "(호출 스택이 비어 있음)"
                        },
                        {
                            type: "array",
                            label: "발견된 부분집합 (지금까지 " + found.length + "개)",
                            cells: found.map(function (label, i) {
                                return { v: label, sub: (i + 1) + "번째", cls: "done" };
                            }),
                            emptyText: "(아직 없음)"
                        }
                    ];
                    if (noteText) groups.push({ type: "note", text: noteText });
                    return groups;
                }
```

- [ ] **Step 2: 논리 검증(수동 추적)**

`found.push(label)`은 `subsets()`의 종료 조건 분기에서 `snap(...)` 호출 **전**에
실행된다(기존 코드 순서 확인). `snap()` → `view()` → 위 새 그룹이 그 시점의
`found`를 읽으므로, 매 스냅샷은 항상 "그 시점까지 확정된" 목록을 정확히 반영한다.
새로운 상태 변수나 타이밍 조정이 필요 없다는 뜻 — 코드를 다시 읽어 `found.push`가
`snap` 호출보다 앞에 있는지 눈으로 확인한다.

- [ ] **Step 3: 검증**

Run: `node scripts/validate.mjs`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 4: 커밋**

```bash
git add algorithms/07-recursion-and-backtracking.html
git commit -m "feat: 부분집합 시각화에 단계별 누적 발견 목록 추가"
```

---

## Task 3: 결정 트리 시각화 (시각화 3) 신규 추가

**Files:**
- Modify: `algorithms/07-recursion-and-backtracking.html` (섹션 07 마크업 + 인라인 스크립트)

**Interfaces:**
- Consumes: `AlgoViz.create`(전역, `assets/js/visualization.js`가 제공) — 시그니처는
  `AlgoViz.create({ mount, title, legend, initialInput, makeSteps, makeInput })`,
  `makeSteps(input)`은 `{ caption, counters, view }` 배열을 반환해야 하고 `view`는
  `{ type, ... }` 그룹 배열(`graph` 타입은 `{ type:"graph", w, h, nodeRadius, nodes:[{id,x,y,label,sub,cls}], edges:[{from,to,cls}] }`).
  이 계약은 기존 `viz-factorial`, `viz-subsets` 블록과 동일하다(수정하지 않음).
- Produces: 없음(페이지 안에서 완결).

- [ ] **Step 1: 섹션 07 도입 문단 수정**

`algorithms/07-recursion-and-backtracking.html`에서 (원래 306-311번째 줄 부근):

```html
                <p>
                    두 개의 시각화로 오늘의 핵심을 확인합니다.
```

를

```html
                <p>
                    세 개의 시각화로 오늘의 핵심을 확인합니다.
```

로 교체(나머지 문단 내용은 그대로).

- [ ] **Step 2: 시각화 3 마크업 삽입**

기존 "시각화 2" 블록의 마지막 `</p>`와 그다음 `</section>` 사이(원래 330-331번째
줄 사이)에 삽입:

```html
                <h3>시각화 3 — {1, 2, 3} 부분집합의 결정 트리</h3>
                <p>
                    같은 재귀를 이번엔 트리 전체 관점에서 봅니다. 미리 다 그려둔 트리 위를
                    재귀가 어떻게 훑고 지나가는지 관찰하세요. <strong>파란 경로는 지금 내려가 있는 가지</strong>,
                    <strong>초록 노드는 이미 확정된 부분집합</strong>입니다.
                </p>
                <div id="viz-tree" aria-label="부분집합 백트래킹의 결정 트리 시각화"></div>
```

- [ ] **Step 3: 결정 트리 시각화 스크립트 추가**

기존 "시각화 2" IIFE가 끝나는 `})();` 다음(퀴즈 초기화 `AlgoQuiz.init(...)` 호출
앞)에 새 IIFE를 통째로 삽입:

```js
        /* ===== 시각화 3: 부분집합 결정 트리 (같은 재귀를 트리 관점에서) ===== */
        (function () {
            function buildTree(numbers) {
                var n = numbers.length;
                var w = 640, h = 300;
                var marginX = 30, marginTop = 40, marginBottom = 50;
                var leafCount = Math.pow(2, n);
                var slot = (w - marginX * 2) / leafCount;
                var levelGap = (h - marginTop - marginBottom) / n;
                var nodeById = {};
                var leafIndex = 0;

                function subsetLabel(id) {
                    var parts = [];
                    for (var i = 0; i < id.length; i += 1) {
                        if (id.charAt(i) === "1") parts.push(numbers[i]);
                    }
                    return "{" + parts.join(", ") + "}";
                }

                function nodeLabel(id) {
                    if (id === "") return "∅";
                    var depth = id.length;
                    var bit = id.charAt(depth - 1);
                    return bit === "1" ? String(numbers[depth - 1]) : "×";
                }

                function visit(id) {
                    var depth = id.length;
                    var y = marginTop + depth * levelGap;
                    var x;
                    if (depth === n) {
                        x = marginX + (leafIndex + 0.5) * slot;
                        leafIndex += 1;
                    } else {
                        var xIn = visit(id + "1");
                        var xOut = visit(id + "0");
                        x = (xIn + xOut) / 2;
                    }
                    nodeById[id] = { id: id, x: x, y: y, label: nodeLabel(id), sub: subsetLabel(id) };
                    return x;
                }
                visit("");

                var edges = [];
                Object.keys(nodeById).forEach(function (id) {
                    if (id.length > 0) {
                        edges.push({ from: id.slice(0, -1), to: id });
                    }
                });

                return { w: w, h: h, nodeById: nodeById, edges: edges };
            }

            function makeSteps(numbers) {
                var steps = [];
                var tree = buildTree(numbers);
                var chosen = [];
                var pathBits = [];
                var found = [];
                var foundIds = [];
                var subsetCount = 0;

                function nodesView(currentId) {
                    var prefixes = {};
                    if (currentId !== null) {
                        for (var i = 0; i <= currentId.length; i += 1) {
                            prefixes[currentId.slice(0, i)] = true;
                        }
                    }
                    return Object.keys(tree.nodeById).map(function (id) {
                        var node = tree.nodeById[id];
                        var cls = "";
                        if (foundIds.indexOf(id) !== -1) cls = "done";
                        else if (prefixes[id]) cls = "current";
                        return { id: id, x: node.x, y: node.y, label: node.label, sub: node.sub, cls: cls };
                    });
                }

                function edgesView(currentId) {
                    return tree.edges.map(function (edge) {
                        var onPath = currentId !== null && currentId.slice(0, edge.to.length) === edge.to;
                        return { from: edge.from, to: edge.to, cls: onPath ? "current" : "" };
                    });
                }

                function view(currentId, noteText) {
                    var groups = [
                        {
                            type: "graph",
                            label: "결정 트리 — 파란 경로 = 지금 위치, 초록 = 확정된 부분집합",
                            w: tree.w,
                            h: tree.h,
                            nodeRadius: 15,
                            nodes: nodesView(currentId),
                            edges: edgesView(currentId)
                        }
                    ];
                    if (noteText) groups.push({ type: "note", text: noteText });
                    return groups;
                }

                function snap(caption, currentId, noteText) {
                    steps.push({
                        caption: caption,
                        counters: { "부분집합": subsetCount },
                        view: view(currentId, noteText)
                    });
                }

                steps.push({
                    caption: "루트(빈 집합)에서 시작합니다. 0번 원소부터 넣을지/뺄지 결정하며 트리를 내려갑니다.",
                    counters: { "부분집합": 0 },
                    view: view("", "리프(잎) 노드는 모두 " + Math.pow(2, numbers.length) + "개 — 원소마다 두 갈래로 갈라지기 때문입니다.")
                });

                function walk(index) {
                    if (index === numbers.length) {
                        var id = pathBits.join("");
                        subsetCount += 1;
                        var label = "[" + chosen.join(", ") + "]";
                        found.push(label);
                        foundIds.push(id);
                        snap("리프 노드 도달 — 부분집합 " + subsetCount + ": " + label + " 확정, 초록으로 칠해집니다.", id, null);
                        return;
                    }

                    chosen.push(numbers[index]);
                    pathBits.push("1");
                    snap("(1) 선택 + (2) 진행: " + numbers[index] + "을(를) 넣고 파란 경로를 따라 자식 노드로 내려갑니다.", pathBits.join(""), null);
                    walk(index + 1);
                    pathBits.pop();
                    chosen.pop();

                    pathBits.push("0");
                    snap("(3) 취소: " + numbers[index] + "을(를) 넣지 않는 반대쪽(×) 가지로 이동합니다.", pathBits.join(""), null);
                    walk(index + 1);
                    pathBits.pop();
                }

                walk(0);

                steps.push({
                    caption: "완료! 트리의 모든 리프 " + subsetCount + "개가 초록으로 칠해졌습니다 — 나열이 빠짐없고 중복 없음을 뜻합니다.",
                    counters: { "부분집합": subsetCount },
                    view: view(null, "같은 나무를 손 풀이(STEP 4) · 시각화 2 · 이 트리가 모두 같은 순서로 훑습니다.")
                });

                return steps;
            }

            AlgoViz.create({
                mount: "#viz-tree",
                title: "부분집합 결정 트리 — 경로와 확정된 리프",
                legend: ["current", "done"],
                initialInput: [1, 2, 3],
                makeSteps: makeSteps,
                makeInput: function () {
                    var pool = [1, 2, 3, 4, 5, 6, 7, 8, 9];
                    var size = 2 + Math.floor(Math.random() * 2); // 2 ~ 3개
                    var picked = [];
                    while (picked.length < size) {
                        var v = pool[Math.floor(Math.random() * pool.length)];
                        if (picked.indexOf(v) === -1) picked.push(v);
                    }
                    picked.sort(function (a, b) { return a - b; });
                    return picked;
                }
            });
        })();

```

- [ ] **Step 4: 트리 레이아웃 수치 검증 (Node로 임시 실행)**

브라우저 없이 좌표 계산이 맞는지 확인한다. 아래 스크래치 스크립트를 임시로 만들어
실행하고 끝나면 지운다(저장소에 커밋하지 않음):

```bash
cat > /tmp/check-tree.mjs <<'EOF'
function buildTree(numbers) {
    var n = numbers.length;
    var w = 640, h = 300;
    var marginX = 30, marginTop = 40, marginBottom = 50;
    var leafCount = Math.pow(2, n);
    var slot = (w - marginX * 2) / leafCount;
    var levelGap = (h - marginTop - marginBottom) / n;
    var nodeById = {};
    var leafIndex = 0;
    function visit(id) {
        var depth = id.length;
        var y = marginTop + depth * levelGap;
        var x;
        if (depth === n) { x = marginX + (leafIndex + 0.5) * slot; leafIndex += 1; }
        else { var xIn = visit(id + "1"); var xOut = visit(id + "0"); x = (xIn + xOut) / 2; }
        nodeById[id] = { x: x, y: y };
        return x;
    }
    visit("");
    return nodeById;
}
[2, 3].forEach(function (n) {
    var numbers = n === 2 ? [1, 2] : [1, 2, 3];
    var nodes = buildTree(numbers);
    var xs = Object.keys(nodes).map(function (id) { return nodes[id].x; });
    var ys = Object.keys(nodes).map(function (id) { return nodes[id].y; });
    console.log("n=" + n, "노드 수=" + Object.keys(nodes).length,
        "x범위=" + Math.min.apply(null, xs) + ".." + Math.max.apply(null, xs),
        "y범위=" + Math.min.apply(null, ys) + ".." + Math.max.apply(null, ys));
});
EOF
node /tmp/check-tree.mjs
rm /tmp/check-tree.mjs
```

Expected: n=2는 노드 수 7개, n=3은 노드 수 15개. x범위가 대략 15~625 사이(marginX=30,
반지름 15 고려하면 0~640 안에 들어옴), y범위가 40~250 사이(marginBottom=50 여유로 300
안에 들어옴) — 둘 다 `w=640`, `h=300` 안쪽에 여유 있게 들어오면 통과. `NaN`이나 범위
이탈이 있으면 `buildTree`의 `marginX`/`marginTop`/`marginBottom`/`slot`/`levelGap`
계산을 다시 확인한다.

- [ ] **Step 5: 검증**

Run: `node scripts/validate.mjs`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 6: 커밋**

```bash
git add algorithms/07-recursion-and-backtracking.html
git commit -m "feat: 부분집합 백트래킹 결정 트리 시각화 추가"
```

---

## Task 4: 최종 확인 및 배포

**Files:** 없음(검증 + 배포만)

**Interfaces:** 없음.

- [ ] **Step 1: 전체 검증 재실행**

Run: `node scripts/validate.mjs`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 2: 변경 파일 diff 최종 확인**

Run: `git status --short && git log --oneline -4`
Expected: Task 1~3의 커밋 3개가 보이고, `git status`에 남은 미커밋 변경이 없어야 한다.

- [ ] **Step 3: origin/main에 배포**

```bash
git push origin main
```

Vercel이 `main`을 감시하고 있으므로(빌드 스텝 없음) push가 곧 배포다. 별도의 수동
배포 단계는 없다.
