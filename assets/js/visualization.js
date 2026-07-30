/*
 * AlgoViz — 단계(step) 기반 알고리즘 시각화 엔진
 *
 * 사용법 (강의 페이지 인라인 스크립트):
 *   AlgoViz.create({
 *       mount: "#viz-selection-sort",
 *       title: "선택 정렬 단계별 실행",
 *       legend: ["compare", "current", "done"],          // 표시할 범례 (선택)
 *       makeSteps: function (input) { return steps; },   // 단계 배열 생성 함수
 *       makeInput: function () { return [5, 3, 8]; },    // "새 데이터" 버튼용 (선택)
 *       initialInput: [29, 10, 14, 37, 13]
 *   });
 *
 * 각 step 객체:
 *   {
 *       caption: "i=0 회차: 3번 인덱스가 새로운 최솟값입니다.",
 *       counters: { "비교": 3, "교환": 1 },              // 선택
 *       view: [ <group>, <group>, ... ]
 *   }
 *
 * view group 종류 (모두 선택 필드는 생략 가능):
 *   { type:"array", label:"점수", cells:[{ v:29, sub:"0", cls:"compare" }, ...] }
 *   { type:"stack", label:"스택", items:[{ v:"(", cls:"" }], emptyText:"(비어 있음)" }
 *   { type:"queue", label:"큐",   items:[...], emptyText:"(비어 있음)" }
 *   { type:"table", label:"DP",  head:["i","0","1"], rows:[[{v:"dp[i]"},{v:0,cls:"updated"}]] }
 *   { type:"graph", label:"그래프", w:560, h:300,
 *     nodes:[{ id:"A", x:80, y:60, cls:"visited", sub:"dist 3" }],
 *     edges:[{ from:"A", to:"B", label:"5", cls:"active", directed:true }] }
 *   { type:"frames", label:"호출 스택", items:[{ v:"fibo(3)", cls:"current" }] }
 *   { type:"note", text:"큐가 비면 탐색이 끝납니다." }
 *
 * cls 값: compare | current | done | found | error | min | visited | updated | faded
 * 모든 값은 textContent로만 삽입한다(HTML 미해석).
 */
(function () {
    "use strict";

    var SPEEDS = [
        { label: "느리게", ms: 1600 },
        { label: "보통", ms: 900 },
        { label: "빠르게", ms: 450 }
    ];

    var LEGEND_LABELS = {
        compare: ["lg-compare", "비교 중"],
        current: ["lg-current", "현재 위치/방문"],
        done: ["lg-done", "확정/완료"],
        error: ["lg-error", "오류/실패"]
    };

    function el(tag, className, text) {
        var node = document.createElement(tag);
        if (className) node.className = className;
        if (text !== undefined && text !== null) node.textContent = String(text);
        return node;
    }

    function svgEl(tag) {
        return document.createElementNS("http://www.w3.org/2000/svg", tag);
    }

    /* ---------- view group 렌더러 ---------- */

    function renderCells(container, items, extraClass) {
        (items || []).forEach(function (item) {
            var cell = el("div", "viz-cell" + (item.cls ? " viz-cell--" + item.cls : ""));
            cell.appendChild(document.createTextNode(item.v === undefined ? "" : String(item.v)));
            if (item.sub !== undefined && item.sub !== null) {
                cell.appendChild(el("small", null, item.sub));
            }
            container.appendChild(cell);
        });
        if ((!items || items.length === 0) && extraClass) {
            container.appendChild(el("span", "viz-endcap", extraClass));
        }
    }

    var GROUP_RENDERERS = {
        array: function (group) {
            var wrap = el("div", "viz-array");
            renderCells(wrap, group.cells, group.emptyText || "(비어 있음)");
            return wrap;
        },

        stack: function (group) {
            var wrap = el("div", "viz-stack");
            renderCells(wrap, group.items, group.emptyText || "(비어 있음)");
            if (group.items && group.items.length) {
                wrap.appendChild(el("span", "viz-endcap", "← top (여기서 넣고 꺼냄)"));
            }
            return wrap;
        },

        queue: function (group) {
            var wrap = el("div", "viz-queue");
            wrap.appendChild(el("span", "viz-endcap", "꺼냄(front) →"));
            renderCells(wrap, group.items, group.emptyText || "(비어 있음)");
            if (group.items && group.items.length) {
                wrap.appendChild(el("span", "viz-endcap", "← 넣음(rear)"));
            }
            return wrap;
        },

        table: function (group) {
            var scroll = el("div", "table-scroll");
            var table = el("table", "viz-table");
            if (group.head && group.head.length) {
                var thead = el("thead");
                var headRow = el("tr");
                group.head.forEach(function (h) {
                    headRow.appendChild(el("th", null, h));
                });
                thead.appendChild(headRow);
                table.appendChild(thead);
            }
            var tbody = el("tbody");
            (group.rows || []).forEach(function (row) {
                var tr = el("tr");
                row.forEach(function (cellDef) {
                    var isHead = cellDef && cellDef.head;
                    var td = el(isHead ? "th" : "td",
                        cellDef && cellDef.cls ? "viz-cell--" + cellDef.cls : null,
                        cellDef === null || cellDef === undefined ? "" : cellDef.v);
                    tr.appendChild(td);
                });
                tbody.appendChild(tr);
            });
            table.appendChild(tbody);
            scroll.appendChild(table);
            return scroll;
        },

        graph: function (group) {
            var w = group.w || 560;
            var h = group.h || 300;
            var svg = svgEl("svg");
            svg.setAttribute("class", "viz-svg");
            svg.setAttribute("viewBox", "0 0 " + w + " " + h);
            svg.setAttribute("role", "img");
            if (group.alt) {
                svg.setAttribute("aria-label", group.alt);
            }

            var byId = {};
            (group.nodes || []).forEach(function (node) {
                byId[node.id] = node;
            });

            /* 화살표 마커 (방향 그래프용) */
            var defs = svgEl("defs");
            var marker = svgEl("marker");
            marker.setAttribute("id", "viz-arrow-" + Math.floor(Math.random() * 1e9));
            marker.setAttribute("viewBox", "0 0 10 10");
            marker.setAttribute("refX", "9");
            marker.setAttribute("refY", "5");
            marker.setAttribute("markerWidth", "7");
            marker.setAttribute("markerHeight", "7");
            marker.setAttribute("orient", "auto-start-reverse");
            var arrowPath = svgEl("path");
            arrowPath.setAttribute("d", "M 0 0 L 10 5 L 0 10 z");
            arrowPath.setAttribute("fill", "currentColor");
            marker.appendChild(arrowPath);
            defs.appendChild(marker);
            svg.appendChild(defs);

            var R = group.nodeRadius || 20;

            (group.edges || []).forEach(function (edge) {
                var from = byId[edge.from];
                var to = byId[edge.to];
                if (!from || !to) return;
                var dx = to.x - from.x;
                var dy = to.y - from.y;
                var len = Math.sqrt(dx * dx + dy * dy) || 1;
                var sx = from.x + (dx / len) * R;
                var sy = from.y + (dy / len) * R;
                var tx = to.x - (dx / len) * (R + (edge.directed ? 5 : 0));
                var ty = to.y - (dy / len) * (R + (edge.directed ? 5 : 0));

                var line = svgEl("line");
                line.setAttribute("x1", sx);
                line.setAttribute("y1", sy);
                line.setAttribute("x2", tx);
                line.setAttribute("y2", ty);
                line.setAttribute("class", "edge" + (edge.cls ? " edge--" + edge.cls : ""));
                if (edge.directed) {
                    line.setAttribute("marker-end", "url(#" + marker.getAttribute("id") + ")");
                }
                svg.appendChild(line);

                if (edge.label !== undefined && edge.label !== null) {
                    var text = svgEl("text");
                    text.setAttribute("x", (from.x + to.x) / 2 + (edge.labelDx || 6));
                    text.setAttribute("y", (from.y + to.y) / 2 + (edge.labelDy || -6));
                    text.setAttribute("class", "edge-label");
                    text.textContent = String(edge.label);
                    svg.appendChild(text);
                }
            });

            (group.nodes || []).forEach(function (node) {
                var g = svgEl("g");
                g.setAttribute("class", "node" + (node.cls ? " node--" + node.cls : ""));
                var circle = svgEl("circle");
                circle.setAttribute("cx", node.x);
                circle.setAttribute("cy", node.y);
                circle.setAttribute("r", R);
                g.appendChild(circle);
                var label = svgEl("text");
                label.setAttribute("x", node.x);
                label.setAttribute("y", node.y);
                label.textContent = String(node.label !== undefined ? node.label : node.id);
                g.appendChild(label);
                if (node.sub !== undefined && node.sub !== null) {
                    var sub = svgEl("text");
                    sub.setAttribute("x", node.x);
                    sub.setAttribute("y", node.y + R + 13);
                    sub.setAttribute("class", "node-sub");
                    sub.textContent = String(node.sub);
                    g.appendChild(sub);
                }
                svg.appendChild(g);
            });

            return svg;
        },

        frames: function (group) {
            var wrap = el("div", "viz-frames");
            (group.items || []).forEach(function (item) {
                wrap.appendChild(el("div", "viz-frame" + (item.cls ? " viz-frame--" + item.cls : ""), item.v));
            });
            if (!group.items || !group.items.length) {
                wrap.appendChild(el("span", "viz-endcap", group.emptyText || "(호출 스택이 비어 있음)"));
            }
            return wrap;
        },

        note: function (group) {
            return el("p", "viz-note", group.text);
        }
    };

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
            parts.push((group.head || []).join(""));
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

    /* ---------- 모션 수명 주기 (공통) ----------
     * "취소"와 "완료"를 분리한다: 어느 쪽이든 DOM 리셋(reset)은 반드시 하지만,
     * onDone(= 값 커밋)은 완료(finish) 경로에서만 호출한다. 재구성(rebuild)처럼
     * 진행 중인 애니메이션의 스텝 데이터가 이미 못 쓰게 된 상황에서는 cancel()을
     * 써서 리셋만 하고 커밋은 절대 실행하지 않는다.
     * animateSwap과 (Task 4의) animateCopy가 이 헬퍼를 공유해 pendingMotion을
     * 다루는 코드를 두 번 만들지 않는다.
     */
    function createMotion(resetFn, onDone, durMs) {
        var finished = false;
        var timeoutId = null;

        function clearPendingTimeout() {
            if (timeoutId !== null) {
                clearTimeout(timeoutId);
                timeoutId = null;
            }
        }

        function finish() {
            if (finished) return;
            finished = true;
            clearPendingTimeout();
            resetFn();
            onDone();
        }

        function cancel() {
            if (finished) return;
            finished = true;
            clearPendingTimeout();
            resetFn();
            /* onDone은 호출하지 않는다 — 이 모션의 스텝은 절대 커밋되면 안 된다 */
        }

        timeoutId = setTimeout(finish, durMs + 40);
        return { finish: finish, cancel: cancel };
    }

    function animateSwap(cells, a, b, durMs, onDone) {
        var ra = cells[a].getBoundingClientRect();
        var rb = cells[b].getBoundingClientRect();
        var dx = rb.left - ra.left;

        function reset() {
            [a, b].forEach(function (k) {
                cells[k].style.transition = "";
                cells[k].style.transform = "";
                cells[k].classList.remove("is-moving");
            });
        }

        [a, b].forEach(function (k) {
            cells[k].classList.add("is-moving");
            cells[k].style.transition = "transform " + durMs + "ms var(--ease-in-out)";
        });
        cells[a].style.transform = "translateX(" + dx + "px)";
        cells[b].style.transform = "translateX(" + (-dx) + "px)";

        return createMotion(reset, onDone, durMs);
    }

    /* 무대가 가로 스크롤될 수 있어 position: fixed로 뷰포트 좌표를 쓴다.
     * animateSwap과 동일하게 createMotion에 수명 주기를 통째로 위임한다 —
     * reset()에서 고스트 노드 제거와 is-vacating 해제를 모두 처리해야
     * cancel() 경로(재구성 도중 클릭)에서도 고스트가 반드시 사라진다.
     */
    function animateCopy(cells, from, to, durMs, onDone) {
        var rf = cells[from].getBoundingClientRect();
        var rt = cells[to].getBoundingClientRect();
        var ghost = cells[from].cloneNode(true);

        ghost.className = cells[from].className + " viz-cell--ghost";
        ghost.style.position = "fixed";
        ghost.style.left = rf.left + "px";
        ghost.style.top = rf.top + "px";
        ghost.style.width = rf.width + "px";
        ghost.style.height = rf.height + "px";
        ghost.style.margin = "0";
        document.body.appendChild(ghost);
        cells[to].classList.add("is-vacating");

        function reset() {
            if (ghost.parentNode) ghost.parentNode.removeChild(ghost);
            cells[to].classList.remove("is-vacating");
        }

        /* 다음 프레임에 목표 위치를 지정해야 전환이 발동한다 */
        requestAnimationFrame(function () {
            ghost.style.transition = "transform " + durMs + "ms var(--ease-out)";
            ghost.style.transform = "translateX(" + (rt.left - rf.left) + "px)";
        });

        return createMotion(reset, onDone, durMs);
    }

    /* ---------- 플레이어 ---------- */

    function create(config) {
        var mount = typeof config.mount === "string" ? document.querySelector(config.mount) : config.mount;
        if (!mount) {
            return null;
        }

        var input = config.initialInput;
        var steps = [];
        var index = 0;
        var timer = null;
        var mounted = [];
        var pendingMotion = null;   // 진행 중인 이동 모션 { cancel, finish } (createMotion 참고)
        var prevValues = null;      // 직전 단계의 배열 값 (이동 감지용)
        var speedMs = SPEEDS[1].ms;
        var reducedMotion = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;

        /* 구조 생성 */
        mount.classList.add("viz");
        mount.textContent = "";

        var header = el("div", "viz__header");
        var title = el("p", "viz__title", config.title || "알고리즘 시각화");
        var counter = el("span", "viz__counter", "");
        header.appendChild(title);
        header.appendChild(counter);

        var stage = el("div", "viz__stage");
        var caption = el("div", "viz__caption");
        caption.setAttribute("aria-live", "polite");
        var stats = el("div", "viz__stats");

        var legend = null;
        if (config.legend && config.legend.length) {
            legend = el("div", "viz-legend");
            config.legend.forEach(function (key) {
                var def = LEGEND_LABELS[key];
                if (!def) return;
                var item = el("span");
                item.appendChild(el("i", def[0]));
                item.appendChild(document.createTextNode(def[1]));
                legend.appendChild(item);
            });
        }

        var controls = el("div", "viz__controls");
        var btnFirst = el("button", "button", "⏮ 처음부터");
        var btnPrev = el("button", "button", "◀ 이전 단계");
        var btnNext = el("button", "button button--primary", "다음 단계 ▶");
        var btnPlay = el("button", "button", "▶ 자동 실행");
        [btnFirst, btnPrev, btnNext, btnPlay].forEach(function (b) { b.type = "button"; });

        var speedWrap = el("label", "viz__speed");
        speedWrap.appendChild(document.createTextNode("속도"));
        var speedSelect = document.createElement("select");
        SPEEDS.forEach(function (s, i) {
            var opt = document.createElement("option");
            opt.value = String(s.ms);
            opt.textContent = s.label;
            if (i === 1) opt.selected = true;
            speedSelect.appendChild(opt);
        });
        speedWrap.appendChild(speedSelect);

        controls.appendChild(btnFirst);
        controls.appendChild(btnPrev);
        controls.appendChild(btnNext);
        controls.appendChild(btnPlay);

        var btnNewData = null;
        if (typeof config.makeInput === "function") {
            btnNewData = el("button", "button", "🎲 새 데이터");
            btnNewData.type = "button";
            controls.appendChild(btnNewData);
        }
        controls.appendChild(speedWrap);

        mount.appendChild(header);
        mount.appendChild(stage);
        mount.appendChild(caption);
        mount.appendChild(stats);
        if (legend) mount.appendChild(legend);
        mount.appendChild(controls);

        function stopAuto() {
            if (timer) {
                clearInterval(timer);
                timer = null;
            }
            btnPlay.textContent = "▶ 자동 실행";
        }

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

        function commitStep(step, stepIndex) {
            renderView(step.view);

            caption.textContent = "";
            caption.appendChild(el("strong", null, "단계 " + (stepIndex + 1) + ". "));
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

            counter.textContent = (stepIndex + 1) + " / " + steps.length;
            btnPrev.disabled = stepIndex === 0;
            btnFirst.disabled = stepIndex === 0;
            btnNext.disabled = stepIndex >= steps.length - 1;
            prevValues = arrayValuesOf(step.view);

            if (stepIndex >= steps.length - 1) {
                stopAuto();
            }
        }

        function moveDuration() {
            /* 자동 재생 중에는 모션이 재생 간격을 넘지 않게 조인다 */
            var base = 340;
            if (timer) return Math.min(base, Math.round(speedMs * 0.55));
            return base;
        }

        /* 진행 중인 모션을 "완료"시킨다: 리셋 후 onDone(값 커밋)까지 실행한다.
         * 일반적인 단계 이동(다음/이전/처음부터)에서 쓴다. onDone은 그 모션이
         * 시작될 때 캡처해 둔 stepIndex로 자신의 스텝을 정확한 라벨로 커밋하고,
         * 곧바로 이어지는 새 단계 커밋이 그 위에 겹쳐 그려지므로 화면에는
         * 보이지 않고 자연스럽게 이어진다. stepIndex를 캡처하지 않고 공유
         * index를 읽으면(이미 호출자가 증가시킨 뒤이므로) 옛 스텝의 내용이
         * 새 스텝의 번호표를 달고 잠깐 그려지는 라벨 불일치가 생긴다. */
        function finishPendingMotion() {
            if (pendingMotion) {
                var motion = pendingMotion;
                pendingMotion = null;
                motion.finish();
            }
        }

        /* 진행 중인 모션을 "취소"한다: 리셋만 하고 onDone은 호출하지 않는다.
         * rebuild()처럼 스텝 데이터 자체가 통째로 바뀌는 상황에서 쓴다 —
         * 여기서 onDone을 부르면 새로 지어진 stage/mounted에 옛 스텝의
         * 값이 뒤늦게(setTimeout 이후) 커밋되어 화면이 되돌아가는
         * 잔상 버그가 생긴다. */
        function cancelPendingMotion() {
            if (pendingMotion) {
                var motion = pendingMotion;
                pendingMotion = null;
                motion.cancel();
            }
        }

        function renderStep() {
            var step = steps[index];
            if (!step) return;
            var stepIndex = index;   /* 이 호출이 다루는 스텝의 번호를 미리 고정해 둔다 —
                                      * 이후 index가 다음 클릭으로 먼저 바뀌어도(강제 종료
                                      * 경로) 이 스텝은 항상 자신의 올바른 번호로 커밋된다. */

            /* 진행 중인 모션이 있으면 즉시 끝내고(커밋) 새 단계로 넘어간다 */
            finishPendingMotion();

            var nextValues = arrayValuesOf(step.view);
            var swap = reducedMotion ? null : detectSwap(prevValues, nextValues);
            var copy = (reducedMotion || swap) ? null : detectCopyForward(prevValues, nextValues);

            if (!swap && !copy) {
                commitStep(step, stepIndex);
                return;
            }

            var cells = stage.querySelectorAll(".viz-array .viz-cell");
            if (cells.length !== nextValues.length) {
                commitStep(step, stepIndex);
                return;
            }

            if (swap) {
                pendingMotion = animateSwap(cells, swap.a, swap.b, moveDuration(), function () {
                    pendingMotion = null;
                    commitStep(step, stepIndex);
                });
            } else {
                pendingMotion = animateCopy(cells, copy.from, copy.to, moveDuration(), function () {
                    pendingMotion = null;
                    commitStep(step, stepIndex);
                });
            }
        }

        function rebuild() {
            /* 진행 중인 모션이 있다면 취소만 한다 — 절대 완료(커밋)시키지 않는다.
             * steps/stage가 곧 통째로 교체되므로 옛 스텝을 커밋하면 새 데이터 위에
             * 옛 값이 지연 반영되는 버그가 생긴다. */
            cancelPendingMotion();

            try {
                steps = config.makeSteps(input) || [];
            } catch (e) {
                steps = [{ caption: "시각화 데이터를 만드는 중 오류가 발생했습니다: " + e.message, view: [] }];
            }
            if (!steps.length) {
                steps = [{ caption: "표시할 단계가 없습니다.", view: [] }];
            }
            mounted = [];
            stage.textContent = "";
            prevValues = null;
            index = 0;
            renderStep();
        }

        btnNext.addEventListener("click", function () {
            stopAuto();
            if (index < steps.length - 1) {
                index += 1;
                renderStep();
            }
        });

        btnPrev.addEventListener("click", function () {
            stopAuto();
            if (index > 0) {
                index -= 1;
                renderStep();
            }
        });

        btnFirst.addEventListener("click", function () {
            stopAuto();
            index = 0;
            prevValues = null;   /* 인접 단계가 아니므로 이동 감지를 끈다 */
            renderStep();
        });

        btnPlay.addEventListener("click", function () {
            if (timer) {
                stopAuto();
                return;
            }
            if (index >= steps.length - 1) {
                index = 0;
                renderStep();
            }
            btnPlay.textContent = "⏸ 일시 정지";
            var interval = reducedMotion ? Math.max(speedMs, 1600) : speedMs;
            timer = setInterval(function () {
                if (index < steps.length - 1) {
                    index += 1;
                    renderStep();
                } else {
                    stopAuto();
                }
            }, interval);
        });

        speedSelect.addEventListener("change", function () {
            speedMs = parseInt(speedSelect.value, 10) || SPEEDS[1].ms;
            if (timer) {
                stopAuto();
                btnPlay.click();
            }
        });

        if (btnNewData) {
            btnNewData.addEventListener("click", function () {
                stopAuto();
                input = config.makeInput();
                rebuild();
            });
        }

        rebuild();

        return {
            rebuild: rebuild,
            setInput: function (next) {
                input = next;
                rebuild();
            }
        };
    }

    window.AlgoViz = { create: create };
})();
