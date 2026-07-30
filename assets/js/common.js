/*
 * 모든 페이지 공통 동작
 * - 상단 내비게이션 구성([data-site-header] 채움: 홈, 강의 목차 드롭다운, 다크 모드, 인쇄)
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
            brand.appendChild(document.createTextNode("초보 개발자를 위한 필수 알고리즘"));
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

            /* 인쇄 (강의 페이지에서만) */
            if (isLessonPage) {
                var printBtn = el("button", "icon-button print-button", "🖨 인쇄");
                printBtn.type = "button";
                printBtn.setAttribute("aria-label", "이 강의 인쇄 (학생용, 정답 숨김)");
                printBtn.addEventListener("click", function () {
                    document.body.classList.remove("print-instructor");
                    window.print();
                });
                nav.appendChild(printBtn);
            }

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
                  잡아낸다. 미지원 브라우저에서는 스크롤/리사이즈/toggle만으로
                  성능이 저하 없이 동작한다(우아한 성능 저하). */
            document.addEventListener("toggle", scheduleUpdate, true);

            if ("ResizeObserver" in window) {
                var heightObserver = new ResizeObserver(scheduleUpdate);
                heightObserver.observe(document.documentElement);
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

        /* ---------- 교수자용 인쇄 버튼 ([data-print]) ---------- */
        document.querySelectorAll("[data-print]").forEach(function (button) {
            button.addEventListener("click", function () {
                var mode = button.dataset.print;
                document.body.classList.toggle("print-instructor", mode === "instructor");
                window.print();
            });
        });
        window.addEventListener("afterprint", function () {
            document.body.classList.remove("print-instructor");
        });

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
