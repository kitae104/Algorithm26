/*
 * 랜딩 페이지
 * - window.ALGORITHMS 기반 강의 카드 렌더링
 * - 제목 검색 / 학습 영역 칩 / 난이도 · 학습 상태 필터
 * - 전체 통계, 학습 진행률, 강의별 학습 상태
 * - 히어로 라이브 선택 정렬 스트립
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
        var statusSelect = document.getElementById("filter-status");

        /* render()는 검색어/필터가 바뀔 때마다 그리드를 통째로 새로 그린다.
           스크롤 진입 애니메이션은 "스크롤해서 처음 만나는 콘텐츠"를 위한
           것이지, 검색창에 한 글자 칠 때마다 이미 봤던 카드를 다시 재생하라는
           뜻이 아니다. 그래서 강의 id별로 "이미 한 번 공개됐는지"를 기억해
           두고, 이미 공개된 강의의 카드는 재렌더 시 처음부터 보이는 상태로
           만든다(reveal-on-scroll을 아예 붙이지 않음) — 애니메이션은 강의당
           최대 1회만 재생된다. */
        var revealedLessonIds = {};

        function lessonStatus(entry) {
            if (entry && entry.completed) return "done";
            if (entry && entry.started) return "started";
            return "new";
        }

        function render() {
            if (!grid) return;
            var keyword = (searchInput && searchInput.value || "").trim().toLowerCase();
            var category = activeCategory;
            var difficulty = difficultySelect ? difficultySelect.value : "";
            var statusFilter = statusSelect ? statusSelect.value : "";
            var progressState = window.AllProgress ? window.AllProgress.getState() : { lessons: {} };

            grid.textContent = "";
            var shown = 0;

            lessons.forEach(function (lesson) {
                var entry = progressState.lessons[lesson.id];
                var status = lessonStatus(entry);

                if (keyword &&
                    lesson.title.toLowerCase().indexOf(keyword) === -1 &&
                    lesson.englishTitle.toLowerCase().indexOf(keyword) === -1 &&
                    lesson.description.toLowerCase().indexOf(keyword) === -1) return;
                if (category && lesson.category !== category) return;
                if (difficulty && lesson.difficulty !== difficulty) return;
                if (statusFilter && status !== statusFilter) return;

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

                var statusLabel = { done: "완료 ✓", started: "학습 중 …", new: "미시작" }[status];
                top.appendChild(el("span", "course-card__status is-" + status, statusLabel));
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
        }

        [searchInput, difficultySelect, statusSelect].forEach(function (control) {
            if (!control) return;
            control.addEventListener("input", render);
            control.addEventListener("change", render);
        });

        buildChips();
        render();

        /* ---------- 전체 진행률 ---------- */
        function renderProgress() {
            if (!window.AllProgress) return;
            var overall = window.AllProgress.overall();
            var fill = document.getElementById("overall-progress-fill");
            var label = document.getElementById("overall-progress-text");
            if (fill) fill.style.width = overall.percent + "%";
            if (label) {
                label.textContent = "";
                label.appendChild(document.createTextNode("전체 진행률 "));
                label.appendChild(el("b", null, overall.percent + "%"));
                label.appendChild(document.createTextNode(
                    " · 완료 " + overall.completed + "개 / 학습 중 " + overall.started + "개 / 전체 " + overall.total + "개"));
            }
        }

        renderProgress();
        document.addEventListener("all:progresschange", function () {
            renderProgress();
            render();
        });

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
