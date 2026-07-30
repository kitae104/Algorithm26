# 시각화 모션·본문 강조 개선 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 13개 강의의 시각화 단계 전환을 하드컷에서 의미 있는 모션으로 바꾸고, 랜딩 히어로를 재설계하고, 강의의 주요 내용을 훑어보기 쉽게 강조한다.

**Architecture:** `AlgoViz.renderStep()`이 매 단계 무대 DOM을 파괴·재생성하던 것을 키 기반 셀 재사용으로 바꿔 CSS 전환이 실제로 발동하게 만든다. 그 위에 이전·현재 단계의 값 배열을 비교해 교환과 앞으로 복사를 자동 감지하고 슬라이드 모션을 낸다. 강조는 CSS 중심으로, 강의 HTML은 클래스 삽입만 한다.

**Tech Stack:** 순수 ES5 JavaScript (빌드 단계 없음), CSS 커스텀 프로퍼티, 정적 HTML. 외부 런타임 의존성은 Pretendard 웹폰트 CDN뿐이다.

## Global Constraints

- **ES5만 사용한다.** 프로젝트 전체가 `var`·`function`·IIFE이고 트랜스파일 단계가 없다. `const`/`let`/화살표 함수/템플릿 리터럴/`class`를 쓰지 않는다.
- **외부 라이브러리를 추가하지 않는다.** npm 런타임 의존성 0개를 유지한다.
- **`AlgoViz` 공개 API를 바꾸지 않는다.** `AlgoViz.create(config)`, 반환 객체의 `rebuild()`·`setInput(next)`, step 객체 형식(`caption`/`counters`/`view`), view group 형식(`array`/`stack`/`queue`/`table`/`graph`/`frames`/`note`)이 모두 그대로여야 한다. 13개 강의에 있는 **15개 시각화 호출부의 `makeSteps` 코드는 한 줄도 고치지 않는다.**
- **`prefers-reduced-motion: reduce`를 존중한다.** 이 상태에서 자동 재생·이동 모션·스크롤 진입이 모두 비활성이어야 하고, 수동 조작은 계속 가능해야 한다.
- **색상만으로 상태를 구분하지 않는다.** `docs/adding-lessons.md`의 접근성 규칙이다. 모든 상태는 색 + 형태/위치/텍스트 중 하나를 함께 쓴다.
- **인쇄 호환.** 학생용·교수자용 인쇄가 핵심 기능이다. 애니메이션 중간 상태(예: 스크롤 진입 전 `opacity: 0`)가 인쇄물에 남으면 안 된다.
- **`node scripts/validate.mjs`가 항상 통과해야 한다.** `npm test`가 이 스크립트다.
- **파일 인코딩은 UTF-8, 줄바꿈은 LF.** 기존 파일과 동일하게 유지한다.
- 커밋 메시지 마지막 줄: `Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>`

## 테스트 전략 (중요 — 이 저장소에 단위 테스트 프레임워크가 없다)

`package.json`의 `test`는 `node scripts/validate.mjs`이고, 이는 사이트 구조 정합성 검사기다. jest/pytest 같은 프레임워크가 없고 이 계획에서 도입하지도 않는다. 따라서 각 태스크의 검증은 둘 중 하나다.

1. **정적 불변식** → `scripts/validate.mjs`에 실제 assertion을 추가한다. `npm test`로 자동 실행되고 배포 게이트가 된다.
2. **런타임 동작** → 로컬 서버를 띄우고 브라우저에서 정해진 스니펫을 실행해 기대값과 비교한다. 각 태스크에 실행할 코드와 기대 출력이 그대로 적혀 있다.

**로컬 서버 (런타임 검증이 있는 모든 태스크에서 사용)**

```bash
cd /d/Github/Algorithm_WS/Algorithm26
python -m http.server 4173 &
# 확인
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:4173/index.html   # 기대: 200
```

작업이 끝나면 서버를 정리한다.

```powershell
Get-CimInstance Win32_Process -Filter "Name like 'python%'" |
  Where-Object { $_.CommandLine -like '*http.server 4173*' } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
```

## File Structure

| 파일 | 역할 | 변경 |
|---|---|---|
| `assets/css/common.css` | 전역 토큰·기본 컴포넌트. 모션 토큰의 단일 출처가 된다 | 수정 |
| `assets/css/visualization.css` | 시각화 상태 모션 문법 | 수정 |
| `assets/css/landing.css` | 랜딩 히어로 |  수정 |
| `assets/css/lesson.css` | 강의 강조 컴포넌트. 죽은 `inline-array` 제거 | 수정 |
| `assets/css/print.css` | 인쇄 시 애니메이션 중간 상태 무력화 | 수정 |
| `assets/js/visualization.js` | 엔진. 키 기반 갱신 + 이동 감지 + 슬라이드 모션 | 수정 |
| `assets/js/landing.js` | 히어로 재생 제어·타임라인 | 수정 |
| `assets/js/common.js` | 스크롤 진입 관찰, 읽는 진도 바 | 수정 |
| `index.html` | 히어로 마크업 | 수정 |
| `algorithms/*.html` (13개) | 핵심 정리 `<ul>`에 클래스 삽입 | 수정 |
| `scripts/validate.mjs` | 정적 불변식 assertion 추가 | 수정 |

`visualization.js`가 459줄에서 늘어난다. 이동 감지 로직이 렌더링과 뒤섞이지 않게 파일 안에서 `/* ---------- 이동 감지 ---------- */` 섹션으로 분리하되, 파일은 쪼개지 않는다. 이 저장소는 스크립트 하나당 한 관심사를 유지하고 빌드가 없어 모듈 분할이 곧 `<script>` 태그 추가를 뜻하기 때문이다 — 13개 강의 HTML을 모두 고쳐야 하므로 제약과 충돌한다.

---

### Task 1: 모션 토큰과 reduced-motion 배선

전환 시간과 이징을 한곳에서 정의해 이후 모든 태스크가 참조하게 만든다. 지금은 전환 선언 9곳이 모두 맨 `ease`라 사이트에 리듬이 없다.

**Files:**
- Modify: `assets/css/common.css:19-50` (`:root` 토큰 블록), `assets/css/common.css:157-168` (reduced-motion 블록)
- Modify: `scripts/validate.mjs` (CSS 정적 검사 섹션 신규)

**Interfaces:**
- Consumes: 없음
- Produces: CSS 커스텀 프로퍼티 6개 — `--dur-fast`, `--dur-base`, `--dur-slow`, `--ease-out`, `--ease-settle`, `--ease-in-out`. 이후 모든 태스크가 이 이름을 그대로 쓴다.

- [ ] **Step 1: 실패하는 검사를 먼저 추가한다**

`scripts/validate.mjs`의 `/* ---------- index.html 링크 검증 ---------- */` 블록 **바로 앞**에 아래를 삽입한다.

```javascript
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
```

- [ ] **Step 2: 검사가 실패하는 것을 확인한다**

```bash
node scripts/validate.mjs
```

기대: exit code 1, 오류 9건 — 토큰 6건 누락 + reduced-motion 무력화 3건 누락.

- [ ] **Step 3: 토큰을 정의한다**

`assets/css/common.css`의 `:root` 블록에서 `--shadow-pop` 선언 바로 아래에 삽입한다.

```css
    /* 모션 — 전환 시간과 이징의 단일 출처 */
    --dur-fast: 140ms;      /* 색·테두리 등 즉각 반응 */
    --dur-base: 240ms;      /* 일반 상태 전환 */
    --dur-slow: 340ms;      /* 자리 교대, 높이 변화 */
    --ease-out: cubic-bezier(0.22, 0.61, 0.36, 1);
    --ease-settle: cubic-bezier(0.34, 1.26, 0.64, 1);   /* 약한 오버슈트 — 확정의 정착감 */
    --ease-in-out: cubic-bezier(0.45, 0.05, 0.55, 0.95);
```

- [ ] **Step 4: reduced-motion에서 토큰을 무력화한다**

`assets/css/common.css:157`의 블록을 아래로 교체한다. 기존 `*` 규칙은 유지하고 토큰 무력화를 더한다.

```css
@media (prefers-reduced-motion: reduce) {
    :root {
        --dur-fast: 0.01ms;
        --dur-base: 0.01ms;
        --dur-slow: 0.01ms;
    }

    *,
    *::before,
    *::after {
        animation-duration: 0.01ms !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.01ms !important;
        scroll-behavior: auto !important;
    }
}
```

- [ ] **Step 5: 기존 전환 9곳을 토큰으로 바꾼다**

아래 표대로 정확히 치환한다. 값이 바뀌는 곳이 있으므로 그대로 따른다.

| 파일:줄 | 변경 전 | 변경 후 |
|---|---|---|
| `common.css:241` | `transition: top 0.15s ease;` | `transition: top var(--dur-fast) var(--ease-out);` |
| `common.css:485` | `transition: background 0.12s ease, transform 0.12s ease;` | `transition: background var(--dur-fast) var(--ease-out), transform var(--dur-fast) var(--ease-out);` |
| `common.css:574` | `transition: width 0.4s ease;` | `transition: width var(--dur-slow) var(--ease-out);` |
| `common.css:594` | `transition: opacity 0.2s ease;` | `transition: opacity var(--dur-base) var(--ease-out);` |
| `code.css:70` | `transition: background 0.12s ease;` | `transition: background var(--dur-fast) var(--ease-out);` |
| `landing.css:88` | `transition: height 0.35s ease, background 0.25s ease, border-color 0.25s ease;` | `transition: height var(--dur-slow) var(--ease-out), background var(--dur-base) var(--ease-out), border-color var(--dur-base) var(--ease-out);` |
| `landing.css:343` | `transition: transform 0.15s ease, border-color 0.15s ease;` | `transition: transform var(--dur-fast) var(--ease-out), border-color var(--dur-fast) var(--ease-out);` |
| `lesson.css:364` | `transition: border-color 0.12s ease;` | `transition: border-color var(--dur-fast) var(--ease-out);` |
| `quiz.css:82` | `transition: border-color 0.1s ease, background 0.1s ease;` | `transition: border-color var(--dur-fast) var(--ease-out), background var(--dur-fast) var(--ease-out);` |

`visualization.css:77`은 Task 4에서 다루므로 지금 건드리지 않는다.

- [ ] **Step 6: 검사가 통과하는 것을 확인한다**

```bash
node scripts/validate.mjs
```

기대: `모든 검증 통과 ✓`, exit code 0.

- [ ] **Step 7: 커밋**

```bash
git add assets/css/common.css assets/css/code.css assets/css/landing.css assets/css/lesson.css assets/css/quiz.css scripts/validate.mjs
git commit -m "$(cat <<'EOF'
feat: 모션 토큰 도입과 reduced-motion 배선

전환 시간·이징을 :root 토큰 6개로 통일하고 기존 전환 9곳을 토큰 참조로
바꾼다. reduced-motion에서 duration 토큰을 0.01ms로 덮어 한곳에서
모든 모션을 무력화한다.

validate.mjs에 토큰 정의와 reduced-motion 무력화 검사를 추가한다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 2: 엔진 — 키 기반 셀 재사용

이 태스크가 하드컷의 근본 원인을 없앤다. `renderStep()`이 `stage.textContent = ""`로 무대를 비우므로 새 셀은 상태 클래스를 갖고 태어나고, 전환의 시작값이 없어 `visualization.css:77`의 선언이 발동하지 않는다.

**Files:**
- Modify: `assets/js/visualization.js` — `GROUP_RENDERERS` 뒤에 패치 계층 추가, `renderStep()` 재작성

**Interfaces:**
- Consumes: Task 1의 `--dur-*`, `--ease-*` 토큰 (CSS에서만 사용)
- Produces:
  - `groupSignature(group) -> string` — 그룹의 구조 지문. 같으면 패치 가능
  - `patchGroup(entry, group) -> void` — `entry`는 `{ sig: string, block: HTMLElement, body: HTMLElement }`
  - `mountGroup(group) -> { sig, block, body }` — 분리된(detached) DOM을 만든다
  - `renderView(view) -> void` — `mounted` 배열을 갱신하며 무대를 동기화
  - 모듈 스코프 변수 `mounted` (배열, `create()` 내부)

- [ ] **Step 1: 실패하는 브라우저 검사를 준비한다**

서버를 띄우고 `http://localhost:4173/algorithms/04-sorting-algorithms.html`에서 아래를 실행한다.

```javascript
(function () {
    var stage = document.querySelector('#viz-selection-sort .viz__stage');
    var before = Array.prototype.slice.call(stage.querySelectorAll('.viz-cell'));
    var next = Array.prototype.slice
        .call(document.querySelectorAll('#viz-selection-sort .viz__controls .button'))
        .filter(function (b) { return b.textContent.indexOf('다음 단계') !== -1; })[0];
    next.click();
    var after = Array.prototype.slice.call(stage.querySelectorAll('.viz-cell'));
    return {
        sameLength: before.length === after.length,
        identityPreserved: before.length > 0 && before.every(function (n, i) { return n === after[i]; })
    };
})()
```

기대(현재, 실패): `{ sameLength: true, identityPreserved: false }` — 노드가 전부 새로 만들어지고 있다.

- [ ] **Step 2: 시그니처 함수를 추가한다**

`assets/js/visualization.js`의 `GROUP_RENDERERS` 객체 닫는 `};` 바로 뒤에 삽입한다.

```javascript
    /* ---------- 그룹 시그니처와 패치 ----------
     * 시그니처가 같으면 기존 DOM을 재사용해 값·클래스만 갱신한다.
     * 그래야 CSS 전환의 시작값이 존재해 실제로 애니메이션이 발동한다.
     */

    function cellItems(group) {
        if (group.type === "array") return group.cells || [];
        return group.items || [];
    }

    function groupSignature(group) {
        var parts = [group.type, group.label || ""];
        if (group.type === "table") {
            parts.push((group.head || []).join(""));
            parts.push((group.rows || []).map(function (r) { return r.length; }).join(","));
        } else if (group.type === "graph") {
            parts.push((group.nodes || []).map(function (n) { return n.id; }).join(","));
            parts.push((group.edges || []).map(function (e) { return e.from + ">" + e.to; }).join(","));
            parts.push(String(group.w || 560), String(group.h || 300));
        } else if (group.type === "note") {
            parts.push("note");
        } else {
            parts.push(String(cellItems(group).length));
            parts.push(group.emptyText || "");
        }
        return parts.join("|");
    }
```

- [ ] **Step 3: 패치 함수들을 추가한다**

Step 2의 코드 바로 뒤에 이어 붙인다. `renderCells`가 텍스트 노드를 먼저 넣고 `<small>`을 뒤에 붙이므로(`visualization.js:66-71`) `firstChild`가 텍스트 노드다.

```javascript
    function setCellText(cell, value) {
        var text = value === undefined ? "" : String(value);
        if (cell.firstChild && cell.firstChild.nodeType === 3) {
            if (cell.firstChild.nodeValue !== text) {
                cell.firstChild.nodeValue = text;
            }
        } else {
            cell.insertBefore(document.createTextNode(text), cell.firstChild || null);
        }
    }

    function setCellSub(cell, sub) {
        var small = cell.querySelector("small");
        if (sub === undefined || sub === null) {
            if (small) small.parentNode.removeChild(small);
            return;
        }
        if (!small) {
            small = el("small");
            cell.appendChild(small);
        }
        if (small.textContent !== String(sub)) {
            small.textContent = String(sub);
        }
    }

    function setClass(node, className) {
        if (node.getAttribute("class") !== className) {
            node.setAttribute("class", className);
        }
    }

    function patchCells(container, items, baseClass, modifierPrefix) {
        var nodes = container.querySelectorAll("." + baseClass);
        for (var i = 0; i < items.length && i < nodes.length; i += 1) {
            var item = items[i];
            setCellText(nodes[i], item.v);
            if (baseClass === "viz-cell") {
                setCellSub(nodes[i], item.sub);
            }
            setClass(nodes[i], baseClass + (item.cls ? " " + modifierPrefix + item.cls : ""));
        }
    }

    var GROUP_PATCHERS = {
        array: function (body, group) {
            patchCells(body, group.cells || [], "viz-cell", "viz-cell--");
        },
        stack: function (body, group) {
            patchCells(body, group.items || [], "viz-cell", "viz-cell--");
        },
        queue: function (body, group) {
            patchCells(body, group.items || [], "viz-cell", "viz-cell--");
        },
        frames: function (body, group) {
            patchCells(body, group.items || [], "viz-frame", "viz-frame--");
        },
        table: function (body, group) {
            var rows = body.querySelectorAll("tbody tr");
            (group.rows || []).forEach(function (row, r) {
                if (!rows[r]) return;
                var cells = rows[r].children;
                row.forEach(function (cellDef, c) {
                    if (!cells[c]) return;
                    var value = (cellDef === null || cellDef === undefined) ? "" : cellDef.v;
                    cells[c].textContent = value === undefined ? "" : String(value);
                    setClass(cells[c], cellDef && cellDef.cls ? "viz-cell--" + cellDef.cls : "");
                });
            });
        },
        graph: function (body, group) {
            var nodeEls = body.querySelectorAll("g.node");
            (group.nodes || []).forEach(function (node, i) {
                if (!nodeEls[i]) return;
                setClass(nodeEls[i], "node" + (node.cls ? " node--" + node.cls : ""));
                var subEl = nodeEls[i].querySelector("text.node-sub");
                if (node.sub === undefined || node.sub === null) {
                    if (subEl) subEl.parentNode.removeChild(subEl);
                } else if (subEl) {
                    if (subEl.textContent !== String(node.sub)) {
                        subEl.textContent = String(node.sub);
                    }
                } else {
                    var sub = svgEl("text");
                    sub.setAttribute("x", node.x);
                    sub.setAttribute("y", node.y + (group.nodeRadius || 20) + 13);
                    sub.setAttribute("class", "node-sub");
                    sub.textContent = String(node.sub);
                    nodeEls[i].appendChild(sub);
                }
            });
            /* 간선은 svg에 line으로 순서대로 추가되고 라벨 text가 섞여 들어가지만
             * line.edge로 필터하면 group.edges 순서가 그대로 유지된다 */
            var edgeEls = body.querySelectorAll("line.edge");
            (group.edges || []).forEach(function (edge, i) {
                if (!edgeEls[i]) return;
                var cls = "edge" + (edge.cls ? " edge--" + edge.cls : "");
                if (edgeEls[i].getAttribute("class") !== cls) {
                    edgeEls[i].setAttribute("class", cls);
                }
            });
        },
        note: function (body, group) {
            if (body.textContent !== String(group.text)) {
                body.textContent = String(group.text);
            }
        }
    };
```

- [ ] **Step 4: `mountGroup`과 `renderView`를 추가한다**

Step 3 코드 바로 뒤에 이어 붙인다.

```javascript
    function mountGroup(group) {
        var renderer = GROUP_RENDERERS[group.type];
        if (!renderer) return null;
        var block = el("div");
        if (group.label) {
            block.appendChild(el("p", "viz__group-label", group.label));
        }
        var body = renderer(group);
        block.appendChild(body);
        return { sig: groupSignature(group), block: block, body: body };
    }
```

- [ ] **Step 5: `renderStep()`을 재작성한다**

`assets/js/visualization.js:334-373`의 `renderStep` 함수 전체를 아래로 교체한다. `create()` 안에 `var mounted = [];`를 `var timer = null;` 선언 옆에 추가한다.

```javascript
        function renderView(view) {
            var groups = (view || []).filter(function (g) {
                return !!GROUP_RENDERERS[g.type];
            });

            if (mounted.length !== groups.length) {
                stage.textContent = "";
                mounted = [];
                groups.forEach(function (group) {
                    var entry = mountGroup(group);
                    if (!entry) return;
                    stage.appendChild(entry.block);
                    mounted.push(entry);
                });
                return;
            }

            groups.forEach(function (group, i) {
                var sig = groupSignature(group);
                if (mounted[i].sig !== sig) {
                    var entry = mountGroup(group);
                    if (!entry) return;
                    stage.replaceChild(entry.block, mounted[i].block);
                    mounted[i] = entry;
                    return;
                }
                var patcher = GROUP_PATCHERS[group.type];
                if (patcher) patcher(mounted[i].body, group);
            });
        }

        function renderStep() {
            var step = steps[index];
            if (!step) return;

            renderView(step.view);

            caption.textContent = "";
            caption.appendChild(el("strong", null, "단계 " + (index + 1) + ". "));
            caption.appendChild(document.createTextNode(step.caption || ""));

            stats.textContent = "";
            if (step.counters) {
                Object.keys(step.counters).forEach(function (key) {
                    var chip = el("span", "viz__stat");
                    chip.appendChild(document.createTextNode(key + " "));
                    chip.appendChild(el("b", null, step.counters[key]));
                    stats.appendChild(chip);
                });
            }

            counter.textContent = (index + 1) + " / " + steps.length;
            btnPrev.disabled = index === 0;
            btnFirst.disabled = index === 0;
            btnNext.disabled = index >= steps.length - 1;

            if (index >= steps.length - 1) {
                stopAuto();
            }
        }
```

`rebuild()`는 무대를 새로 만들어야 하므로 `index = 0;` 앞에 `mounted = []; stage.textContent = "";`를 추가한다.

- [ ] **Step 6: 노드 identity가 유지되는지 확인한다**

Step 1의 스니펫을 다시 실행한다.

기대: `{ sameLength: true, identityPreserved: true }`

- [ ] **Step 7: 15개 시각화가 회귀하지 않았는지 확인한다**

각 강의 페이지에서 아래를 실행한다. 시각화별로 처음부터 끝까지 단계를 넘기며 캡션·카운터가 정상인지 본다.

```javascript
(function () {
    var out = [];
    document.querySelectorAll('.viz').forEach(function (viz) {
        var buttons = Array.prototype.slice.call(viz.querySelectorAll('.viz__controls .button'));
        var next = buttons.filter(function (b) { return b.textContent.indexOf('다음 단계') !== -1; })[0];
        var total = parseInt((viz.querySelector('.viz__counter').textContent.split('/')[1] || '0'), 10);
        var errors = 0;
        for (var i = 1; i < total; i += 1) {
            next.click();
            if (!viz.querySelector('.viz__caption').textContent.trim()) errors += 1;
            if (!viz.querySelector('.viz__stage').children.length) errors += 1;
        }
        out.push({
            title: viz.querySelector('.viz__title').textContent,
            total: total,
            reachedEnd: viz.querySelector('.viz__counter').textContent.trim() === total + ' / ' + total,
            errors: errors
        });
    });
    return out;
})()
```

기대: 모든 시각화가 `reachedEnd: true`, `errors: 0`. 13개 강의 전부 확인한다 (4·6·7강은 시각화가 2개다).

- [ ] **Step 8: 커밋**

```bash
git add assets/js/visualization.js
git commit -m "$(cat <<'EOF'
fix: 시각화 단계 전환의 하드컷 원인 제거

renderStep이 매 단계 stage.textContent로 무대를 비워 새 셀이 상태
클래스를 갖고 태어났고, 그래서 선언된 CSS 전환의 시작값이 없어
애니메이션이 발동하지 않았다.

그룹 시그니처(종류+라벨+개수)가 같으면 기존 DOM을 재사용해 값·서브라벨·
상태 클래스만 갱신하도록 바꾼다. 공개 API와 step 데이터 형식은 그대로여서
15개 시각화 호출부는 무변경이다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 3: 엔진 — 교환 감지와 자리 교대 모션

Task 2로 색 전환은 부드러워졌지만 값이 제자리에서 갈아치워진다. 선택 정렬의 교환이 "두 셀이 자리를 바꿈"으로 보이게 만든다.

**Files:**
- Modify: `assets/js/visualization.js` — 이동 감지 섹션 추가, `renderStep`에 애니메이션 단계 삽입
- Modify: `assets/css/visualization.css` — `.viz-cell.is-moving`

**Interfaces:**
- Consumes: Task 2의 `renderView`, `cellItems`, `mounted`
- Produces:
  - `detectSwap(prev, next) -> { a: number, b: number } | null`
  - `animateSwap(cells, a, b, durMs, onDone) -> function` — 반환값은 즉시 종료 함수
  - `arrayValuesOf(view) -> Array | null` — view의 첫 `array` 그룹 값 배열

**동작 원리 (중요):** 슬라이드는 **옛 값을 보여준 채로** 진행하고, 애니메이션이 끝나는 순간 transform 리셋과 값 커밋을 동시에 한다. 리셋 직전 노드 `a`는 위치 `b`에서 `prev[a]`를 보여주고 있고, 리셋 직후 노드 `b`가 위치 `b`에서 `next[b]`(= `prev[a]`)를 보여준다. 각 화면 위치의 내용이 그 순간 동일하므로 커밋이 시각적으로 이어진다.

- [ ] **Step 1: 실패하는 검사를 준비한다**

4강 선택 정렬은 교환 단계의 캡션에 "교환:"이 들어간다. 교환 단계로 이동할 때 셀에 `is-moving`이 붙는지 본다.

```javascript
(function () {
    var viz = document.querySelector('#viz-selection-sort');
    var next = Array.prototype.slice.call(viz.querySelectorAll('.viz__controls .button'))
        .filter(function (b) { return b.textContent.indexOf('다음 단계') !== -1; })[0];
    var sawMoving = false;
    for (var i = 0; i < 12; i += 1) {
        next.click();
        if (viz.querySelectorAll('.viz-cell.is-moving').length === 2) sawMoving = true;
        if (viz.querySelector('.viz__caption').textContent.indexOf('교환:') !== -1) break;
    }
    return { caption: viz.querySelector('.viz__caption').textContent.trim().slice(0, 40), sawMoving: sawMoving };
})()
```

기대(현재, 실패): `sawMoving: false`

- [ ] **Step 2: 감지 함수를 추가한다**

Task 2에서 만든 `mountGroup` 바로 뒤에 삽입한다.

```javascript
    /* ---------- 이동 감지 ----------
     * 강의의 makeSteps 코드를 고치지 않고 step 데이터만 비교해 값의 이동을 추론한다.
     * 교환: 정확히 두 위치의 값이 서로 맞바뀐다 (4강 선택 정렬의 tmp 교환).
     */

    function arrayValuesOf(view) {
        var groups = view || [];
        for (var i = 0; i < groups.length; i += 1) {
            if (groups[i].type === "array") {
                return (groups[i].cells || []).map(function (c) { return c.v; });
            }
        }
        return null;
    }

    function detectSwap(prev, next) {
        if (!prev || !next || prev.length !== next.length) return null;
        var diff = [];
        for (var i = 0; i < next.length; i += 1) {
            if (prev[i] !== next[i]) {
                diff.push(i);
                if (diff.length > 2) return null;
            }
        }
        if (diff.length !== 2) return null;
        var a = diff[0];
        var b = diff[1];
        if (prev[a] === next[b] && prev[b] === next[a]) {
            return { a: a, b: b };
        }
        return null;
    }

    function animateSwap(cells, a, b, durMs, onDone) {
        var ra = cells[a].getBoundingClientRect();
        var rb = cells[b].getBoundingClientRect();
        var dx = rb.left - ra.left;
        var finished = false;

        function finish() {
            if (finished) return;
            finished = true;
            [a, b].forEach(function (k) {
                cells[k].style.transition = "";
                cells[k].style.transform = "";
                cells[k].classList.remove("is-moving");
            });
            onDone();
        }

        [a, b].forEach(function (k) {
            cells[k].classList.add("is-moving");
            cells[k].style.transition = "transform " + durMs + "ms var(--ease-in-out)";
        });
        cells[a].style.transform = "translateX(" + dx + "px)";
        cells[b].style.transform = "translateX(" + (-dx) + "px)";

        setTimeout(finish, durMs + 40);
        return finish;
    }
```

- [ ] **Step 3: `renderStep`에 애니메이션 단계를 넣는다**

`create()` 안에 상태 변수를 추가한다. `var mounted = [];` 옆이다.

```javascript
        var pendingFinish = null;   // 진행 중인 이동 모션의 즉시 종료 함수
        var prevValues = null;      // 직전 단계의 배열 값 (이동 감지용)
```

`renderStep()`을 아래로 교체한다. Task 2 버전에서 이동 감지 분기만 추가된 형태다.

```javascript
        function commitStep(step) {
            renderView(step.view);

            caption.textContent = "";
            caption.appendChild(el("strong", null, "단계 " + (index + 1) + ". "));
            caption.appendChild(document.createTextNode(step.caption || ""));

            stats.textContent = "";
            if (step.counters) {
                Object.keys(step.counters).forEach(function (key) {
                    var chip = el("span", "viz__stat");
                    chip.appendChild(document.createTextNode(key + " "));
                    chip.appendChild(el("b", null, step.counters[key]));
                    stats.appendChild(chip);
                });
            }

            counter.textContent = (index + 1) + " / " + steps.length;
            btnPrev.disabled = index === 0;
            btnFirst.disabled = index === 0;
            btnNext.disabled = index >= steps.length - 1;
            prevValues = arrayValuesOf(step.view);

            if (index >= steps.length - 1) {
                stopAuto();
            }
        }

        function moveDuration() {
            /* 자동 재생 중에는 모션이 재생 간격을 넘지 않게 조인다 */
            var base = 340;
            if (timer) return Math.min(base, Math.round(speedMs * 0.55));
            return base;
        }

        function renderStep() {
            var step = steps[index];
            if (!step) return;

            /* 진행 중인 모션이 있으면 즉시 끝내고 새 단계로 넘어간다 */
            if (pendingFinish) {
                var finishNow = pendingFinish;
                pendingFinish = null;
                finishNow();
            }

            var nextValues = arrayValuesOf(step.view);
            var swap = reducedMotion ? null : detectSwap(prevValues, nextValues);

            if (!swap) {
                commitStep(step);
                return;
            }

            var cells = stage.querySelectorAll(".viz-array .viz-cell");
            if (cells.length !== nextValues.length) {
                commitStep(step);
                return;
            }

            pendingFinish = animateSwap(cells, swap.a, swap.b, moveDuration(), function () {
                pendingFinish = null;
                commitStep(step);
            });
        }
```

`rebuild()`에 `prevValues = null; pendingFinish = null;`을 추가한다.

- [ ] **Step 3b: 임의 점프에서 엉뚱한 모션이 나오지 않게 막는다**

"⏮ 처음부터"는 마지막에 본 단계에서 0번 단계로 건너뛴다. 이때 두 단계 사이에 우연히
"두 위치만 다르고 값이 맞바뀐" 관계가 성립하면 실제로는 일어나지 않은 교환 모션이 재생된다.
점프 직전에 직전 값을 지워 감지를 끈다. `btnFirst`의 핸들러를 아래로 교체한다.

```javascript
        btnFirst.addEventListener("click", function () {
            stopAuto();
            index = 0;
            prevValues = null;   /* 인접 단계가 아니므로 이동 감지를 끈다 */
            renderStep();
        });
```

- [ ] **Step 4: 이동 중 셀이 이웃 위로 오게 한다**

`assets/css/visualization.css`의 `.viz-cell--faded` 규칙 뒤에 추가한다.

```css
/* 자리 교대 중인 셀은 이웃 위로 올라온다 */
.viz-cell.is-moving {
    position: relative;
    z-index: 2;
    box-shadow: var(--shadow-pop);
}
```

- [ ] **Step 5: 자리 교대가 보이는지 확인한다**

Step 1의 스니펫을 다시 실행한다.

기대: `sawMoving: true`, 캡션에 `교환:` 포함.

- [ ] **Step 6: 연속 클릭에서 깨지지 않는지 확인한다**

```javascript
(function () {
    var viz = document.querySelector('#viz-selection-sort');
    var next = Array.prototype.slice.call(viz.querySelectorAll('.viz__controls .button'))
        .filter(function (b) { return b.textContent.indexOf('다음 단계') !== -1; })[0];
    for (var i = 0; i < 30; i += 1) next.click();     // 모션 대기 없이 난타
    var cells = Array.prototype.slice.call(viz.querySelectorAll('.viz-cell'));
    return {
        counter: viz.querySelector('.viz__counter').textContent.trim(),
        strayTransform: cells.filter(function (c) { return c.style.transform; }).length,
        strayMoving: viz.querySelectorAll('.viz-cell.is-moving').length
    };
})()
```

기대: `strayTransform: 0`, `strayMoving: 0` — 잔여 인라인 스타일이 남지 않아야 한다.

- [ ] **Step 7: reduced-motion에서 모션이 없는지 확인한다**

브라우저를 `prefers-reduced-motion: reduce`로 강제한 뒤 Step 1을 재실행한다.

기대: `sawMoving: false`, 단계 이동은 정상 동작.

- [ ] **Step 8: 커밋**

```bash
git add assets/js/visualization.js assets/css/visualization.css
git commit -m "$(cat <<'EOF'
feat: 교환 단계에 자리 교대 모션 추가

step 데이터만 비교해 교환을 자동 감지한다. 정확히 두 위치의 값이 서로
맞바뀐 경우만 인정하므로 잘못된 모션이 나올 위험이 없다.

슬라이드는 옛 값을 유지한 채 진행하고 끝나는 순간 transform 리셋과 값
커밋을 동시에 한다. 각 화면 위치의 내용이 그 순간 같아 이어져 보인다.

자동 재생 중에는 모션 시간을 재생 간격에 맞춰 조이고, 단계 난타 시에는
진행 중 모션을 즉시 종료해 잔여 인라인 스타일을 남기지 않는다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 4: 엔진 — 앞으로 복사(shift) 모션

삽입 정렬은 `arr[j + 1] = arr[j]`로 값을 복제하므로 값 구성이 달라지고 교환 감지기에 걸리지 않는다. 별도 감지기와 고스트 모션이 필요하다.

**이 태스크는 독립적으로 거절 가능하다.** 없어도 삽입 정렬은 Task 2의 부드러운 값 전환으로 동작한다.

**Files:**
- Modify: `assets/js/visualization.js`
- Modify: `assets/css/visualization.css`

**Interfaces:**
- Consumes: Task 3의 `renderStep`, `pendingFinish`, `prevValues`, `moveDuration`
- Produces:
  - `detectCopyForward(prev, next) -> { from: number, to: number } | null`
  - `animateCopy(cells, from, to, durMs, onDone) -> function`

- [ ] **Step 1: 실패하는 검사를 준비한다**

4강 두 번째 시각화(삽입 정렬)에서 "한 칸 뒤로 밉니다" 캡션 단계를 찾는다.

```javascript
(function () {
    var vizes = Array.prototype.slice.call(document.querySelectorAll('.viz'));
    var viz = vizes.filter(function (v) {
        return v.querySelector('.viz__title').textContent.indexOf('삽입') !== -1;
    })[0];
    var next = Array.prototype.slice.call(viz.querySelectorAll('.viz__controls .button'))
        .filter(function (b) { return b.textContent.indexOf('다음 단계') !== -1; })[0];
    var sawGhost = false;
    for (var i = 0; i < 10; i += 1) {
        next.click();
        if (document.querySelectorAll('.viz-cell--ghost').length) sawGhost = true;
        if (viz.querySelector('.viz__caption').textContent.indexOf('뒤로 밉니다') !== -1) break;
    }
    return { caption: viz.querySelector('.viz__caption').textContent.trim().slice(0, 50), sawGhost: sawGhost };
})()
```

기대(현재, 실패): `sawGhost: false`

- [ ] **Step 2: 감지 함수를 추가한다**

Task 3의 `detectSwap` 바로 뒤에 삽입한다.

```javascript
    /* 앞으로 복사: 인접한 한 위치만 달라지고 그 값이 왼쪽 이웃에서 왔다.
     * 삽입 정렬의 arr[j + 1] = arr[j] 패턴. key를 배열 밖 변수에 들고 있어
     * 값이 복제되므로 값 구성이 달라지고 교환 감지기에는 걸리지 않는다.
     */
    function detectCopyForward(prev, next) {
        if (!prev || !next || prev.length !== next.length) return null;
        var diff = [];
        for (var i = 0; i < next.length; i += 1) {
            if (prev[i] !== next[i]) {
                diff.push(i);
                if (diff.length > 1) return null;
            }
        }
        if (diff.length !== 1) return null;
        var to = diff[0];
        if (to > 0 && next[to] === prev[to - 1]) {
            return { from: to - 1, to: to };
        }
        return null;
    }
```

- [ ] **Step 3: 고스트 애니메이션을 추가한다**

`animateSwap` 바로 뒤에 삽입한다. 무대가 가로 스크롤될 수 있어 `position: fixed`로 뷰포트 좌표를 쓴다.

```javascript
    function animateCopy(cells, from, to, durMs, onDone) {
        var rf = cells[from].getBoundingClientRect();
        var rt = cells[to].getBoundingClientRect();
        var ghost = cells[from].cloneNode(true);
        var finished = false;

        ghost.className = cells[from].className + " viz-cell--ghost";
        ghost.style.position = "fixed";
        ghost.style.left = rf.left + "px";
        ghost.style.top = rf.top + "px";
        ghost.style.width = rf.width + "px";
        ghost.style.height = rf.height + "px";
        ghost.style.margin = "0";
        document.body.appendChild(ghost);
        cells[to].classList.add("is-vacating");

        function finish() {
            if (finished) return;
            finished = true;
            if (ghost.parentNode) ghost.parentNode.removeChild(ghost);
            cells[to].classList.remove("is-vacating");
            onDone();
        }

        /* 다음 프레임에 목표 위치를 지정해야 전환이 발동한다 */
        requestAnimationFrame(function () {
            ghost.style.transition = "transform " + durMs + "ms var(--ease-out)";
            ghost.style.transform = "translateX(" + (rt.left - rf.left) + "px)";
        });

        setTimeout(finish, durMs + 60);
        return finish;
    }
```

- [ ] **Step 4: `renderStep`에 분기를 추가한다**

Task 3의 `renderStep`에서 `var swap = ...` 아래에 복사 감지를 더하고, 분기를 확장한다.

```javascript
            var nextValues = arrayValuesOf(step.view);
            var swap = reducedMotion ? null : detectSwap(prevValues, nextValues);
            var copy = (reducedMotion || swap) ? null : detectCopyForward(prevValues, nextValues);

            if (!swap && !copy) {
                commitStep(step);
                return;
            }

            var cells = stage.querySelectorAll(".viz-array .viz-cell");
            if (cells.length !== nextValues.length) {
                commitStep(step);
                return;
            }

            if (swap) {
                pendingFinish = animateSwap(cells, swap.a, swap.b, moveDuration(), function () {
                    pendingFinish = null;
                    commitStep(step);
                });
            } else {
                pendingFinish = animateCopy(cells, copy.from, copy.to, moveDuration(), function () {
                    pendingFinish = null;
                    commitStep(step);
                });
            }
```

- [ ] **Step 5: 고스트와 비워지는 셀의 스타일을 추가한다**

`assets/css/visualization.css`의 `.viz-cell.is-moving` 뒤에 추가한다.

```css
/* 앞으로 복사 중 날아가는 사본 */
.viz-cell--ghost {
    z-index: 30;
    pointer-events: none;
    box-shadow: var(--shadow-pop);
    opacity: 0.92;
}

/* 값이 덮어써질 셀은 옅어진다 */
.viz-cell.is-vacating {
    opacity: 0.45;
    transition: opacity var(--dur-fast) var(--ease-out);
}
```

- [ ] **Step 6: 고스트가 보이는지 확인한다**

Step 1의 스니펫을 다시 실행한다.

기대: `sawGhost: true`

- [ ] **Step 7: 고스트가 남지 않는지 확인한다**

```javascript
(function () {
    var vizes = Array.prototype.slice.call(document.querySelectorAll('.viz'));
    var viz = vizes.filter(function (v) {
        return v.querySelector('.viz__title').textContent.indexOf('삽입') !== -1;
    })[0];
    var next = Array.prototype.slice.call(viz.querySelectorAll('.viz__controls .button'))
        .filter(function (b) { return b.textContent.indexOf('다음 단계') !== -1; })[0];
    for (var i = 0; i < 40; i += 1) next.click();
    return {
        strayGhosts: document.querySelectorAll('.viz-cell--ghost').length,
        strayVacating: document.querySelectorAll('.viz-cell.is-vacating').length
    };
})()
```

기대: 둘 다 `0`. 페이지를 스크롤해도 화면에 떠 있는 셀이 없어야 한다.

- [ ] **Step 8: 커밋**

```bash
git add assets/js/visualization.js assets/css/visualization.css
git commit -m "$(cat <<'EOF'
feat: 삽입 정렬의 앞으로 복사에 고스트 모션 추가

arr[j + 1] = arr[j]는 값을 복제해 값 구성이 달라지므로 교환 감지기에
걸리지 않는다. 인접 한 칸 복사를 따로 감지해 사본이 날아가는 모션을 낸다.

덮어써질 셀은 옅어져 어느 값이 사라지는지 보이게 한다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 5: 상태 모션 문법

상태마다 고유한 모션 어휘를 준다. 색 외에 형태·위치 신호를 함께 유지해 접근성 규칙을 지킨다.

**Files:**
- Modify: `assets/css/visualization.css:66-129` (셀 상태 규칙), 파일 끝에 `@keyframes` 추가

**Interfaces:**
- Consumes: Task 1의 모션 토큰
- Produces: `@keyframes viz-pulse`, `@keyframes viz-settle`

- [ ] **Step 1: 셀 전환을 토큰으로 바꾸고 상태별 모션을 준다**

`assets/css/visualization.css:77`의 전환 선언을 교체한다.

```css
    transition: transform var(--dur-base) var(--ease-out),
                background var(--dur-fast) var(--ease-out),
                border-color var(--dur-fast) var(--ease-out),
                opacity var(--dur-base) var(--ease-out);
```

- [ ] **Step 2: 확정과 발견에 모션을 준다**

`.viz-cell--done`과 `.viz-cell--found` 규칙을 아래로 교체한다.

```css
.viz-cell--done {
    border-color: var(--state-done);
    background: var(--state-done-bg);
    animation: viz-settle var(--dur-slow) var(--ease-settle);
}

.viz-cell--found {
    border-color: var(--state-done);
    background: var(--state-done);
    color: #fff;
    animation: viz-pulse var(--dur-slow) var(--ease-out);
}
```

- [ ] **Step 3: 버려진 셀을 축소한다**

`.viz-cell--faded` 규칙을 교체한다. 투명도만으로 구분하지 않도록 크기 신호를 더한다.

```css
.viz-cell--faded {
    opacity: 0.35;
    transform: scale(0.92);
}
```

- [ ] **Step 4: 키프레임을 추가한다**

`assets/css/visualization.css` 파일 맨 끝(`@media (max-width: 640px)` 블록 **앞**)에 추가한다.

```css
/* ---------- 상태 모션 키프레임 ---------- */

/* 확정 — 제자리에 가라앉는 느낌 */
@keyframes viz-settle {
    0%   { transform: translateY(-4px); }
    100% { transform: translateY(0); }
}

/* 발견 — 한 번의 짧은 맥박 */
@keyframes viz-pulse {
    0%   { transform: scale(1); }
    45%  { transform: scale(1.09); }
    100% { transform: scale(1); }
}
```

- [ ] **Step 5: 표 셀에는 변형이 적용되지 않게 유지한다**

`visualization.css:180-187`의 표 셀 예외 규칙에 `animation: none;`을 더한다. 표는 셀 크기가 고정이라 변형이 레이아웃을 흔든다.

```css
.viz-table td.viz-cell--compare,
.viz-table td.viz-cell--current,
.viz-table td.viz-cell--done,
.viz-table td.viz-cell--updated,
.viz-table td.viz-cell--error,
.viz-table td.viz-cell--faded {
    transform: none;
    animation: none;
}
```

- [ ] **Step 6: 확인한다**

11강(DP 테이블)과 5강(이진 탐색 — `found` 상태 있음)에서 아래를 실행한다.

```javascript
(function () {
    var viz = document.querySelector('.viz');
    var next = Array.prototype.slice.call(viz.querySelectorAll('.viz__controls .button'))
        .filter(function (b) { return b.textContent.indexOf('다음 단계') !== -1; })[0];
    var total = parseInt(viz.querySelector('.viz__counter').textContent.split('/')[1], 10);
    var tableShifted = 0;
    for (var i = 1; i < total; i += 1) {
        next.click();
        Array.prototype.slice.call(viz.querySelectorAll('.viz-table td')).forEach(function (td) {
            var t = getComputedStyle(td).transform;
            if (t && t !== 'none') tableShifted += 1;
        });
    }
    return { total: total, tableShifted: tableShifted };
})()
```

기대: `tableShifted: 0` — 표 셀은 변형되지 않는다.

- [ ] **Step 7: 커밋**

```bash
git add assets/css/visualization.css
git commit -m "$(cat <<'EOF'
feat: 시각화 상태별 모션 문법 정리

확정은 가라앉는 정착, 발견은 한 번의 맥박, 버려진 셀은 축소로 구분한다.
투명도만으로 구분하지 않도록 크기 신호를 함께 준다.

표 셀은 크기가 고정이라 변형·애니메이션을 계속 차단한다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 6: 랜딩 히어로 재설계

무대를 넓히고 재생 제어를 준다. 현재 `setInterval` 무한 재생을 멈출 방법이 없다.

**Files:**
- Modify: `index.html:41-46` (히어로 시각화 마크업)
- Modify: `assets/js/landing.js:166-249` (히어로 로직)
- Modify: `assets/css/landing.css:58-129` (히어로 스타일)

**Interfaces:**
- Consumes: Task 1의 모션 토큰
- Produces: DOM id `hero-viz-bars`, `hero-viz-caption`, `hero-viz-timeline`, `hero-viz-toggle`, `hero-viz-shuffle`

- [ ] **Step 1: 실패하는 검사를 준비한다**

랜딩에서 실행한다.

```javascript
({
    hasToggle: !!document.getElementById('hero-viz-toggle'),
    hasShuffle: !!document.getElementById('hero-viz-shuffle'),
    hasTimeline: !!document.getElementById('hero-viz-timeline')
})
```

기대(현재, 실패): 세 값 모두 `false`

- [ ] **Step 2: 마크업을 교체한다**

`index.html`의 `.hero-viz` 블록을 아래로 교체한다.

```html
            <div class="hero-viz">
                <p class="hero-viz__label">LIVE — SELECTION SORT · 4강에서 직접 구현합니다</p>
                <div class="hero-viz__bars" id="hero-viz-bars" role="img"
                     aria-label="선택 정렬이 배열을 단계별로 정렬하는 라이브 시각화"></div>
                <p class="hero-viz__caption" id="hero-viz-caption"></p>
                <div class="hero-viz__foot">
                    <ol class="hero-viz__timeline" id="hero-viz-timeline" aria-label="회차 진행"></ol>
                    <div class="hero-viz__controls">
                        <button type="button" class="hero-viz__btn" id="hero-viz-toggle"
                                aria-label="자동 재생 일시 정지">⏸</button>
                        <button type="button" class="hero-viz__btn" id="hero-viz-shuffle"
                                aria-label="새 데이터로 다시 정렬">🎲</button>
                    </div>
                </div>
            </div>
```

- [ ] **Step 3: 히어로 로직을 교체한다**

`assets/js/landing.js`의 `/* ---------- 히어로 라이브 선택 정렬 ---------- */` 주석부터 파일의 `});` 앞까지를 아래로 교체한다. `buildFrames`는 그대로 재사용하므로 남긴다.

```javascript
        /* ---------- 히어로 라이브 선택 정렬 ---------- */
        var barsHost = document.getElementById("hero-viz-bars");
        var captionHost = document.getElementById("hero-viz-caption");
        var timelineHost = document.getElementById("hero-viz-timeline");
        var toggleBtn = document.getElementById("hero-viz-toggle");
        var shuffleBtn = document.getElementById("hero-viz-shuffle");
        if (!barsHost) return;

        var reducedMotion = window.matchMedia &&
            window.matchMedia("(prefers-reduced-motion: reduce)").matches;

        var MAX_VALUE = 44;

        function randomValues() {
            var out = [];
            while (out.length < 7) {
                var v = 6 + Math.floor(Math.random() * (MAX_VALUE - 6));
                if (out.indexOf(v) === -1) out.push(v);   /* 중복 없는 값 — 교환 표시가 명확해진다 */
            }
            return out;
        }

        var values = [34, 12, 27, 8, 40, 19, 31];

        /* 선택 정렬의 모든 단계를 미리 만든다 (시각화와 실제 알고리즘 동작 일치) */
        function buildFrames(arr) {
            var a = arr.slice();
            var frames = [];
            for (var i = 0; i < a.length - 1; i += 1) {
                var minIndex = i;
                for (var j = i + 1; j < a.length; j += 1) {
                    frames.push({
                        arr: a.slice(), sortedUpto: i - 1, min: minIndex, compare: j, round: i,
                        text: "인덱스 " + j + "의 값 " + a[j] + "을(를) 현재 최솟값 " + a[minIndex] + "과(와) 비교합니다."
                    });
                    if (a[j] < a[minIndex]) {
                        minIndex = j;
                        frames.push({
                            arr: a.slice(), sortedUpto: i - 1, min: minIndex, compare: -1, round: i,
                            text: "새로운 최솟값 발견: " + a[minIndex] + " (인덱스 " + minIndex + ")"
                        });
                    }
                }
                var tmp = a[i];
                a[i] = a[minIndex];
                a[minIndex] = tmp;
                frames.push({
                    arr: a.slice(), sortedUpto: i, min: -1, compare: -1, round: i,
                    text: (i + 1) + "회차 완료 — " + a[i] + "이(가) 인덱스 " + i + "에 확정되었습니다."
                });
            }
            frames.push({
                arr: a.slice(), sortedUpto: a.length - 1, min: -1, compare: -1, round: a.length - 1,
                text: "정렬 완료! 이 과정을 4강에서 직접 구현합니다."
            });
            return frames;
        }

        var frames = [];
        var frameIndex = 0;
        var bars = [];
        var timer = null;

        function buildBars() {
            barsHost.textContent = "";
            bars = values.map(function (value) {
                var bar = el("div", "hero-viz__bar");
                bar.appendChild(el("span", "hero-viz__bar-value", value));
                barsHost.appendChild(bar);
                return bar;
            });
        }

        function buildTimeline() {
            if (!timelineHost) return;
            timelineHost.textContent = "";
            for (var i = 0; i < values.length - 1; i += 1) {
                timelineHost.appendChild(el("li", "hero-viz__tick"));
            }
        }

        function renderFrame(frame) {
            frame.arr.forEach(function (value, i) {
                var bar = bars[i];
                bar.style.height = Math.round((value / MAX_VALUE) * 100) + "%";
                bar.querySelector(".hero-viz__bar-value").textContent = value;
                bar.classList.toggle("is-done", i <= frame.sortedUpto);
                bar.classList.toggle("is-min", i === frame.min);
                bar.classList.toggle("is-compare", i === frame.compare);
            });
            if (captionHost) {
                captionHost.textContent = "";
                captionHost.appendChild(el("b", null, "선택 정렬 실행 중 · "));
                captionHost.appendChild(document.createTextNode(frame.text));
            }
            if (timelineHost) {
                Array.prototype.forEach.call(timelineHost.children, function (tick, i) {
                    tick.classList.toggle("is-done", i < frame.round);
                    tick.classList.toggle("is-current", i === frame.round);
                });
            }
        }

        function stopAuto() {
            if (timer) {
                clearInterval(timer);
                timer = null;
            }
            if (toggleBtn) {
                toggleBtn.textContent = "▶";
                toggleBtn.setAttribute("aria-label", "자동 재생 시작");
            }
        }

        function startAuto() {
            if (timer) return;
            timer = setInterval(function () {
                frameIndex = (frameIndex + 1) % frames.length;
                renderFrame(frames[frameIndex]);
            }, 1100);
            if (toggleBtn) {
                toggleBtn.textContent = "⏸";
                toggleBtn.setAttribute("aria-label", "자동 재생 일시 정지");
            }
        }

        function reset(nextValues) {
            stopAuto();
            values = nextValues;
            frames = buildFrames(values);
            frameIndex = 0;
            buildBars();
            buildTimeline();
            renderFrame(frames[0]);
        }

        reset(values);

        if (toggleBtn) {
            toggleBtn.addEventListener("click", function () {
                if (timer) {
                    stopAuto();
                } else {
                    startAuto();
                }
            });
        }

        if (shuffleBtn) {
            shuffleBtn.addEventListener("click", function () {
                var wasPlaying = !!timer;
                reset(randomValues());
                if (wasPlaying && !reducedMotion) startAuto();
            });
        }

        if (!reducedMotion) {
            startAuto();
        } else {
            stopAuto();
            if (captionHost) {
                captionHost.textContent =
                    "선택 정렬의 한 장면입니다. 애니메이션 축소 설정이 감지되어 자동 재생을 멈췄습니다. ▶ 버튼으로 직접 넘겨볼 수 있습니다.";
            }
        }
    });
})();
```

- [ ] **Step 4: 히어로 스타일을 갱신한다**

`assets/css/landing.css`에서 `.hero-viz__bars` 높이를 키우고, 값 라벨을 막대 위로 올리고, 새 요소 스타일을 추가한다.

```css
.hero-viz__bars {
    display: flex;
    align-items: flex-end;
    gap: 8px;
    height: 170px;
    padding-top: 22px;      /* 막대 위 값 라벨 자리 */
}

.hero-viz__bar-value {
    position: absolute;
    top: -20px;
    left: 0;
    right: 0;
    text-align: center;
    font-family: var(--font-mono);
    font-size: 0.72rem;
    color: var(--ink-faint);
    transition: color var(--dur-fast) var(--ease-out);
}

.hero-viz__bar.is-compare .hero-viz__bar-value { color: var(--state-compare); font-weight: 700; }
.hero-viz__bar.is-min .hero-viz__bar-value { color: var(--state-visit); font-weight: 700; }
.hero-viz__bar.is-done .hero-viz__bar-value { color: var(--state-done); }

.hero-viz__caption {
    margin: 16px 0 0;
    font-size: 0.86rem;
    color: var(--ink-soft);
    min-height: 44px;
}

.hero-viz__foot {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-top: 6px;
}

.hero-viz__timeline {
    display: flex;
    gap: 6px;
    list-style: none;
    margin: 0;
    padding: 0;
}

.hero-viz__tick {
    width: 9px;
    height: 9px;
    border-radius: 999px;
    border: 1.5px solid var(--line-strong);
    background: transparent;
    transition: background var(--dur-base) var(--ease-out),
                border-color var(--dur-base) var(--ease-out),
                transform var(--dur-base) var(--ease-settle);
}

.hero-viz__tick.is-done {
    background: var(--state-done);
    border-color: var(--state-done);
}

.hero-viz__tick.is-current {
    border-color: var(--brand);
    background: var(--brand-soft);
    transform: scale(1.35);
}

.hero-viz__controls {
    display: flex;
    gap: 6px;
}

.hero-viz__btn {
    font: inherit;
    font-size: 0.9rem;
    line-height: 1;
    padding: 6px 10px;
    border-radius: 8px;
    border: 1px solid var(--line-strong);
    background: var(--surface);
    color: var(--ink);
    cursor: pointer;
    transition: border-color var(--dur-fast) var(--ease-out),
                background var(--dur-fast) var(--ease-out);
}

.hero-viz__btn:hover {
    border-color: var(--brand);
    background: var(--surface-2);
}
```

기존 `.hero-viz__bar span` 규칙은 `.hero-viz__bar-value`로 대체되었으므로 삭제한다. `.hero-viz__bar`에 `position: relative`가 이미 있으므로(`landing.css:89`) 값 라벨의 절대 위치가 동작한다.

- [ ] **Step 5: 컨트롤이 동작하는지 확인한다**

```javascript
(function () {
    var toggle = document.getElementById('hero-viz-toggle');
    var shuffle = document.getElementById('hero-viz-shuffle');
    var readValues = function () {
        return Array.prototype.slice.call(document.querySelectorAll('.hero-viz__bar-value'))
            .map(function (n) { return n.textContent; }).join(',');
    };
    var pausedLabel = (toggle.click(), toggle.textContent);       // 일시 정지 후 라벨
    var before = readValues();
    var stillSame = true;
    return new Promise(function (resolve) {
        setTimeout(function () {
            stillSame = readValues() === before;                  // 정지했으면 변하지 않는다
            var beforeShuffle = readValues();
            shuffle.click();
            resolve({
                pausedLabel: pausedLabel,
                frozenWhilePaused: stillSame,
                shuffleChanged: readValues() !== beforeShuffle,
                tickCount: document.querySelectorAll('.hero-viz__tick').length
            });
        }, 2500);
    });
})()
```

기대: `pausedLabel: "▶"`, `frozenWhilePaused: true`, `shuffleChanged: true`, `tickCount: 6`

- [ ] **Step 6: reduced-motion에서 자동 재생이 없는지 확인한다**

`prefers-reduced-motion: reduce`를 강제하고 랜딩을 새로 로드한 뒤 실행한다.

```javascript
({
    toggleLabel: document.getElementById('hero-viz-toggle').textContent,
    caption: document.getElementById('hero-viz-caption').textContent.slice(0, 30)
})
```

기대: `toggleLabel: "▶"` (정지 상태), 캡션에 애니메이션 축소 안내 포함. `▶`를 눌러 수동 재생이 되어야 한다.

- [ ] **Step 7: 커밋**

```bash
git add index.html assets/js/landing.js assets/css/landing.css
git commit -m "$(cat <<'EOF'
feat: 랜딩 히어로 시각화 재설계

무대를 넓히고 값 라벨을 막대 위로 올린다. 회차 진행 타임라인을 추가하고
재생/일시정지와 새 데이터 버튼을 붙인다.

기존에는 setInterval 무한 재생을 멈출 수단이 없었다. reduced-motion에서는
자동 재생을 하지 않되 수동 재생은 가능하게 한다.

새 데이터는 중복 없는 값을 생성해 교환이 명확히 보이게 한다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 7: 스크롤 진입 애니메이션

섹션·카드가 뷰포트에 들어올 때 1회 페이드 + 상승. 목차 활성화에 쓰는 기존 `IntersectionObserver` 패턴(`common.js:187`)을 따른다.

**Files:**
- Modify: `assets/js/common.js` (진입 관찰 함수 추가)
- Modify: `assets/css/common.css` (진입 상태 클래스)
- Modify: `assets/css/print.css` (인쇄 시 강제 표시)

**Interfaces:**
- Consumes: Task 1의 모션 토큰
- Produces: CSS 클래스 `.reveal-on-scroll`, `.is-revealed`

- [ ] **Step 1: 실패하는 검사를 준비한다**

강의 페이지에서 실행한다.

```javascript
({
    marked: document.querySelectorAll('.reveal-on-scroll').length,
    revealed: document.querySelectorAll('.is-revealed').length
})
```

기대(현재, 실패): 둘 다 `0`

- [ ] **Step 2: 진입 스타일을 추가한다**

`assets/css/common.css`의 reduced-motion 블록 **앞**에 추가한다. `reveal-box`와 이름이 겹치지 않게 `reveal-on-scroll`을 쓴다.

```css
/* ---------- 스크롤 진입 ---------- */
.reveal-on-scroll {
    opacity: 0;
    transform: translateY(14px);
    transition: opacity var(--dur-slow) var(--ease-out),
                transform var(--dur-slow) var(--ease-out);
}

.reveal-on-scroll.is-revealed {
    opacity: 1;
    transform: translateY(0);
}
```

reduced-motion 블록 안에 안전장치를 더한다. JS가 관찰을 걸지 않지만 CSS만으로도 내용이 보이게 한다.

```css
    .reveal-on-scroll {
        opacity: 1;
        transform: none;
    }
```

- [ ] **Step 3: 인쇄 시 강제 표시한다**

`assets/css/print.css`의 `@media print` 블록 안에 추가한다. 진입 전 상태가 인쇄물에 남으면 내용이 사라진다.

```css
    .reveal-on-scroll {
        opacity: 1 !important;
        transform: none !important;
    }
```

- [ ] **Step 4: 관찰 로직을 추가한다**

`assets/js/common.js`의 `DOMContentLoaded` 핸들러 안, 목차 관찰 코드 뒤에 추가한다.

```javascript
        /* ---------- 스크롤 진입 ---------- */
        (function () {
            var reduced = window.matchMedia &&
                window.matchMedia("(prefers-reduced-motion: reduce)").matches;

            var targets = document.querySelectorAll(
                ".lesson-section, .course-card, .how-card, .stat-tile");
            if (!targets.length) return;

            /* reduced-motion이거나 IntersectionObserver가 없으면 그냥 보여준다 */
            if (reduced || !("IntersectionObserver" in window)) return;

            Array.prototype.forEach.call(targets, function (node) {
                node.classList.add("reveal-on-scroll");
            });

            var observer = new IntersectionObserver(function (entries) {
                entries.forEach(function (entry) {
                    if (!entry.isIntersecting) return;
                    entry.target.classList.add("is-revealed");
                    observer.unobserve(entry.target);   /* 1회만 — 되돌아가도 재생 안 함 */
                });
            }, { rootMargin: "0px 0px -8% 0px", threshold: 0.05 });

            Array.prototype.forEach.call(targets, function (node) {
                observer.observe(node);
            });
        })();
```

- [ ] **Step 5: 진입이 동작하는지 확인한다**

강의 페이지를 새로 로드한 뒤 실행한다.

```javascript
(function () {
    var total = document.querySelectorAll('.reveal-on-scroll').length;
    var revealedAtTop = document.querySelectorAll('.is-revealed').length;
    window.scrollTo(0, document.body.scrollHeight);
    return new Promise(function (resolve) {
        setTimeout(function () {
            resolve({
                total: total,
                revealedAtTop: revealedAtTop,
                revealedAfterScroll: document.querySelectorAll('.is-revealed').length,
                allVisible: Array.prototype.slice.call(document.querySelectorAll('.reveal-on-scroll'))
                    .every(function (n) { return parseFloat(getComputedStyle(n).opacity) > 0.99; })
            });
        }, 1200);
    });
})()
```

기대: `total: 20` (강의 섹션 20개), `revealedAtTop`이 `total`보다 작고, `revealedAfterScroll === total`, `allVisible: true`.

- [ ] **Step 6: 인쇄에서 내용이 사라지지 않는지 확인한다**

```javascript
(function () {
    var hidden = 0;
    document.querySelectorAll('.reveal-on-scroll').forEach(function (n) {
        n.classList.remove('is-revealed');       // 진입 전 상태로 되돌린다
    });
    /* 인쇄 미디어 규칙이 적용되는지 확인 */
    return { restoredToPreReveal: document.querySelectorAll('.reveal-on-scroll:not(.is-revealed)').length };
})()
```

그다음 브라우저 인쇄 미리보기를 열어 모든 섹션이 보이는지 눈으로 확인한다. 학생용·교수자용 인쇄 버튼 양쪽 다 확인한다.

- [ ] **Step 7: 커밋**

```bash
git add assets/js/common.js assets/css/common.css assets/css/print.css
git commit -m "$(cat <<'EOF'
feat: 섹션·카드 스크롤 진입 애니메이션

뷰포트에 들어올 때 1회 페이드+상승. 되돌아가도 재생하지 않도록 관찰을
해제한다. reduced-motion이거나 IntersectionObserver가 없으면 클래스를
붙이지 않아 내용이 즉시 보인다.

인쇄에서 진입 전 상태가 남아 내용이 사라지지 않도록 print.css에서
강제 표시한다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 8: 핵심 정리 카드 승급 (13개 강의)

강의의 결론부인 "오늘의 핵심 정리"가 아무 장식 없는 `<ul>`이라 가장 안 보인다. 전용 카드로 올린다.

**Files:**
- Modify: `algorithms/*.html` (13개) — `#sec-summary`의 `<ul>`에 클래스 삽입
- Modify: `assets/css/lesson.css` — `.summary-list` 규칙 추가
- Modify: `scripts/validate.mjs` — 13개 강의 모두 보유하는지 검사

**Interfaces:**
- Consumes: Task 1의 모션 토큰
- Produces: CSS 클래스 `.summary-list`

- [ ] **Step 1: 실패하는 검사를 추가한다**

`scripts/validate.mjs`의 강의 HTML 루프 안(`/* 21개 섹션 순서 검증 */` 블록 뒤)에 추가한다.

```javascript
    /* 핵심 정리 섹션이 전용 카드 클래스를 쓰는지 */
    const summaryMatch = html.match(/id="sec-summary"[\s\S]*?<\/section>/);
    if (!summaryMatch) {
        fail(`${lesson.path}: sec-summary 섹션을 찾을 수 없음`);
    } else if (!summaryMatch[0].includes('class="summary-list"')) {
        fail(`${lesson.path}: 핵심 정리 목록에 summary-list 클래스 없음`);
    }
```

- [ ] **Step 2: 검사가 실패하는 것을 확인한다**

```bash
node scripts/validate.mjs
```

기대: exit 1, 13건 실패 (`핵심 정리 목록에 summary-list 클래스 없음`).

- [ ] **Step 3: 13개 강의에 클래스를 삽입한다**

`#sec-summary` 안의 첫 `<ul>`에만 클래스를 넣는다. 파이썬으로 일괄 처리하고 결과를 검증한다.

```bash
cd /d/Github/Algorithm_WS/Algorithm26/algorithms && PYTHONIOENCODING=utf-8 python - <<'PYEOF'
import glob, re

changed = 0
for path in sorted(glob.glob('*.html')):
    src = open(path, encoding='utf-8').read()
    start = src.index('id="sec-summary"')
    end = src.index('</section>', start)
    body = src[start:end]
    assert 'class="summary-list"' not in body, path
    new_body, n = re.subn(r'<ul>', '<ul class="summary-list">', body, count=1)
    assert n == 1, f'{path}: sec-summary 안에 ul이 없다'
    src = src[:start] + new_body + src[end:]
    open(path, 'w', encoding='utf-8', newline='').write(src)
    changed += 1
print('수정한 파일:', changed)
PYEOF
```

기대 출력: `수정한 파일: 13`

- [ ] **Step 4: 카드 스타일을 추가한다**

`assets/css/lesson.css`의 `.objective-list` 규칙 뒤에 추가한다.

```css
/* 오늘의 핵심 정리 — 강의의 결론부를 전용 카드로 */
.summary-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: grid;
    gap: 10px;
    counter-reset: summary;
}

.summary-list > li {
    counter-increment: summary;
    display: flex;
    gap: 12px;
    background: var(--surface-2);
    border: 1px solid var(--line);
    border-left: 3px solid var(--brand);
    border-radius: var(--radius-s);
    padding: 13px 16px;
    transition: border-color var(--dur-fast) var(--ease-out),
                background var(--dur-fast) var(--ease-out);
}

.summary-list > li::before {
    content: counter(summary);
    font-family: var(--font-mono);
    font-size: 0.76rem;
    font-weight: 700;
    color: var(--brand);
    background: var(--brand-soft);
    border-radius: 6px;
    padding: 1px 7px;
    height: fit-content;
    flex-shrink: 0;
}

.summary-list > li:hover {
    border-left-color: var(--state-done);
    background: var(--surface);
}
```

- [ ] **Step 5: 검사가 통과하는 것을 확인한다**

```bash
node scripts/validate.mjs
```

기대: `모든 검증 통과 ✓`

- [ ] **Step 6: 인쇄에서 번호가 보이는지 확인한다**

`::before`의 배경색이 흑백 인쇄에서 사라져도 숫자는 남아야 한다. 강의 페이지 인쇄 미리보기로 핵심 정리 섹션을 확인한다. 번호와 텍스트가 모두 읽히면 통과.

- [ ] **Step 7: 커밋**

```bash
git add algorithms assets/css/lesson.css scripts/validate.mjs
git commit -m "$(cat <<'EOF'
feat: 오늘의 핵심 정리를 전용 카드로 승급

강의의 결론부인데 아무 장식 없는 ul이라 가장 안 보였다. 번호 배지와
좌측 강조 바가 있는 카드로 올린다.

13개 강의 모두 summary-list 클래스를 갖는지 validate.mjs로 검사한다.
본문 텍스트는 건드리지 않고 클래스만 삽입했다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 9: note-box·본문 강조·죽은 CSS 제거

138곳에 쓰이는 `note-box`의 위계를 정리하고, 본문 `strong`에 마킹을 주고, 사용 0건인 `inline-array` 규칙을 지운다.

**Files:**
- Modify: `assets/css/common.css:638-673` (`note-box`), `assets/css/lesson.css:211-213` (`strong`), `assets/css/lesson.css:174-194` (섹션 헤더)
- Modify: `assets/css/lesson.css:259-298` (삭제)
- Modify: `scripts/validate.mjs` (죽은 CSS 재유입 방지)

**Interfaces:**
- Consumes: Task 1의 모션 토큰
- Produces: 없음 (스타일만)

- [ ] **Step 1: 실패하는 검사를 추가한다**

Task 1에서 만든 CSS 정적 검사 섹션에 추가한다.

```javascript
/* 사용하지 않는 규칙이 다시 들어오지 않게 */
const lessonCss = readFileSync(join(ROOT, "assets/css/lesson.css"), "utf8");
if (lessonCss.includes(".inline-array")) {
    fail("lesson.css: 사용처가 없는 .inline-array 규칙이 남아 있음");
}
```

- [ ] **Step 2: 검사가 실패하는 것을 확인한다**

```bash
node scripts/validate.mjs
```

기대: exit 1, `lesson.css: 사용처가 없는 .inline-array 규칙이 남아 있음` 1건.

- [ ] **Step 3: 죽은 규칙을 삭제한다**

`assets/css/lesson.css`에서 `/* 정적 배열 그림 (텍스트 기반) */` 주석과 `.inline-array` 관련 6개 규칙(259-298줄)을 전부 삭제한다. 삭제 전 사용처가 정말 없는지 재확인한다.

```bash
cd /d/Github/Algorithm_WS/Algorithm26 && grep -rn "inline-array" --include=*.html --include=*.js . | grep -v node_modules | wc -l
```

기대: `0`

- [ ] **Step 4: note-box 위계를 정리한다**

`assets/css/common.css`의 `note-box` 규칙군을 아래로 교체한다. 변형마다 아이콘을 붙여 색만으로 구분하지 않게 한다.

```css
/* ---------- 안내 박스 ---------- */
.note-box {
    position: relative;
    border-radius: var(--radius-m);
    border: 1px solid var(--line);
    border-left: 4px solid var(--brand);
    background: var(--surface-2);
    padding: 14px 18px 14px 46px;
    margin: 16px 0;
    transition: border-color var(--dur-fast) var(--ease-out);
}

.note-box::before {
    content: "ℹ";
    position: absolute;
    left: 16px;
    top: 13px;
    font-size: 1rem;
    line-height: 1.3;
    color: var(--brand);
}

.note-box--tip {
    border-left-color: var(--state-done);
}

.note-box--tip::before {
    content: "✓";
    color: var(--state-done);
}

.note-box--warn {
    border-left-color: var(--state-compare);
}

.note-box--warn::before {
    content: "⚠";
    color: var(--state-compare);
}

.note-box--danger {
    border-left-color: var(--state-error);
}

.note-box--danger::before {
    content: "✕";
    color: var(--state-error);
}

.note-box > :first-child {
    margin-top: 0;
}

.note-box > :last-child {
    margin-bottom: 0;
}

.note-box__title {
    display: block;
    font-weight: 800;
    font-size: 0.9rem;
    margin-bottom: 4px;
    color: var(--ink);
}
```

- [ ] **Step 5: 본문 강조에 마킹을 준다**

`assets/css/lesson.css:211-213`의 `strong` 규칙을 교체한다. 배경을 아주 옅게 줘서 훑을 때 눈에 걸리게 한다.

```css
.lesson-section p strong,
.lesson-section li strong {
    color: var(--ink);
    background: linear-gradient(transparent 62%, var(--brand-soft) 62%);
}

/* 강조 위에 또 강조가 겹치는 곳(제목·박스 타이틀)은 마킹하지 않는다 */
.lesson-section .note-box__title,
.lesson-section h2 strong,
.lesson-section h3 strong,
.lesson-section h4 strong {
    background: none;
}
```

- [ ] **Step 6: 섹션 헤더 위계를 높인다**

`assets/css/lesson.css:174-194`의 `h2` 규칙에서 하단 보더를 브랜드색 그라데이션으로 바꾼다.

```css
.lesson-section > h2 {
    display: flex;
    align-items: baseline;
    gap: 12px;
    font-size: 1.32rem;
    font-weight: 800;
    margin: 0 0 18px;
    padding-bottom: 12px;
    border-bottom: 2px solid transparent;
    border-image: linear-gradient(90deg, var(--brand) 0%, var(--surface-2) 42%) 1;
}
```

- [ ] **Step 7: 검사와 화면을 확인한다**

```bash
node scripts/validate.mjs
```

기대: `모든 검증 통과 ✓`

브라우저에서 확인한다.

```javascript
(function () {
    var marked = Array.prototype.slice.call(
        document.querySelectorAll('.lesson-section p strong, .lesson-section li strong'));
    var titles = Array.prototype.slice.call(document.querySelectorAll('.note-box__title'));
    return {
        markedCount: marked.length,
        markedHaveBackground: marked.length > 0 &&
            getComputedStyle(marked[0]).backgroundImage !== 'none',
        titlesUnmarked: titles.every(function (t) {
            return getComputedStyle(t).backgroundImage === 'none';
        }),
        noteIcons: getComputedStyle(document.querySelector('.note-box'), '::before').content
    };
})()
```

기대: `markedHaveBackground: true`, `titlesUnmarked: true`, `noteIcons`가 빈 값이 아님.

라이트·다크 테마 양쪽에서 마킹이 읽히는지, 인쇄 미리보기에서 마킹이 텍스트를 가리지 않는지 눈으로 확인한다.

- [ ] **Step 8: 커밋**

```bash
git add assets/css/common.css assets/css/lesson.css scripts/validate.mjs
git commit -m "$(cat <<'EOF'
feat: 안내 박스 위계 정리와 본문 강조 마킹

note-box 변형마다 아이콘을 붙여 색만으로 구분하지 않게 한다(138곳 사용).
본문 strong에 옅은 밑줄 배경을 줘서 훑을 때 눈에 걸리게 하고, 제목·박스
타이틀처럼 이미 강조된 곳은 제외한다.

섹션 헤더 하단 보더를 브랜드색 그라데이션으로 바꿔 위계를 높인다.

사용처가 0건인 .inline-array 규칙 40줄을 삭제하고, 다시 들어오지 않도록
validate.mjs에 검사를 추가한다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 10: 읽는 진도 바와 최종 전체 검증

강의 페이지 상단에 읽는 진도 바를 두고, 전체를 통합 검증한다.

**Files:**
- Modify: `assets/js/common.js`
- Modify: `assets/css/lesson.css`
- Modify: `assets/css/print.css`

**Interfaces:**
- Consumes: Task 1의 모션 토큰
- Produces: DOM id `reading-progress`

- [ ] **Step 1: 실패하는 검사를 준비한다**

강의 페이지에서 실행한다.

```javascript
({ hasBar: !!document.getElementById('reading-progress') })
```

기대(현재, 실패): `false`

- [ ] **Step 2: 진도 바를 만든다**

`assets/js/common.js`의 `DOMContentLoaded` 핸들러 안, 스크롤 진입 코드 뒤에 추가한다.

```javascript
        /* ---------- 읽는 진도 바 (강의 페이지) ---------- */
        (function () {
            if (!document.body.hasAttribute("data-lesson-id")) return;

            var bar = document.createElement("div");
            bar.id = "reading-progress";
            bar.className = "reading-progress";
            var fill = document.createElement("div");
            fill.className = "reading-progress__fill";
            bar.appendChild(fill);
            document.body.appendChild(bar);

            var ticking = false;

            function update() {
                var doc = document.documentElement;
                var max = doc.scrollHeight - window.innerHeight;
                var ratio = max > 0 ? window.scrollY / max : 0;
                if (ratio < 0) ratio = 0;
                if (ratio > 1) ratio = 1;
                fill.style.width = (ratio * 100).toFixed(2) + "%";
                ticking = false;
            }

            window.addEventListener("scroll", function () {
                if (ticking) return;
                ticking = true;
                window.requestAnimationFrame(update);
            });
            window.addEventListener("resize", update);
            update();
        })();
```

- [ ] **Step 3: 스타일을 추가한다**

`assets/css/lesson.css` 파일 끝에 추가한다.

```css
/* ---------- 읽는 진도 바 ---------- */
.reading-progress {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: transparent;
    /* .site-header(100)보다 위, .skip-link(200)보다 아래 —
       헤더에 가리지 않으면서 접근성 링크는 계속 최상단에 둔다 */
    z-index: 150;
    pointer-events: none;
}

.reading-progress__fill {
    height: 100%;
    width: 0;
    background: linear-gradient(90deg, var(--state-compare), var(--brand), var(--state-done));
    transition: width var(--dur-fast) linear;
}
```

**주의:** `.site-header`는 `position: sticky; top: 0; z-index: 100`이다(`common.css:262-265`).
진도 바의 z-index가 이보다 작으면 헤더 뒤에 가려 전혀 보이지 않는다. 150을 쓴다.

- [ ] **Step 4: 인쇄에서 숨긴다**

`assets/css/print.css`의 `@media print` 블록에 추가한다.

```css
    .reading-progress {
        display: none !important;
    }
```

- [ ] **Step 5: 진도 바가 동작하는지 확인한다**

```javascript
(function () {
    var fill = document.querySelector('.reading-progress__fill');
    var atTop = fill.style.width;
    window.scrollTo(0, document.body.scrollHeight);
    return new Promise(function (resolve) {
        setTimeout(function () {
            resolve({ atTop: atTop, atBottom: fill.style.width });
        }, 600);
    });
})()
```

기대: `atTop`이 `"0.00%"`에 가깝고 `atBottom`이 `"100.00%"`.

- [ ] **Step 6: 최종 통합 검증**

아래를 모두 통과해야 한다.

```bash
node scripts/validate.mjs
```

기대: `모든 검증 통과 ✓`

브라우저 확인 목록.

1. **15개 시각화 전수** — 13개 강의를 모두 열어 Task 2 Step 7의 스니펫으로 `reachedEnd: true`, `errors: 0` 확인
2. **자동 재생 3속도** — 느리게/보통/빠르게에서 모션이 밀리지 않는지, 잔여 인라인 스타일이 없는지 확인

```javascript
(function () {
    var viz = document.querySelector('.viz');
    var select = viz.querySelector('.viz__speed select');
    var play = Array.prototype.slice.call(viz.querySelectorAll('.viz__controls .button'))
        .filter(function (b) { return b.textContent.indexOf('자동') !== -1; })[0];
    select.value = '450';
    select.dispatchEvent(new Event('change'));
    play.click();
    return new Promise(function (resolve) {
        setTimeout(function () {
            resolve({
                strayTransform: Array.prototype.slice.call(viz.querySelectorAll('.viz-cell'))
                    .filter(function (c) { return c.style.transform; }).length,
                strayGhosts: document.querySelectorAll('.viz-cell--ghost').length
            });
        }, 6000);
    });
})()
```

기대: 둘 다 `0`

3. **랜딩** — 재생/일시정지/새 데이터, 타임라인, 스크롤 진입
4. **인쇄** — 학생용·교수자용 양쪽 미리보기. 모든 섹션이 보이고 정답이 규칙대로 숨거나 보이고 진도 바가 없어야 한다
5. **reduced-motion** — 강제한 상태로 랜딩과 강의 3개를 확인. 이동 모션·자동 재생·스크롤 진입이 모두 없고 내용은 전부 보여야 한다
6. **라이트/다크** — 양쪽에서 강조 마킹과 상태 색이 읽히는지 확인

- [ ] **Step 7: 커밋**

```bash
git add assets/js/common.js assets/css/lesson.css assets/css/print.css
git commit -m "$(cat <<'EOF'
feat: 강의 페이지 읽는 진도 바 추가

상단 고정 바로 문서 내 위치를 알려준다. rAF로 스크롤 처리를 조이고
인쇄에서는 숨긴다.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
EOF
)"
```

---

## 자체 점검 결과

**스펙 커버리지**

| 스펙 항목 | 태스크 |
|---|---|
| 5.1 모션 토큰 | Task 1 |
| 5.2 엔진 키 기반 갱신 | Task 2 |
| 5.2 교환 감지 | Task 3 |
| 5.2 앞으로 복사 감지 | Task 4 |
| 5.2 카운터 칩 강조 | **미포함 — 아래 참고** |
| 5.3 상태 모션 문법 | Task 5 |
| 5.4 랜딩 히어로 | Task 6 |
| 5.5 스크롤 진입 | Task 7 |
| 5.6 핵심 정리 승급 | Task 8 |
| 5.6 note-box·strong·섹션 헤더·죽은 CSS | Task 9 |
| 5.6 읽는 진도 바 | Task 10 |
| 6. 검증 | 각 태스크 + Task 10 Step 6 |

**의도적으로 제외한 것**

스펙 5.2의 "카운터 칩은 값이 바뀐 칩만 짧게 강조한다"를 뺐다. `renderStep`이 매번 `stats`를 비우고 다시 만들기 때문에 칩에도 키 기반 갱신을 도입해야 하는데, 카운터는 숫자가 늘어나는 것이 캡션에 이미 적혀 있어 모션의 정보 가치가 낮다. 넣는다면 Task 3에 붙이는 것이 자연스럽지만 YAGNI로 판단했다. 원하면 별도 태스크로 추가할 수 있다.

**타입·이름 일관성 확인**

- `groupSignature`/`patchGroup`/`mountGroup`/`renderView`/`commitStep`/`moveDuration` — Task 2에서 정의, Task 3·4에서 같은 이름으로 사용
- `detectSwap`은 `{a, b}`, `detectCopyForward`는 `{from, to}` — 반환 형태가 다르므로 Task 4 Step 4에서 각각 맞는 필드로 호출
- `animateSwap`/`animateCopy` 모두 즉시 종료 함수를 반환하고 `pendingFinish`에 저장 — Task 3에서 도입한 규약을 Task 4가 따름
- CSS 클래스: `.reveal-on-scroll`은 기존 `details.reveal-box`와 이름이 겹치지 않는다
- `.summary-list`는 기존 `.objective-list`·`.checklist`와 다른 이름
