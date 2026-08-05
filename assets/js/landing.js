/*
 * 랜딩 페이지
 * - window.ALGORITHMS 기반 강의 카드 렌더링
 * - 제목 검색 / 학습 영역 칩 / 난이도 필터
 * - 전체 통계
 * - 히어로 라이브 선택 정렬 무대 (three.js 3D, 실패 시 2D 막대)
 * - 강의 카드 3D 기울임
 */
(function () {
    "use strict";

    function el(tag, className, text) {
        var node = document.createElement(tag);
        if (className) node.className = className;
        if (text !== undefined && text !== null) node.textContent = String(text);
        return node;
    }

    /* ---------- 학습 영역 아이콘 ----------
       한 영역이 실제로 하는 일 한 컷씩. 24x24 격자를 공유하고 선 굵기도 같아
       카드가 나란히 놓였을 때 굵기가 튀지 않는다. 색은 넣지 않는다 —
       currentColor로 그려 --cat-ink가 그대로 흘러들어 온다. */
    function svgIcon(inner) {
        return '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" ' +
            'stroke-linecap="round" stroke-linejoin="round" focusable="false">' + inner + "</svg>";
    }

    var CATEGORY_ICONS = {
        /* 기초 — 같은 문제라도 방법에 따라 갈라지는 두 곡선 */
        basics: svgIcon('<path d="M4 4v16h16"/>' +
            '<path d="M5.5 19C10 18.4 13.6 15 15.8 5.5"/>' +
            '<path d="M5.5 19 20 14.2"/>'),
        /* 자료구조 — 칸이 나뉜 한 덩어리(배열·리스트).
           칸마다 rect를 따로 두면 20px로 줄었을 때 1.8 굵기의 테두리가 속을
           거의 다 먹어 검은 덩어리로 뭉친다. 테두리 하나에 칸막이만 긋는다. */
        structure: svgIcon('<rect x="2" y="7" width="20" height="10" rx="2.6"/>' +
            '<path d="M8.7 7v10M15.3 7v10"/>'),
        /* 정렬 — 키 순으로 선 막대 */
        sorting: svgIcon('<path d="M4 20v-4.5M9.3 20v-8M14.6 20v-11.5M19.9 20V4.5"/>'),
        /* 탐색 — 범위를 좁혀 하나를 찾아낸다 */
        search: svgIcon('<circle cx="10.5" cy="10.5" r="6.5"/>' +
            '<path d="m15.4 15.4 4.6 4.6"/>' +
            '<circle cx="10.5" cy="10.5" r="1.7" fill="currentColor" stroke="none"/>'),
        /* 그래프 — 정점과 간선 */
        graph: svgIcon('<circle cx="5.8" cy="6.6" r="2.8"/>' +
            '<circle cx="18.2" cy="6.6" r="2.8"/>' +
            '<circle cx="12" cy="18" r="2.8"/>' +
            '<path d="M8.6 6.6h6.8"/><path d="m7.7 8.8 2.6 6.6"/><path d="m16.3 8.8-2.6 6.6"/>'),
        /* 설계 기법 — 갈림길에서 하나를 고른다 */
        design: svgIcon('<circle cx="4.6" cy="12" r="2"/>' +
            '<circle cx="19.4" cy="5.8" r="2"/>' +
            '<circle cx="19.4" cy="18.2" r="2"/>' +
            '<path d="M6.6 12h2.6l4.2-6.2h4"/><path d="m9.2 12 4.2 6.2h4"/>'),
        /* 프로젝트 — 다 모아 하나를 세운다 */
        project: svgIcon('<path d="M6 21V3.5"/><path d="M6 4.5h11.5l-2.6 4.2 2.6 4.2H6"/>'),
        /* 표에 없는 분류를 만났을 때의 중립 글리프 */
        fallback: svgIcon('<circle cx="12" cy="12" r="7.5"/><path d="M12 8.5v7M8.5 12h7"/>')
    };

    var CATEGORY_KEYS = window.CATEGORY_KEYS || {};

    function categoryKey(name) {
        return CATEGORY_KEYS[name] || "";
    }

    document.addEventListener("DOMContentLoaded", function () {
        var lessons = window.ALGORITHMS || [];

        /* ---------- 통계 ---------- */
        var totalExamples = lessons.reduce(function (sum, lesson) {
            return sum + lesson.examples;
        }, 0);
        var beginnerCount = lessons.filter(function (lesson) { return lesson.difficulty === "초급"; }).length;
        var intermediateCount = lessons.length - beginnerCount;

        var categories = [];
        lessons.forEach(function (lesson) {
            if (categories.indexOf(lesson.category) === -1) categories.push(lesson.category);
        });

        function setText(id, text) {
            var node = document.getElementById(id);
            if (node) node.textContent = text;
        }

        setText("stat-total", lessons.length + "개");
        setText("stat-categories", categories.length + "개");
        setText("stat-examples", totalExamples + "개");
        setText("stat-beginner", beginnerCount);
        setText("stat-intermediate", intermediateCount);

        /* ---------- 학습 영역 칩 ----------
           색 범례와 필터를 겸한다. 칩 하나가 곧 그 영역의 색 견본이므로
           아래 카드의 띠·아이콘 색이 무엇을 뜻하는지 따로 설명할 필요가 없다. */
        var chipHost = document.getElementById("filter-category");
        var activeCategory = "";        /* "" = 전체 */

        function buildChips() {
            if (!chipHost) return;
            chipHost.textContent = "";

            var items = [{ value: "", label: "전체", count: lessons.length, key: "" }];
            categories.forEach(function (name) {
                var count = lessons.filter(function (lesson) {
                    return lesson.category === name;
                }).length;
                items.push({ value: name, label: name, count: count, key: categoryKey(name) });
            });

            items.forEach(function (item) {
                var chip = el("button", "cat-chip");
                chip.type = "button";
                chip.setAttribute("data-value", item.value);
                if (item.key) chip.setAttribute("data-cat", item.key);
                chip.setAttribute("aria-pressed", item.value === activeCategory ? "true" : "false");

                var dot = el("span", "cat-chip__dot");
                dot.setAttribute("aria-hidden", "true");
                chip.appendChild(dot);
                chip.appendChild(document.createTextNode(item.label));
                chip.appendChild(el("span", "cat-chip__n", item.count));

                chip.addEventListener("click", function () {
                    /* 눌린 칩을 다시 누르면 전체로 돌아간다 — 필터를 푸는 데
                       "전체" 칩까지 찾아가지 않아도 된다. */
                    activeCategory = activeCategory === item.value ? "" : item.value;
                    syncChips();
                    render();
                });

                chipHost.appendChild(chip);
            });
        }

        function syncChips() {
            if (!chipHost) return;
            Array.prototype.forEach.call(chipHost.children, function (chip) {
                chip.setAttribute("aria-pressed",
                    chip.getAttribute("data-value") === activeCategory ? "true" : "false");
            });
        }

        /* ---------- 카드 렌더링 ---------- */
        var grid = document.getElementById("course-grid");
        var emptyMsg = document.getElementById("courses-empty");
        var countLabel = document.getElementById("courses-count");
        var searchInput = document.getElementById("course-search");
        var difficultySelect = document.getElementById("filter-difficulty");

        /* render()는 검색어/필터가 바뀔 때마다 그리드를 통째로 새로 그린다.
           스크롤 진입 애니메이션은 "스크롤해서 처음 만나는 콘텐츠"를 위한
           것이지, 검색창에 한 글자 칠 때마다 이미 봤던 카드를 다시 재생하라는
           뜻이 아니다. 그래서 강의 id별로 "이미 한 번 공개됐는지"를 기억해
           두고, 이미 공개된 강의의 카드는 재렌더 시 처음부터 보이는 상태로
           만든다(reveal-on-scroll을 아예 붙이지 않음) — 애니메이션은 강의당
           최대 1회만 재생된다. */
        var revealedLessonIds = {};

        function render() {
            if (!grid) return;
            var keyword = (searchInput && searchInput.value || "").trim().toLowerCase();
            var category = activeCategory;
            var difficulty = difficultySelect ? difficultySelect.value : "";
            /* 진도는 화면에 표시하지도, 거르지도 않는다. 카드에 남은 유일한
               진도 흔적은 퀴즈 최고 점수뿐이라 여기서만 읽는다. */
            var progressState = window.AllProgress ? window.AllProgress.getState() : { lessons: {} };

            grid.textContent = "";
            var shown = 0;

            lessons.forEach(function (lesson) {
                var entry = progressState.lessons[lesson.id];

                if (keyword &&
                    lesson.title.toLowerCase().indexOf(keyword) === -1 &&
                    lesson.englishTitle.toLowerCase().indexOf(keyword) === -1 &&
                    lesson.description.toLowerCase().indexOf(keyword) === -1) return;
                if (category && lesson.category !== category) return;
                if (difficulty && lesson.difficulty !== difficulty) return;

                shown += 1;

                /* window.AllReveal이 켜져 있고(reduced-motion이 아니고
                   IntersectionObserver를 지원) 이 강의 카드가 아직 한 번도
                   공개된 적이 없을 때만 reveal-on-scroll을 붙인다 — 카드를
                   DOM에 넣기 전에 붙이므로 삽입되는 순간부터 이미
                   opacity: 0 상태이고("보였다가 사라지는" 깜빡임 없음),
                   이미 공개됐던 강의는 이 클래스를 아예 붙이지 않아 재렌더 시
                   페이드 없이 즉시 보인다. */
                var revealOn = Boolean(window.AllReveal && window.AllReveal.enabled) &&
                    !revealedLessonIds[lesson.id];
                var card = el("li", revealOn ? "course-card reveal-on-scroll" : "course-card");
                if (revealOn) {
                    /* forEach 콜백 인자라 강의별로 이미 별도 스코프이므로
                       추가 클로저 없이 lesson.id를 그대로 참조해도 안전하다. */
                    card.addEventListener("all:revealed", function () {
                        revealedLessonIds[lesson.id] = true;
                    });
                }

                /* 카드 전체가 자기 학습 영역 색을 연다 — 띠, 아이콘 타일,
                   회차 숫자, 분류 칩, hover 테두리가 모두 이 한 줄을 따라간다.
                   표에 없는 분류면 붙이지 않고 중립색으로 남긴다. */
                var catKey = categoryKey(lesson.category);
                if (catKey) card.setAttribute("data-cat", catKey);

                var top = el("div", "course-card__top");

                var icon = el("span", "course-card__icon");
                icon.setAttribute("aria-hidden", "true");
                /* innerHTML의 내용은 위 CATEGORY_ICONS의 하드코딩된 리터럴뿐이다
                   (강의 데이터가 섞여 들어가는 자리가 없다). */
                icon.innerHTML = CATEGORY_ICONS[catKey] || CATEGORY_ICONS.fallback;
                top.appendChild(icon);

                top.appendChild(el("span", "course-card__no", String(lesson.order).padStart(2, "0")));

                var categoryBadge = el("span", "badge badge--category", lesson.category);
                if (catKey) categoryBadge.setAttribute("data-cat", catKey);
                top.appendChild(categoryBadge);
                card.appendChild(top);

                var h3 = el("h3");
                var link = el("a", null, lesson.order + "강. " + lesson.title);
                link.href = lesson.path;
                h3.appendChild(link);
                card.appendChild(h3);

                card.appendChild(el("p", "course-card__english", lesson.englishTitle));
                card.appendChild(el("p", "course-card__desc", lesson.description));

                /* 난이도와 언어는 아랫줄로 내린다. 윗줄은 "이 강의가 어느
                   영역인가" 하나만 말하게 두어야 색이 흩어지지 않는다. */
                var meta = el("div", "course-card__meta");
                meta.appendChild(el("span",
                    "badge " + (lesson.difficulty === "초급" ? "badge--beginner" : "badge--intermediate"),
                    lesson.difficulty));
                meta.appendChild(el("span", "badge", lesson.language));
                if (entry && entry.quizBest) {
                    var quiz = el("span", "course-card__quiz");
                    quiz.appendChild(document.createTextNode("퀴즈 최고 "));
                    quiz.appendChild(el("b", null, entry.quizBest.score + "/" + entry.quizBest.total));
                    meta.appendChild(quiz);
                }
                meta.appendChild(el("span", "course-card__examples", "실행 예제 " + lesson.examples + "개"));
                card.appendChild(meta);

                grid.appendChild(card);
            });

            if (countLabel) {
                countLabel.textContent = shown + " / " + lessons.length + "강";
            }
            if (emptyMsg) {
                emptyMsg.classList.toggle("is-shown", shown === 0);
            }

            /* 아직 공개되지 않아 reveal-on-scroll이 붙은 카드만 골라 스크롤
               진입 관찰을 다시 건다(이미 공개된 강의의 카드는 애초에 이
               클래스가 없으므로 관찰이 필요 없다). render()는 검색어/필터가
               바뀔 때마다 그리드를 통째로 새로 그리므로, 매번 호출해 이전
               카드(이미 제거됨)에 대한 관찰을 해제하고 새 카드를 관찰
               대상에 올린다 — 필터링으로 다시 나타난 카드가 opacity: 0에
               갇힌 채 남는 경우가 없다. */
            if (window.AllReveal) {
                window.AllReveal.observeCourseCards(
                    Array.prototype.slice.call(grid.querySelectorAll(".course-card.reveal-on-scroll")));
            }

            /* 카드를 새로 그렸으니 기울임도 새로 건다 — 이전 카드는 이미 DOM에서
               사라졌으므로 리스너를 따로 걷어 낼 필요가 없다. */
            bindCardTilt(Array.prototype.slice.call(grid.querySelectorAll(".course-card")));
        }

        /* ---------- 카드 3D 기울임 ----------
           카드를 평면 위의 종이가 아니라 손에 든 판으로 만든다. 포인터 위치를
           카드 로컬 좌표(-1..1)로 바꿔 rotateX/rotateY로 기울이고, 같은 좌표를
           --tilt-x/--tilt-y로 흘려 보내 표면 광택(::after)이 포인터를 따라온다.
           아이콘·회차 숫자는 preserve-3d 안에서 translateZ로 한 겹 띄워 두었기
           때문에(landing.css) 기울일 때 카드 면보다 크게 움직여 깊이가 생긴다.

           거친 포인터(터치)에서는 걸지 않는다 — hover 상태가 없어 기울인 채로
           굳어 버리고, 스크롤 중에 카드가 흔들리기만 한다.
           reduced-motion에서도 걸지 않는다. */
        var tiltEnabled = !(window.matchMedia &&
                window.matchMedia("(prefers-reduced-motion: reduce)").matches) &&
            Boolean(window.matchMedia && window.matchMedia("(hover: hover) and (pointer: fine)").matches);

        var MAX_TILT = 7;      /* deg — 이보다 크면 글자가 눈에 띄게 일그러진다 */

        function bindCardTilt(cards) {
            if (!tiltEnabled) return;
            cards.forEach(function (card) {
                var raf = 0;
                var pending = null;

                function apply() {
                    raf = 0;
                    if (!pending) return;
                    card.style.setProperty("--tilt-x", pending.px.toFixed(3));
                    card.style.setProperty("--tilt-y", pending.py.toFixed(3));
                    card.style.transform =
                        "perspective(900px) rotateX(" + (-pending.py * MAX_TILT).toFixed(2) + "deg)" +
                        " rotateY(" + (pending.px * MAX_TILT).toFixed(2) + "deg)" +
                        " translateY(-6px) scale(1.015)";
                }

                card.addEventListener("pointermove", function (event) {
                    if (event.pointerType === "touch") return;
                    var rect = card.getBoundingClientRect();
                    if (!rect.width || !rect.height) return;
                    pending = {
                        px: ((event.clientX - rect.left) / rect.width) * 2 - 1,
                        py: ((event.clientY - rect.top) / rect.height) * 2 - 1
                    };
                    /* 포인터 이벤트는 프레임당 여러 번 온다 — 실제 반영은
                       한 프레임에 한 번으로 묶는다. */
                    if (!raf) raf = window.requestAnimationFrame(apply);
                    card.classList.add("is-tilting");
                });

                card.addEventListener("pointerleave", function () {
                    if (raf) {
                        window.cancelAnimationFrame(raf);
                        raf = 0;
                    }
                    pending = null;
                    card.classList.remove("is-tilting");
                    /* 인라인 transform을 비워 CSS의 원래 상태로 되돌린다 —
                       :hover 규칙이나 reveal-on-scroll의 transform과 싸우지 않는다. */
                    card.style.transform = "";
                    card.style.removeProperty("--tilt-x");
                    card.style.removeProperty("--tilt-y");
                });
            });
        }

        [searchInput, difficultySelect].forEach(function (control) {
            if (!control) return;
            control.addEventListener("input", render);
            control.addEventListener("change", render);
        });

        buildChips();
        render();

        /* 다른 탭/강의 페이지에서 퀴즈를 풀면 카드의 최고 점수가 따라가야 한다. */
        document.addEventListener("all:progresschange", render);

        /* ---------- 추가 정보 카드 ----------
           강의 카드와 같은 기울임·색 배선을 쓰되 마크업은 따로다. 진도도,
           난이도 배지도, 검색·필터도 없다 — 두 장뿐이고 커리큘럼 밖이라
           고를 것이 없기 때문이다. 대신 "이 문서를 읽고 나면 몇 강 코드를
           바꿔 볼 수 있는가"를 카드에 적는다. */
        var SUPPLEMENT_ICONS = {
            /* 람다 — 여러 줄짜리 블록이 화살표 하나로 넘어간다 */
            "lambda-expressions": svgIcon('<rect x="2.5" y="6.5" width="7.5" height="11" rx="2.4"/>' +
                '<path d="M12.6 12h8"/><path d="m17.2 8 4 4-4 4"/>'),
            /* 스트림 — 위에서 아래로 좁아지며 걸러진다 */
            "java-streams": svgIcon('<path d="M3.2 5.4h17.6"/><path d="M6.6 11.2h10.8"/>' +
                '<path d="M9.8 17h4.4"/>' +
                '<circle cx="12" cy="21" r="1.4" fill="currentColor" stroke="none"/>')
        };

        function renderExtras() {
            var host = document.getElementById("extra-grid");
            var items = window.SUPPLEMENTS || [];
            if (!host || !items.length) return;

            host.textContent = "";
            items.forEach(function (item) {
                var card = el("li", "extra-card");
                var catKey = categoryKey(item.category);
                if (catKey) card.setAttribute("data-cat", catKey);

                var top = el("div", "extra-card__top");
                var icon = el("span", "extra-card__icon");
                icon.setAttribute("aria-hidden", "true");
                /* innerHTML의 내용은 위 SUPPLEMENT_ICONS의 하드코딩된 리터럴뿐이다. */
                icon.innerHTML = SUPPLEMENT_ICONS[item.id] || CATEGORY_ICONS.fallback;
                top.appendChild(icon);
                top.appendChild(el("span", "extra-card__kicker", item.summary));
                card.appendChild(top);

                var h3 = el("h3");
                var link = el("a", null, item.title);
                link.href = item.path;
                h3.appendChild(link);
                card.appendChild(h3);

                card.appendChild(el("p", "extra-card__english", item.englishTitle));
                card.appendChild(el("p", "extra-card__desc", item.description));

                var meta = el("div", "extra-card__meta");
                var badge = el("span", "badge badge--supplement", "보충 자료");
                if (catKey) badge.setAttribute("data-cat", catKey);
                meta.appendChild(badge);
                meta.appendChild(el("span", "badge", item.language));

                var related = (item.relatedLessons || []).map(function (order) {
                    return order + "강";
                }).join(" · ");
                if (related) {
                    var applies = el("span", "extra-card__applies");
                    applies.appendChild(document.createTextNode("바꿔 볼 코드 "));
                    applies.appendChild(el("b", null, related));
                    meta.appendChild(applies);
                }
                card.appendChild(meta);

                host.appendChild(card);
            });

            bindCardTilt(Array.prototype.slice.call(host.querySelectorAll(".extra-card")));
        }

        renderExtras();

        /* ---------- 히어로 라이브 선택 정렬 ---------- */
        var stageHost = document.getElementById("hero-viz-stage");
        var barsHost = document.getElementById("hero-viz-bars");
        var captionHost = document.getElementById("hero-viz-caption");
        var timelineHost = document.getElementById("hero-viz-timeline");
        var toggleBtn = document.getElementById("hero-viz-toggle");
        var shuffleBtn = document.getElementById("hero-viz-shuffle");
        var vizLabel = document.getElementById("hero-viz-label");
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

        /* ---------- 무대 선택 ----------
           three.js 모듈이 실행됐고 WebGL이 있으면 3D 무대를 쓴다. 그렇지
           않으면(CDN 차단·오프라인·WebGL 없음) 아래 2D 막대가 그대로 남는다.
           어느 쪽이든 아래 buildFrames()가 만든 같은 프레임 배열을 받으므로
           화면에 보이는 알고리즘 동작은 동일하다. */
        var stage = null;
        if (stageHost && window.AllHero3D) {
            stage = window.AllHero3D.create(stageHost, {
                reducedMotion: reducedMotion,
                maxValue: MAX_VALUE
            });
        }
        if (stage) {
            stageHost.classList.add("is-3d");
            if (vizLabel) vizLabel.textContent = "LIVE 3D — SELECTION SORT · 4강에서 직접 구현합니다";
        }

        var REDUCED_NOTICE =
            "선택 정렬의 한 장면입니다. 애니메이션 축소 설정이 감지되어 자동 재생을 멈췄습니다. ▶ 버튼으로 직접 넘겨볼 수 있습니다.";

        /* 캡션은 항상 현재 재생 상태(timer)를 그대로 반영한다 — reset()/재생/정지
           어느 경로를 거치든 "일시정지인데 실행 중"이라고 거짓말하지 않는다. */
        function updateCaption() {
            if (!captionHost) return;
            captionHost.textContent = "";
            if (!timer && reducedMotion) {
                captionHost.textContent = REDUCED_NOTICE;
                return;
            }
            var frame = frames[frameIndex];
            var prefix = timer ? "선택 정렬 실행 중 · " : "일시 정지됨 · ";
            captionHost.appendChild(el("b", null, prefix));
            captionHost.appendChild(document.createTextNode(frame ? frame.text : ""));
        }

        function buildBars() {
            if (stage) {
                stage.setValues(values);
                return;
            }
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
            if (stage) {
                stage.setFrame(frame);
            } else {
                frame.arr.forEach(function (value, i) {
                    var bar = bars[i];
                    bar.style.height = Math.round((value / MAX_VALUE) * 100) + "%";
                    bar.querySelector(".hero-viz__bar-value").textContent = value;
                    bar.classList.toggle("is-done", i <= frame.sortedUpto);
                    bar.classList.toggle("is-min", i === frame.min);
                    bar.classList.toggle("is-compare", i === frame.compare);
                });
            }
            updateCaption();
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
            updateCaption();
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
            updateCaption();
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
            stopAuto();   /* updateCaption()이 reducedMotion + 정지 상태를 감지해 안내 문구를 그린다 */
        }
    });
})();
