/*
 * 모든 페이지 공통 동작
 * - 상단 내비게이션 구성([data-site-header] 채움: 홈, 강의 목차 드롭다운, 다크 모드)
 * - 강의 페이지: 내부 목차 자동 생성 + 스크롤스파이, 이전/다음 강의, 완료 버튼, 학습 위치 저장
 * - 맨 위로 버튼
 * 강의 페이지는 <body data-lesson-id="..."> 로 자신을 식별한다.
 */
(function () {
    "use strict";

    function el(tag, className, text) {
        var node = document.createElement(tag);
        if (className) node.className = className;
        if (text !== undefined && text !== null) node.textContent = String(text);
        return node;
    }

    function ready(fn) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", fn);
        } else {
            fn();
        }
    }

    /* =============== 히어로 개념 모티프 ===============
       강의 히어로는 본문이 62ch로 묶여 우측이 늘 비어 있었다. 그 자리에
       강의 주제를 한 컷으로 요약하는 SVG를 넣는다.

       - 모든 장면은 viewBox 320x180을 공유해 13개 강의의 히어로 높이가
         같게 유지된다.
       - 움직임은 전부 lesson.css의 @keyframes가 담당한다. 여기서는 그리기만
         하고 애니메이션 루프를 돌리지 않는다.
       - 기본 클래스가 곧 "정지 프레임"이다. reduced-motion에서 애니메이션이
         꺼져도 의미 있는 마지막 장면이 남는다. */
    function svgScene(inner) {
        return '<svg viewBox="0 0 320 180" preserveAspectRatio="xMidYMid meet" focusable="false">' +
            inner + "</svg>";
    }

    /* 순차 점등 키프레임 생성.
       n개 슬롯 중 k번째는 주기의 k/n 지점까지 비어 있다가 잠깐 강조된 뒤
       주기 끝까지 확정 상태로 남는다. 그래야 "하나씩 쌓인다"로 읽힌다.
       animation-delay로는 이 누적을 만들 수 없어(각자 자기 주기의 앞부분만
       비게 된다) 슬롯마다 다른 키프레임을 쓴다. */
    var slotStyle = null;
    var slotMade = {};

    function ensureSlotKeyframes(n) {
        if (slotMade[n]) return;
        slotMade[n] = true;

        if (!slotStyle) {
            slotStyle = document.createElement("style");
            document.head.appendChild(slotStyle);
        }

        var plain = "fill:var(--surface-2);stroke:var(--line-strong);stroke-width:1.5";
        var active = "fill:var(--state-compare-bg);stroke:var(--state-compare);stroke-width:3";
        var done = "fill:var(--state-done-bg);stroke:var(--state-done);stroke-width:2.5";
        var step = 100 / n;
        var css = "";

        for (var k = 0; k < n; k += 1) {
            var on = k * step;
            var peak = on + step * 0.5;
            css += "@keyframes m-fill-" + k + "-" + n + "{" +
                "0%," + on.toFixed(2) + "%{" + plain + "}" +
                peak.toFixed(2) + "%{" + active + "}" +
                (peak + 0.01).toFixed(2) + "%,100%{" + done + "}}";
        }

        slotStyle.appendChild(document.createTextNode(css));
    }

    /* n개 중 k번째 슬롯을 차지하는 원소에 붙일 속성 */
    function slot(k, n) {
        return ' style="animation-name: m-fill-' + k + "-" + n + '"';
    }

    /* 이동 목적지 — m-move / m-shuttle이 읽는다 */
    function moveTo(dx, dy) {
        return ' style="--dx: ' + dx + "px; --dy: " + (dy || 0) + 'px"';
    }

    var MOTIFS = {
        /* 복잡도: 같은 문제라도 방법에 따라 일의 양이 갈린다 */
        "algorithm-basics": function () {
            return svgScene(
                '<line class="m-axis" x1="34" y1="26" x2="34" y2="150"/>' +
                '<line class="m-axis" x1="34" y1="150" x2="292" y2="150"/>' +
                /* O(n²) — 가파르게 치솟는다 */
                '<path class="m-curve m-curve--bad m-anim-draw" pathLength="100" ' +
                    'd="M34 150 C120 148 176 120 218 34"/>' +
                /* O(n) — 완만하다 */
                '<path class="m-curve m-curve--good m-anim-draw" pathLength="100" d="M34 150 L286 104"/>' +
                '<text class="m-caption" x="238" y="30" fill="var(--state-error)">O(n²)</text>' +
                '<text class="m-caption" x="272" y="94" fill="var(--state-done)">O(n)</text>' +
                '<text class="m-caption" x="163" y="170">입력 크기 n</text>'
            );
        },

        /* 배열: 중간에 넣으면 뒤가 통째로 밀린다 */
        "arrays-and-lists": function () {
            var vals = [7, 3, 9, 4, 8];
            var w = 40, gap = 4, x0 = 24, y = 74, h = 44;
            function cell(i, x, val, extra, style) {
                return '<g' + (style || "") + ' class="' + (extra || "") + '">' +
                    '<rect class="m-cell" x="' + x + '" y="' + y + '" width="' + w + '" height="' + h + '" rx="7"/>' +
                    '<text class="m-node-label" x="' + (x + w / 2) + '" y="' + (y + h / 2) + '">' + val + "</text></g>";
            }
            var out = "";
            /* 앞의 두 칸은 그대로 */
            out += cell(0, x0, vals[0]) + cell(1, x0 + (w + gap), vals[1]);
            /* 뒤의 세 칸이 한 칸씩 밀린다 */
            for (var i = 2; i < vals.length; i += 1) {
                out += cell(i, x0 + i * (w + gap), vals[i], "m-moved m-anim-move", moveTo(w + gap, 0));
            }
            /* 비워진 자리에 들어오는 새 값 */
            out += '<g class="m-anim-fade-in">' +
                '<rect class="m-cell is-done" x="' + (x0 + 2 * (w + gap)) + '" y="' + y +
                    '" width="' + w + '" height="' + h + '" rx="7"/>' +
                '<text class="m-node-label" x="' + (x0 + 2 * (w + gap) + w / 2) + '" y="' + (y + h / 2) + '">5</text></g>';
            out += '<text class="m-caption" x="160" y="146">중간 삽입 = 뒤를 전부 밀기</text>';
            return svgScene(out);
        },

        /* 완전 탐색: 패턴을 한 칸씩 밀며 전부 대본다 */
        "brute-force-string-hash": function () {
            var text = "ABACABAD";
            var w = 34, gap = 4, x0 = 10, y = 74, h = 46;
            var out = "";
            for (var i = 0; i < text.length; i += 1) {
                var x = x0 + i * (w + gap);
                out += '<rect class="m-cell" x="' + x + '" y="' + y + '" width="' + w + '" height="' + h + '" rx="6"/>' +
                    '<text class="m-node-label" x="' + (x + w / 2) + '" y="' + (y + h / 2) + '">' + text.charAt(i) + "</text>";
            }
            /* 3글자 창이 오른쪽으로 미끄러지다 일치 지점에서 확정된다 */
            out += '<rect class="m-window m-anim-slide" x="' + (x0 - 3) + '" y="' + (y - 5) +
                '" width="' + (3 * w + 2 * gap + 6) + '" height="' + (h + 10) + '" rx="9"/>';
            out += '<text class="m-caption" x="160" y="146">창을 한 칸씩 밀며 대조</text>';
            return svgScene(out);
        },

        /* 정렬: 각자 제자리를 찾아간다 */
        "sorting-algorithms": function () {
            var bars = [
                { h: 44, from: 0, to: 1 },
                { h: 96, from: 1, to: 4 },
                { h: 28, from: 2, to: 0 },
                { h: 74, from: 3, to: 3 },
                { h: 58, from: 4, to: 2 }
            ];
            var w = 38, slotW = 54, x0 = 44, base = 142;
            var out = "";
            bars.forEach(function (b) {
                var x = x0 + b.from * slotW;
                out += '<rect class="m-cell is-done m-moved m-anim-move" x="' + x + '" y="' + (base - b.h) +
                    '" width="' + w + '" height="' + b.h + '" rx="6"' +
                    moveTo((b.to - b.from) * slotW, 0) + "/>";
            });
            out += '<line class="m-axis" x1="30" y1="142" x2="290" y2="142"/>' +
                '<text class="m-caption" x="160" y="166">비교하고 자리를 바꾼다</text>';
            return svgScene(out);
        },

        /* 이진 탐색: 후보 범위가 절반씩 접히고 탐침이 남은 가운데로 뛴다 */
        "search-algorithms": function () {
            var values = [5, 12, 19, 26, 33, 41, 48, 55];
            function cell(i, cls) {
                var x = 10 + i * 38;
                return '<rect class="m-cell ' + cls + '" x="' + x + '" y="66" width="34" height="62" rx="8"/>' +
                    '<text class="m-node-label" x="' + (x + 17) + '" y="97">' + values[i] + "</text>";
            }
            return svgScene(
                /* 탐침 — 지금 비교 중인 가운데 칸을 가리킨다 */
                '<polygon class="m-probe m-anim-probe" points="133,40 149,40 141,54"/>' +
                /* 1단계에서 버려지는 왼쪽 절반 */
                '<g class="m-dropped m-anim-drop-1">' + cell(0, "") + cell(1, "") + cell(2, "") + cell(3, "") + "</g>" +
                /* 2단계에서 버려지는 부분 */
                '<g class="m-dropped m-anim-drop-2">' + cell(4, "") + cell(5, "") + "</g>" +
                /* 3단계에서 버려지는 부분 */
                '<g class="m-dropped m-anim-drop-3">' + cell(7, "") + "</g>" +
                /* 찾는 값 */
                cell(6, "is-done m-anim-target")
            );
        },

        /* 트리: 중위 순회 순서대로 노드가 확정된다 */
        "tree-structures": function () {
            var nodes = [
                { x: 160, y: 36, v: 4, order: 3 },
                { x: 95, y: 92, v: 2, order: 1 },
                { x: 225, y: 92, v: 6, order: 5 },
                { x: 58, y: 146, v: 1, order: 0 },
                { x: 132, y: 146, v: 3, order: 2 },
                { x: 188, y: 146, v: 5, order: 4 },
                { x: 262, y: 146, v: 7, order: 6 }
            ];
            var edges = [[0, 1], [0, 2], [1, 3], [1, 4], [2, 5], [2, 6]];
            var out = "";
            ensureSlotKeyframes(nodes.length);
            edges.forEach(function (e) {
                var a = nodes[e[0]], b = nodes[e[1]];
                out += '<line class="m-edge" x1="' + a.x + '" y1="' + a.y + '" x2="' + b.x + '" y2="' + b.y + '"/>';
            });
            nodes.forEach(function (n) {
                out += '<circle class="m-node is-done m-anim-fill" cx="' + n.x + '" cy="' + n.y + '" r="15"' +
                    slot(n.order, nodes.length) + "/>" +
                    '<text class="m-node-label" x="' + n.x + '" y="' + n.y + '">' + n.v + "</text>";
            });
            return svgScene(out);
        },

        /* 스택과 큐: 넣은 쪽으로 나오는가, 반대쪽으로 나오는가 */
        "stack-and-queue": function () {
            var out = "";
            /* 왼쪽 — 스택. 위로 넣고 위로 뺀다(왕복) */
            [118, 90, 62].forEach(function (y) {
                out += '<rect class="m-cell" x="30" y="' + y + '" width="80" height="26" rx="6"/>';
            });
            out += '<rect class="m-cell is-done m-anim-shuttle" x="30" y="34" width="80" height="26" rx="6"' +
                moveTo(0, -46) + "/>" +
                '<text class="m-caption" x="70" y="166">LIFO</text>';

            /* 오른쪽 — 큐. 앞이 빠지고 모두 당겨진 뒤 뒤에 새로 붙는다 */
            var qx = [186, 220, 254, 288];
            out += '<rect class="m-cell m-exited m-anim-exit" x="' + qx[0] + '" y="76" width="30" height="28" rx="6"' +
                ' style="--dx: -40px"/>';
            [1, 2, 3].forEach(function (i) {
                out += '<rect class="m-cell m-moved m-anim-move" x="' + qx[i] + '" y="76" width="30" height="28" rx="6"' +
                    moveTo(-34, 0) + "/>";
            });
            out += '<rect class="m-cell is-done m-anim-fade-in" x="' + qx[3] + '" y="76" width="30" height="28" rx="6"/>' +
                '<text class="m-caption" x="237" y="166">FIFO</text>';
            return svgScene(out);
        },

        /* 재귀: 깊이 들어갔다가 되돌아 나오며 취소한다 */
        "recursion-and-backtracking": function () {
            var out = "";
            var n = 5;
            ensureSlotKeyframes(n);
            for (var i = 0; i < n; i += 1) {
                out += '<rect class="m-cell is-done m-anim-fill" x="' + (34 + i * 32) + '" y="' + (24 + i * 27) +
                    '" width="86" height="22" rx="6"' + slot(i, n) + "/>";
            }
            /* 되돌아 나오는 경로 — 마지막에 그려진다 */
            out += '<path class="m-curve m-curve--bad m-anim-draw" pathLength="100" fill="none" ' +
                'd="M258 145 C296 132 296 56 268 41"/>' +
                '<polygon class="m-arrow-head" points="262,34 274,38 262,46" ' +
                'style="fill: var(--state-error)"/>' +
                '<text class="m-caption" x="150" y="172">되돌아가며 취소</text>';
            return svgScene(out);
        },

        /* 그래프 탐색: 시작점에서 가까운 것부터 물결처럼 */
        "graph-search": function () {
            var nodes = [
                { x: 38, y: 90, v: "A", order: 0 },
                { x: 104, y: 44, v: "B", order: 1 },
                { x: 104, y: 134, v: "C", order: 2 },
                { x: 176, y: 32, v: "D", order: 3 },
                { x: 176, y: 90, v: "E", order: 4 },
                { x: 176, y: 148, v: "F", order: 5 },
                { x: 250, y: 90, v: "G", order: 6 }
            ];
            var edges = [[0, 1], [0, 2], [1, 3], [1, 4], [2, 4], [2, 5], [3, 6], [4, 6], [5, 6]];
            var out = "";
            ensureSlotKeyframes(nodes.length);
            edges.forEach(function (e) {
                var a = nodes[e[0]], b = nodes[e[1]];
                out += '<line class="m-edge" x1="' + a.x + '" y1="' + a.y + '" x2="' + b.x + '" y2="' + b.y + '"/>';
            });
            nodes.forEach(function (nd) {
                out += '<circle class="m-node is-done m-anim-fill" cx="' + nd.x + '" cy="' + nd.y + '" r="14"' +
                    slot(nd.order, nodes.length) + "/>" +
                    '<text class="m-node-label" x="' + nd.x + '" y="' + nd.y + '">' + nd.v + "</text>";
            });
            /* 캡션은 두지 않는다 — 그래프가 상자를 꽉 채워 F 노드와 글자가 붙는다 */
            return svgScene(out);
        },

        /* 그리디: 매 순간 가장 큰 것을 집는다 (왼쪽부터가 아니다) */
        "greedy-algorithms": function () {
            var bars = [
                { h: 34, order: 4 },
                { h: 78, order: 1 },
                { h: 50, order: 3 },
                { h: 100, order: 0 },
                { h: 64, order: 2 }
            ];
            var out = "";
            ensureSlotKeyframes(bars.length);
            bars.forEach(function (b, i) {
                out += '<rect class="m-cell is-done m-anim-fill" x="' + (44 + i * 54) + '" y="' + (142 - b.h) +
                    '" width="38" height="' + b.h + '" rx="6"' + slot(b.order, bars.length) + "/>";
            });
            out += '<line class="m-axis" x1="30" y1="142" x2="290" y2="142"/>' +
                '<text class="m-caption" x="160" y="166">가장 큰 것부터</text>';
            return svgScene(out);
        },

        /* 최단 경로: 간선 수가 아니라 가중치의 합이 작은 쪽이 남는다 */
        "shortest-path": function () {
            var out = "";
            ensureSlotKeyframes(3);
            /* 위쪽 — 간선은 적지만 합이 크다(9). 흐려진다. */
            out += '<g class="m-dropped m-anim-drop-2">' +
                '<line class="m-edge" x1="36" y1="92" x2="158" y2="36"/>' +
                '<line class="m-edge" x1="158" y1="36" x2="282" y2="92"/>' +
                '<circle class="m-node" cx="158" cy="36" r="13"/>' +
                '<text class="m-caption" x="88" y="52">4</text>' +
                '<text class="m-caption" x="230" y="52">5</text></g>';
            /* 아래쪽 — 간선은 많지만 합이 작다(7). 순서대로 확정된다. */
            var lower = [[36, 92, 118, 148], [118, 148, 200, 148], [200, 148, 282, 92]];
            lower.forEach(function (e, i) {
                out += '<line class="m-edge is-done m-anim-fill" x1="' + e[0] + '" y1="' + e[1] +
                    '" x2="' + e[2] + '" y2="' + e[3] + '"' + slot(i, 3) + "/>";
            });
            out += '<circle class="m-node" cx="118" cy="148" r="12"/>' +
                '<circle class="m-node" cx="200" cy="148" r="12"/>' +
                '<text class="m-caption" x="70" y="134">2</text>' +
                '<text class="m-caption" x="159" y="138">3</text>' +
                '<text class="m-caption" x="248" y="134">2</text>' +
                '<circle class="m-node is-done" cx="36" cy="92" r="15"/>' +
                '<text class="m-node-label" x="36" y="92">S</text>' +
                '<circle class="m-node is-done" cx="282" cy="92" r="15"/>' +
                '<text class="m-node-label" x="282" y="92">T</text>' +
                '<text class="m-caption" x="159" y="18">합이 작은 쪽</text>';
            return svgScene(out);
        },

        /* 동적 계획법: 작은 문제부터 표를 채우고, 각 칸은 왼쪽과 위를 참조한다 */
        "dynamic-programming": function () {
            /* 화살표가 들어갈 자리를 남기려고 칸 사이를 넉넉히(가로 14 · 세로 12) 띄운다 */
            var cols = 5, rows = 3, total = cols * rows;
            var cw = 44, ch = 32, gx = 14, gy = 12;
            var x0 = (320 - (cols * cw + (cols - 1) * gx)) / 2;
            var y0 = (180 - (rows * ch + (rows - 1) * gy)) / 2;
            var out = "";
            ensureSlotKeyframes(total);
            for (var r = 0; r < rows; r += 1) {
                for (var c = 0; c < cols; c += 1) {
                    out += '<rect class="m-cell is-done m-anim-fill" x="' + (x0 + c * (cw + gx)) +
                        '" y="' + (y0 + r * (ch + gy)) + '" width="' + cw + '" height="' + ch + '" rx="6"' +
                        slot(r * cols + c, total) + "/>";
                }
            }
            /* 마지막 칸으로 들어오는 두 화살표 — 값이 왼쪽과 위에서 온다는 사실.
               칸 사이 여백 안에만 그린다(칸을 침범하면 표가 지저분해진다). */
            var lastX = x0 + (cols - 1) * (cw + gx);
            var lastY = y0 + (rows - 1) * (ch + gy);
            var midY = lastY + ch / 2;
            var midX = lastX + cw / 2;
            out += '<path class="m-arrow" d="M' + (lastX - gx + 2) + " " + midY + "H" + (lastX - 5) + '"/>' +
                '<polygon class="m-arrow-head" points="' + (lastX - 6) + "," + (midY - 4) + " " +
                    lastX + "," + midY + " " + (lastX - 6) + "," + (midY + 4) + '"/>' +
                '<path class="m-arrow" d="M' + midX + " " + (lastY - gy + 2) + "V" + (lastY - 5) + '"/>' +
                '<polygon class="m-arrow-head" points="' + (midX - 4) + "," + (lastY - 6) + " " +
                    (midX + 4) + "," + (lastY - 6) + " " + midX + "," + lastY + '"/>';
            return svgScene(out);
        },

        /* 종합: 흩어져 배운 것들이 한 줄로 모인다.
           조각은 서로 다른 모양이어야 "여러 가지를 합친다"로 읽힌다 —
           배열 칸 · 그래프 노드 · 정렬 막대 · DP 표. */
        "algorithm-project": function () {
            var pieces = [
                { start: [52, 36], end: [96, 92], shape: "cell" },
                { start: [262, 40], end: [143, 92], shape: "node" },
                { start: [50, 146], end: [190, 92], shape: "bars" },
                { start: [266, 144], end: [237, 92], shape: "grid" }
            ];

            function draw(shape, cx, cy) {
                if (shape === "node") {
                    return '<circle class="m-node is-done" cx="' + cx + '" cy="' + cy + '" r="18"/>';
                }
                if (shape === "bars") {
                    return '<rect class="m-cell is-done" x="' + (cx - 17) + '" y="' + (cy - 2) + '" width="14" height="20" rx="4"/>' +
                        '<rect class="m-cell is-done" x="' + (cx + 2) + '" y="' + (cy - 18) + '" width="14" height="36" rx="4"/>';
                }
                if (shape === "grid") {
                    var g = "";
                    for (var r = 0; r < 2; r += 1) {
                        for (var c = 0; c < 2; c += 1) {
                            g += '<rect class="m-cell is-done" x="' + (cx - 17 + c * 18) + '" y="' + (cy - 17 + r * 18) +
                                '" width="16" height="16" rx="4"/>';
                        }
                    }
                    return g;
                }
                return '<rect class="m-cell is-done" x="' + (cx - 17) + '" y="' + (cy - 17) +
                    '" width="34" height="34" rx="8"/>';
            }

            var out = "";
            pieces.forEach(function (p) {
                out += '<g class="m-moved m-anim-move"' +
                    moveTo(p.end[0] - p.start[0], p.end[1] - p.start[1]) + ">" +
                    draw(p.shape, p.start[0], p.start[1]) + "</g>";
            });
            out += '<text class="m-caption" x="160" y="150">배운 것을 하나로</text>';
            return svgScene(out);
        }
    };

    ready(function () {
        var lessons = window.ALGORITHMS || [];
        var lessonId = document.body.dataset.lessonId || null;
        var isLessonPage = Boolean(lessonId);
        var rootPrefix = isLessonPage ? "../" : "";

        /* ---------- 상단 내비게이션 ---------- */
        var headerHost = document.querySelector("[data-site-header]");
        if (headerHost) {
            var inner = el("div", "site-header__inner");

            var brand = el("a", "site-brand");
            brand.href = rootPrefix + "index.html";
            var mark = el("span", "site-brand__mark");
            mark.setAttribute("aria-hidden", "true");
            mark.appendChild(el("i"));
            mark.appendChild(el("i"));
            mark.appendChild(el("i"));
            brand.appendChild(mark);
            /* 맨 텍스트 노드로 두면 320px에서 헤더가 41px 넘치는데도
               말줄임을 걸 수 없다(익명 플렉스 아이템). span으로 감싼다. */
            brand.appendChild(el("span", "site-brand__name", "고급알고리즘"));
            /* 소속은 과목명보다 뒤에 온다. 좁은 화면에서는 통째로 사라지고
               과목명만 남는다(둘을 함께 줄이면 둘 다 못 읽는다). */
            brand.appendChild(el("span", "site-brand__dept", "인하공전 컴퓨터시스템공학과"));
            inner.appendChild(brand);

            var menuBtn = el("button", "icon-button mobile-menu-button", "☰ 메뉴");
            menuBtn.type = "button";
            menuBtn.setAttribute("aria-expanded", "false");
            menuBtn.setAttribute("aria-controls", "site-nav");
            inner.appendChild(menuBtn);

            var nav = el("nav", "site-nav");
            nav.id = "site-nav";
            nav.setAttribute("aria-label", "사이트 메뉴");

            var homeLink = el("a", "site-nav__link", "홈");
            homeLink.href = rootPrefix + "index.html";
            if (!isLessonPage) homeLink.setAttribute("aria-current", "page");
            nav.appendChild(homeLink);

            /* 강의 목차 드롭다운 */
            var menuWrap = el("div", "lesson-menu");
            var menuToggle = el("button", "site-nav__link", "강의 목차 ▾");
            menuToggle.type = "button";
            menuToggle.setAttribute("aria-expanded", "false");
            menuToggle.setAttribute("aria-haspopup", "true");
            var menuList = el("ul", "lesson-menu__list");

            var progressState = window.AllProgress ? window.AllProgress.getState() : { lessons: {} };
            lessons.forEach(function (lesson) {
                var li = el("li", "lesson-menu__item");
                var a = el("a");
                a.href = rootPrefix + lesson.path;
                a.appendChild(el("span", "lesson-menu__num", String(lesson.order).padStart(2, "0")));
                a.appendChild(document.createTextNode(lesson.title));
                var entry = progressState.lessons[lesson.id];
                if (entry && entry.completed) {
                    a.appendChild(el("span", "lesson-menu__done", "완료 ✓"));
                }
                if (lesson.id === lessonId) {
                    a.setAttribute("aria-current", "page");
                }
                li.appendChild(a);
                menuList.appendChild(li);
            });

            menuToggle.addEventListener("click", function () {
                var open = menuWrap.classList.toggle("is-open");
                menuToggle.setAttribute("aria-expanded", String(open));
                menuToggle.textContent = open ? "강의 목차 ▴" : "강의 목차 ▾";
            });
            document.addEventListener("click", function (event) {
                if (!menuWrap.contains(event.target) && menuWrap.classList.contains("is-open")) {
                    menuWrap.classList.remove("is-open");
                    menuToggle.setAttribute("aria-expanded", "false");
                    menuToggle.textContent = "강의 목차 ▾";
                }
            });

            menuWrap.appendChild(menuToggle);
            menuWrap.appendChild(menuList);
            nav.appendChild(menuWrap);

            /* 다크 모드 */
            var themeBtn = el("button", "icon-button theme-toggle");
            themeBtn.type = "button";
            function themeLabel() {
                var isDark = document.documentElement.getAttribute("data-theme") === "dark";
                themeBtn.textContent = isDark ? "☀ 라이트 모드" : "🌙 다크 모드";
                themeBtn.setAttribute("aria-label", isDark ? "라이트 모드로 전환" : "다크 모드로 전환");
            }
            themeLabel();
            themeBtn.addEventListener("click", function () {
                if (window.AllTheme) window.AllTheme.toggle();
                themeLabel();
            });
            nav.appendChild(themeBtn);

            menuBtn.addEventListener("click", function () {
                var open = nav.classList.toggle("is-open");
                menuBtn.setAttribute("aria-expanded", String(open));
                menuBtn.textContent = open ? "✕ 닫기" : "☰ 메뉴";
            });

            inner.appendChild(nav);
            headerHost.appendChild(inner);
        }

        /* ---------- 맨 위로 버튼 ---------- */
        var topBtn = el("button", "back-to-top", "↑");
        topBtn.type = "button";
        topBtn.setAttribute("aria-label", "맨 위로 이동");
        topBtn.addEventListener("click", function () {
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
        document.body.appendChild(topBtn);
        window.addEventListener("scroll", function () {
            topBtn.classList.toggle("is-visible", window.scrollY > 600);
        }, { passive: true });

        /* ---------- 스크롤 진입 ----------
           정적 대상(.lesson-section, .how-card, .stat-tile)은 파싱 시점에
           이미 DOM에 있으므로 여기서 동기적으로 표시하고 관찰한다 — 지연 없이
           바로 처리해야 "보였다가 사라지는" 깜빡임이 생기지 않는다.

           랜딩 페이지의 강좌 카드(.course-card)는 사정이 다르다: landing.js가
           검색/필터가 바뀔 때마다 #course-grid를 통째로 다시 그린다. 그 카드를
           이 핸들러가 직접 찾으러 가면(동기든, setTimeout/Promise로 미루든)
           "이미 그려진 뒤에야 알아채는" 시점이 되어, 카드가 먼저 완전히
           보이는 상태로 페인트된 다음에야 opacity:0으로 숨는 깜빡임 구간이
           생길 수 있다. 이를 구조적으로 없애기 위해 window.AllReveal 훅을
           공개한다: landing.js가 카드를 만들 때 "reveal-on-scroll" 클래스를
           DOM 삽입 전에 직접 붙이고(그래서 카드는 첫 페인트부터 이미
           opacity:0 상태), 카드를 다 그려 넣은 직후 이 훅을 호출해 방금
           만든 카드만 골라 관찰을 (재)등록한다. 재렌더마다 이전 카드는 이미
           DOM에서 제거된 상태이므로 관찰을 해제해 분리된 노드를 붙들고 있지
           않는다. */
        (function () {
            var reduced = window.matchMedia &&
                window.matchMedia("(prefers-reduced-motion: reduce)").matches;
            var revealSupported = !reduced && ("IntersectionObserver" in window);

            /* reduced-motion이거나 IntersectionObserver가 없으면 그냥 보여준다 —
               reveal-on-scroll 클래스를 아무 데도 붙이지 않는다. */
            window.AllReveal = {
                enabled: revealSupported,
                observeCourseCards: function () {}   /* 기본은 아무 것도 하지 않는다 */
            };
            if (!revealSupported) return;

            /* threshold: 0 — 뷰포트보다 훨씬 큰 섹션도 한 픽셀만 겹치면
               반응해야 한다. threshold를 0보다 크게 두면 대상 높이가 커질수록
               intersectionRatio의 상한(viewportHeight / targetHeight)이 낮아져,
               아주 긴 섹션은 그 임계값에 영영 도달하지 못하고 opacity: 0으로
               남을 수 있다. */
            var revealObserver = new IntersectionObserver(function (entries) {
                entries.forEach(function (entry) {
                    if (!entry.isIntersecting) return;
                    entry.target.classList.add("is-revealed");
                    /* 랜딩 페이지 카드가 실제로 공개된 시점을 landing.js에 알린다 —
                       검색/필터로 카드가 다시 만들어질 때 "이미 본 카드"는
                       재생하지 않고 곧바로 보이게 하기 위함 (landing.js가 구독). */
                    entry.target.dispatchEvent(new CustomEvent("all:revealed"));
                    revealObserver.unobserve(entry.target);   /* 1회만 — 되돌아가도 재생 안 함 */
                });
            }, { rootMargin: "0px 0px -8% 0px", threshold: 0 });

            var staticTargets = document.querySelectorAll(
                ".lesson-section, .how-card, .stat-tile");
            Array.prototype.forEach.call(staticTargets, function (node) {
                node.classList.add("reveal-on-scroll");
                revealObserver.observe(node);
            });

            var observedCourseCards = [];
            window.AllReveal.observeCourseCards = function (cards) {
                Array.prototype.forEach.call(observedCourseCards, function (card) {
                    revealObserver.unobserve(card);
                });
                observedCourseCards = cards || [];
                Array.prototype.forEach.call(observedCourseCards, function (card) {
                    revealObserver.observe(card);
                });
            };
        })();

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

            function scheduleUpdate() {
                if (ticking) return;
                ticking = true;
                window.requestAnimationFrame(update);
            }

            window.addEventListener("scroll", scheduleUpdate);
            window.addEventListener("resize", scheduleUpdate);

            /* 스크롤/리사이즈만으로는 부족하다 — <details class="answer-box">를
               열고 닫으면 scrollY는 그대로인데 문서 높이가 바뀌어 막대가
               낡은 값에 멈춰 있게 된다("stale bar" 버그). 두 가지로 이를 잡는다.

               1) toggle 이벤트: <details>가 열리고 닫힐 때 발생하지만 버블링되지
                  않으므로 캡처 단계에서 document에 한 번만 걸어 두면 지금 있는
                  것은 물론 나중에 추가되는 모든 <details>까지 한 줄로 커버한다.
               2) ResizeObserver(지원 시): 문서 높이가 바뀌는 다른 원인
                  (이미지 로드, 폰트 스왑, 스크롤 진입 트랜지션 등)까지 넓게
                  잡아낸다. 미지원 브라우저에서는 답 상자 개폐(toggle)까지는
                  그대로 동작하고, 이미지 로드·폰트 스왑으로 생긴 높이 변화만
                  다음 스크롤 때까지 반영이 늦는다. */
            document.addEventListener("toggle", scheduleUpdate, true);

            if ("ResizeObserver" in window) {
                new ResizeObserver(scheduleUpdate).observe(document.documentElement);
            }

            update();
        })();

        if (!isLessonPage) {
            return;
        }

        /* ================= 이하 강의 페이지 전용 ================= */

        var current = lessons.find(function (lesson) { return lesson.id === lessonId; }) || null;

        if (window.AllProgress) {
            window.AllProgress.markStarted(lessonId);
        }

        /* ---------- 히어로 분류 칩에 학습 영역 색 배선 ----------
           랜딩의 카드·칩과 같은 색으로 읽혀야 한다. 13개 HTML을 고치는 대신
           헤더·모티프와 같은 방식으로 여기서 붙인다. 분류 이름은 이미 칩에
           글자로 적혀 있으므로 색은 그 이름을 되짚어 주는 층일 뿐이다. */
        (function () {
            var chip = document.querySelector(".lesson-hero .badge--category");
            var key = current && window.CATEGORY_KEYS ? window.CATEGORY_KEYS[current.category] : "";
            if (chip && key) chip.setAttribute("data-cat", key);
        })();

        /* ---------- 히어로 개념 모티프 ---------- */
        (function () {
            var hero = document.querySelector(".lesson-hero");
            var build = MOTIFS[lessonId];
            if (!hero || !build) return;

            /* 기존 히어로 내용을 한 덩어리로 묶는다. 묶지 않고 그리드를 걸면
               자식 하나하나가 개별 행이 되어 row-gap이 사이사이 벌어지고,
               모티프는 grid-row: 1/-1로도 암시적 행을 가로지르지 못해
               첫 행만 차지한 채 히어로 높이만 크게 늘어난다. */
            var text = el("div", "lesson-hero__text");
            while (hero.firstChild) {
                text.appendChild(hero.firstChild);
            }
            hero.appendChild(text);

            var box = el("div", "lesson-hero__motif");
            box.setAttribute("aria-hidden", "true");   /* 장식 — 본문 정보와 중복되지 않는다 */
            /* innerHTML의 내용은 위 MOTIFS의 하드코딩된 리터럴뿐이다.
               lessonId는 키 조회에만 쓰이고 문자열에 삽입되지 않는다. */
            box.innerHTML = build();
            hero.appendChild(box);
            hero.classList.add("lesson-hero--with-motif");

            /* 히어로가 화면에 있을 때만 재생한다. 본문을 읽는 동안에는
               상단에서 아무것도 움직이지 않는다. */
            if ("IntersectionObserver" in window) {
                new IntersectionObserver(function (entries) {
                    entries.forEach(function (entry) {
                        hero.classList.toggle("is-playing", entry.isIntersecting);
                    });
                }, { threshold: 0 }).observe(hero);
            } else {
                hero.classList.add("is-playing");
            }
        })();

        /* ---------- 내부 목차 자동 생성 + 스크롤스파이 ---------- */
        var tocList = document.getElementById("lesson-toc-list");
        var sections = Array.prototype.slice.call(document.querySelectorAll(".lesson-section[id]"));
        if (tocList && sections.length) {
            var linkById = {};
            sections.forEach(function (section, i) {
                var heading = section.querySelector("h2");
                if (!heading) return;
                var li = el("li");
                var a = el("a");
                a.href = "#" + section.id;
                a.appendChild(el("span", "lesson-toc__num", String(i + 1).padStart(2, "0")));
                var labelText = heading.dataset.tocLabel ||
                    heading.textContent.replace(/^\s*\d+\s*/, "").trim();
                a.appendChild(document.createTextNode(labelText));
                li.appendChild(a);
                tocList.appendChild(li);
                linkById[section.id] = a;
            });

            if ("IntersectionObserver" in window) {
                var activeId = null;
                var observer = new IntersectionObserver(function (entries) {
                    entries.forEach(function (entry) {
                        if (entry.isIntersecting) {
                            activeId = entry.target.id;
                        }
                    });
                    if (activeId && linkById[activeId]) {
                        Object.keys(linkById).forEach(function (id) {
                            linkById[id].classList.toggle("is-active", id === activeId);
                        });
                        if (window.AllProgress) {
                            window.AllProgress.setLastSection(lessonId, activeId);
                        }
                    }
                }, { rootMargin: "-20% 0px -70% 0px" });
                sections.forEach(function (section) { observer.observe(section); });
            }
        }

        /* ---------- 이전/다음 강의 ---------- */
        var pager = document.getElementById("lesson-pager");
        if (pager && current) {
            pager.classList.add("lesson-pager");
            var prev = lessons.find(function (lesson) { return lesson.order === current.order - 1; });
            var next = lessons.find(function (lesson) { return lesson.order === current.order + 1; });

            if (prev) {
                var prevLink = el("a", "is-prev");
                prevLink.href = rootPrefix + prev.path;
                prevLink.appendChild(el("span", "lesson-pager__dir", "← 이전 강의"));
                prevLink.appendChild(el("span", "lesson-pager__title",
                    prev.order + "강. " + prev.title));
                pager.appendChild(prevLink);
            } else {
                pager.appendChild(el("div", "lesson-pager__empty", "첫 번째 강의입니다."));
            }

            if (next) {
                var nextLink = el("a", "is-next");
                nextLink.href = rootPrefix + next.path;
                nextLink.appendChild(el("span", "lesson-pager__dir", "다음 강의 →"));
                nextLink.appendChild(el("span", "lesson-pager__title",
                    next.order + "강. " + next.title));
                pager.appendChild(nextLink);
            } else {
                pager.appendChild(el("div", "lesson-pager__empty",
                    "마지막 강의입니다. 13강까지 완주를 축하합니다! 🎉"));
            }
        }

        /* ---------- 완료 버튼 ---------- */
        var completeSlot = document.getElementById("lesson-complete-slot");
        if (completeSlot && window.AllProgress) {
            completeSlot.classList.add("lesson-complete-slot");

            function renderCompleteSlot() {
                completeSlot.textContent = "";
                var entry = window.AllProgress.get(lessonId);
                if (entry && entry.completed) {
                    completeSlot.appendChild(el("span", "is-done-msg", "✅ 이 강의를 완료했습니다."));
                    var undoBtn = el("button", "button button--ghost", "완료 취소");
                    undoBtn.type = "button";
                    undoBtn.addEventListener("click", function () {
                        window.AllProgress.unmarkCompleted(lessonId);
                        renderCompleteSlot();
                    });
                    completeSlot.appendChild(undoBtn);
                } else {
                    var doneBtn = el("button", "button button--primary", "이 강의를 완료로 표시");
                    doneBtn.type = "button";
                    doneBtn.addEventListener("click", function () {
                        window.AllProgress.markCompleted(lessonId);
                        renderCompleteSlot();
                    });
                    completeSlot.appendChild(doneBtn);
                }
            }

            renderCompleteSlot();
            document.addEventListener("all:progresschange", renderCompleteSlot);
        }
    });
})();
