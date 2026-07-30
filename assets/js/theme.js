/*
 * 다크 모드 토글.
 * FOUC(잘못된 테마 깜빡임)를 막기 위해 <head>에서 동기 로드한다.
 * 저장 키: all-theme ("light" | "dark"), 없으면 시스템 설정을 따른다.
 */
(function () {
    var KEY = "all-theme";

    function systemTheme() {
        return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches
            ? "dark"
            : "light";
    }

    function currentTheme() {
        var saved = null;
        try {
            saved = localStorage.getItem(KEY);
        } catch (e) {
            /* localStorage를 못 쓰는 환경에서는 시스템 설정만 사용 */
        }
        return saved === "dark" || saved === "light" ? saved : systemTheme();
    }

    function apply(theme) {
        document.documentElement.setAttribute("data-theme", theme);
    }

    apply(currentTheme());

    window.AllTheme = {
        get: currentTheme,
        toggle: function () {
            var next = currentTheme() === "dark" ? "light" : "dark";
            try {
                localStorage.setItem(KEY, next);
            } catch (e) {
                /* 저장 실패해도 화면 전환은 수행 */
            }
            apply(next);
            document.dispatchEvent(new CustomEvent("all:themechange", { detail: { theme: next } }));
            return next;
        }
    };
})();
