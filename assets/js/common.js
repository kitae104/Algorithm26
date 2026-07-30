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

        /* ---------- 스크롤 진입 ---------- */
        (function () {
            var reduced = window.matchMedia &&
                window.matchMedia("(prefers-reduced-motion: reduce)").matches;

            /* reduced-motion이거나 IntersectionObserver가 없으면 그냥 보여준다 */
            if (reduced || !("IntersectionObserver" in window)) return;

            /* 랜딩 페이지의 강좌 카드(.course-card)는 landing.js의 별도
               DOMContentLoaded 핸들러가 이 핸들러 뒤에 그려 넣는다. 마이크로태스크는
               각 리스너 실행 직후에 바로 소진되어 너무 이르므로, 매크로태스크로
               미뤄 landing.js의 동기 렌더링까지 모두 끝난 뒤에 대상을 모은다.
               초기 페인트보다 먼저 실행되므로 깜빡임도 없다. */
            setTimeout(function () {
                var targets = document.querySelectorAll(
                    ".lesson-section, .course-card, .how-card, .stat-tile");
                if (!targets.length) return;

                Array.prototype.forEach.call(targets, function (node) {
                    node.classList.add("reveal-on-scroll");
                });

                var revealObserver = new IntersectionObserver(function (entries) {
                    entries.forEach(function (entry) {
                        if (!entry.isIntersecting) return;
                        entry.target.classList.add("is-revealed");
                        revealObserver.unobserve(entry.target);   /* 1회만 — 되돌아가도 재생 안 함 */
                    });
                }, { rootMargin: "0px 0px -8% 0px", threshold: 0.05 });

                Array.prototype.forEach.call(targets, function (node) {
                    revealObserver.observe(node);
                });
            }, 0);
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
