/*
 * 랜딩 페이지
 * - window.ALGORITHMS 기반 강의 카드 렌더링
 * - 제목 검색 / 분류 필터 / 난이도 필터
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

    document.addEventListener("DOMContentLoaded", function () {
        var lessons = window.ALGORITHMS || [];

        /* ---------- 통계 ---------- */
        var totalExamples = lessons.reduce(function (sum, lesson) {
            return sum + lesson.examples;
        }, 0);
        var beginnerCount = lessons.filter(function (lesson) { return lesson.difficulty === "초급"; }).length;
        var intermediateCount = lessons.length - beginnerCount;

        function setText(id, text) {
            var node = document.getElementById(id);
            if (node) node.textContent = text;
        }

        setText("stat-total", lessons.length + "개");
        setText("stat-examples", totalExamples + "개");
        setText("stat-beginner", beginnerCount + "개");
        setText("stat-intermediate", intermediateCount + "개");

        /* ---------- 분류 필터 옵션 ---------- */
        var categorySelect = document.getElementById("filter-category");
        if (categorySelect) {
            var categories = [];
            lessons.forEach(function (lesson) {
                if (categories.indexOf(lesson.category) === -1) categories.push(lesson.category);
            });
            categories.forEach(function (category) {
                var option = document.createElement("option");
                option.value = category;
                option.textContent = category;
                categorySelect.appendChild(option);
            });
        }

        /* ---------- 카드 렌더링 ---------- */
        var grid = document.getElementById("course-grid");
        var emptyMsg = document.getElementById("courses-empty");
        var countLabel = document.getElementById("courses-count");
        var searchInput = document.getElementById("course-search");
        var difficultySelect = document.getElementById("filter-difficulty");
        var statusSelect = document.getElementById("filter-status");

        function lessonStatus(entry) {
            if (entry && entry.completed) return "done";
            if (entry && entry.started) return "started";
            return "new";
        }

        function render() {
            if (!grid) return;
            var keyword = (searchInput && searchInput.value || "").trim().toLowerCase();
            var category = categorySelect ? categorySelect.value : "";
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

                /* window.AllReveal이 켜져 있으면(reduced-motion이 아니고
                   IntersectionObserver를 지원하면) 카드를 DOM에 넣기 전에
                   reveal-on-scroll을 붙인다 — 삽입되는 순간부터 이미
                   opacity: 0 상태이므로 "보였다가 사라지는" 깜빡임이 없다. */
                var revealOn = Boolean(window.AllReveal && window.AllReveal.enabled);
                var card = el("li", revealOn ? "course-card reveal-on-scroll" : "course-card");

                var top = el("div", "course-card__top");
                top.appendChild(el("span", "course-card__no", String(lesson.order).padStart(2, "0")));
                top.appendChild(el("span", "badge badge--category", lesson.category));
                top.appendChild(el("span",
                    "badge " + (lesson.difficulty === "초급" ? "badge--beginner" : "badge--intermediate"),
                    lesson.difficulty));

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

                var meta = el("div", "course-card__meta");
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
                countLabel.textContent = shown + " / " + lessons.length + "개 강좌";
            }
            if (emptyMsg) {
                emptyMsg.classList.toggle("is-shown", shown === 0);
            }

            /* 방금 그린 카드만 골라 스크롤 진입 관찰을 다시 건다. render()는
               검색어/필터가 바뀔 때마다 그리드를 통째로 새로 그리므로, 매번
               호출해 이전 카드(이미 제거됨)에 대한 관찰을 해제하고 새 카드를
               관찰 대상에 올린다 — 필터링으로 다시 나타난 카드가 opacity: 0에
               갇힌 채 남는 경우가 없다. */
            if (window.AllReveal) {
                window.AllReveal.observeCourseCards(
                    Array.prototype.slice.call(grid.querySelectorAll(".course-card")));
            }
        }

        [searchInput, categorySelect, difficultySelect, statusSelect].forEach(function (control) {
            if (!control) return;
            control.addEventListener("input", render);
            control.addEventListener("change", render);
        });

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
