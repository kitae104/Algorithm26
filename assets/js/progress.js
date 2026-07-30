/*
 * 학습 진행률 관리 (localStorage, 키: all-progress-v1)
 * 저장 구조:
 * {
 *   lessons: {
 *     "sorting-algorithms": {
 *       started: true,
 *       completed: false,
 *       quizBest: { score: 8, total: 10 },
 *       lastSection: "sec-10-progressive",
 *       updatedAt: "2026-07-28T09:00:00.000Z"
 *     }
 *   }
 * }
 * 개인정보/로그인 없음. 브라우저별 저장.
 */
(function () {
    var KEY = "all-progress-v1";

    function load() {
        try {
            var raw = localStorage.getItem(KEY);
            if (raw) {
                var parsed = JSON.parse(raw);
                if (parsed && typeof parsed === "object" && parsed.lessons) {
                    return parsed;
                }
            }
        } catch (e) {
            /* 손상된 저장값은 초기화 */
        }
        return { lessons: {} };
    }

    function save(state) {
        try {
            localStorage.setItem(KEY, JSON.stringify(state));
        } catch (e) {
            /* 저장 불가 환경(시크릿 모드 등)에서는 조용히 무시 */
        }
        document.dispatchEvent(new CustomEvent("all:progresschange"));
    }

    function lessonEntry(state, id) {
        if (!state.lessons[id]) {
            state.lessons[id] = { started: false, completed: false, quizBest: null, lastSection: null };
        }
        return state.lessons[id];
    }

    window.AllProgress = {
        getState: load,

        get: function (id) {
            return load().lessons[id] || null;
        },

        markStarted: function (id) {
            var state = load();
            var entry = lessonEntry(state, id);
            if (!entry.started) {
                entry.started = true;
                entry.updatedAt = new Date().toISOString();
                save(state);
            }
        },

        markCompleted: function (id) {
            var state = load();
            var entry = lessonEntry(state, id);
            entry.started = true;
            entry.completed = true;
            entry.updatedAt = new Date().toISOString();
            save(state);
        },

        unmarkCompleted: function (id) {
            var state = load();
            var entry = lessonEntry(state, id);
            entry.completed = false;
            entry.updatedAt = new Date().toISOString();
            save(state);
        },

        setQuizScore: function (id, score, total) {
            var state = load();
            var entry = lessonEntry(state, id);
            var best = entry.quizBest;
            if (!best || score > best.score) {
                entry.quizBest = { score: score, total: total };
            }
            entry.started = true;
            entry.updatedAt = new Date().toISOString();
            save(state);
        },

        setLastSection: function (id, sectionId) {
            var state = load();
            var entry = lessonEntry(state, id);
            entry.lastSection = sectionId;
            save(state);
        },

        /* 전체 진행률: 완료 강의 수 / 전체 강의 수 */
        overall: function () {
            var lessons = (window.ALGORITHMS || []);
            var state = load();
            var completed = 0;
            var started = 0;
            lessons.forEach(function (lesson) {
                var entry = state.lessons[lesson.id];
                if (entry && entry.completed) completed += 1;
                else if (entry && entry.started) started += 1;
            });
            return {
                total: lessons.length,
                completed: completed,
                started: started,
                percent: lessons.length ? Math.round((completed / lessons.length) * 100) : 0
            };
        }
    };
})();
