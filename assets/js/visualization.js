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

        function renderStep() {
            var step = steps[index];
            if (!step) return;

            stage.textContent = "";
            (step.view || []).forEach(function (group) {
                var renderer = GROUP_RENDERERS[group.type];
                if (!renderer) return;
                var block = el("div");
                if (group.label) {
                    block.appendChild(el("p", "viz__group-label", group.label));
                }
                block.appendChild(renderer(group));
                stage.appendChild(block);
            });

            caption.textContent = "";
            var stepTag = el("strong", null, "단계 " + (index + 1) + ". ");
            caption.appendChild(stepTag);
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

        function rebuild() {
            try {
                steps = config.makeSteps(input) || [];
            } catch (e) {
                steps = [{ caption: "시각화 데이터를 만드는 중 오류가 발생했습니다: " + e.message, view: [] }];
            }
            if (!steps.length) {
                steps = [{ caption: "표시할 단계가 없습니다.", view: [] }];
            }
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
