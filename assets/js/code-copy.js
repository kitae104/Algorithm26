/*
 * 1) 코드 카드 복사 버튼: [data-copy-target] 위임 처리 + aria-live 안내
 * 2) 경량 Java 문법 강조: pre code.language-java에 span 토큰 주입
 *    (외부 라이브러리 없음 — 실패해도 원본 텍스트가 그대로 남는다)
 */
(function () {
    /* ---------- aria-live 안내 영역 ---------- */
    var live = document.createElement("div");
    live.className = "visually-hidden";
    live.setAttribute("aria-live", "polite");
    live.id = "copy-live-region";
    document.addEventListener("DOMContentLoaded", function () {
        document.body.appendChild(live);
    });

    /* ---------- 복사 버튼 ----------
       async/await를 쓰지 않는다 — 이 디렉터리는 ES5 문법만 허용한다.
       navigator.clipboard는 Promise를 돌려주므로 .then/.catch로 충분하고,
       API 자체가 없거나 거부될 때는 execCommand 폴백으로 내려간다. */
    function fallbackCopy(text) {
        var textArea = document.createElement("textarea");
        textArea.value = text;
        textArea.style.position = "fixed";
        textArea.style.opacity = "0";
        document.body.appendChild(textArea);
        textArea.select();
        var ok = false;
        try {
            ok = document.execCommand("copy");
        } catch (e) {
            ok = false;
        }
        document.body.removeChild(textArea);
        return ok;
    }

    document.addEventListener("click", function (event) {
        var button = event.target.closest("[data-copy-target]");

        if (!button) {
            return;
        }

        var targetId = button.dataset.copyTarget;
        var codeElement = document.getElementById(targetId);

        if (!codeElement) {
            return;
        }

        var originalText = button.dataset.originalLabel || button.textContent;
        button.dataset.originalLabel = originalText;
        var source = codeElement.textContent;

        function finish(copiedOk) {
            button.textContent = copiedOk ? "복사 완료" : "복사 실패";
            button.classList.toggle("is-copied", copiedOk);
            live.textContent = copiedOk
                ? "코드가 클립보드에 복사되었습니다."
                : "코드 복사에 실패했습니다. 코드를 직접 선택해 복사해 주세요.";

            window.setTimeout(function () {
                button.textContent = originalText;
                button.classList.remove("is-copied");
            }, 1500);
        }

        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(source).then(function () {
                finish(true);
            }, function () {
                finish(fallbackCopy(source));
            });
            return;
        }

        finish(fallbackCopy(source));
    });

    /* ---------- 경량 Java 문법 강조 ---------- */
    var KEYWORDS = new Set(("abstract assert boolean break byte case catch char class const continue default do double " +
        "else enum extends final finally float for goto if implements import instanceof int interface long native new " +
        "package private protected public record return sealed short static strictfp super switch synchronized this throw " +
        "throws transient try var void volatile while yield true false null").split(" "));

    function escapeHtml(text) {
        return text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    }

    function highlightJava(source) {
        var out = "";
        var i = 0;
        var n = source.length;

        function isIdentStart(ch) { return /[A-Za-z_$]/.test(ch); }
        function isIdent(ch) { return /[A-Za-z0-9_$]/.test(ch); }

        while (i < n) {
            var ch = source[i];

            /* 줄/블록 주석 */
            if (ch === "/" && source[i + 1] === "/") {
                var end = source.indexOf("\n", i);
                if (end === -1) end = n;
                var comment = source.slice(i, end);
                var todoMarked = escapeHtml(comment).replace(/(TODO[^<]*)/, '<span class="tok-todo">$1</span>');
                out += '<span class="tok-com">' + todoMarked + "</span>";
                i = end;
                continue;
            }
            if (ch === "/" && source[i + 1] === "*") {
                var endBlock = source.indexOf("*/", i + 2);
                endBlock = endBlock === -1 ? n : endBlock + 2;
                out += '<span class="tok-com">' + escapeHtml(source.slice(i, endBlock)) + "</span>";
                i = endBlock;
                continue;
            }

            /* 문자열/문자 리터럴 */
            if (ch === '"' || ch === "'") {
                var quote = ch;
                var j = i + 1;
                while (j < n && source[j] !== quote) {
                    if (source[j] === "\\") j += 1;
                    j += 1;
                }
                j = Math.min(j + 1, n);
                out += '<span class="tok-str">' + escapeHtml(source.slice(i, j)) + "</span>";
                i = j;
                continue;
            }

            /* 애너테이션 */
            if (ch === "@" && isIdentStart(source[i + 1] || "")) {
                var k = i + 1;
                while (k < n && isIdent(source[k])) k += 1;
                out += '<span class="tok-ann">' + escapeHtml(source.slice(i, k)) + "</span>";
                i = k;
                continue;
            }

            /* 숫자 */
            if (/[0-9]/.test(ch)) {
                var m = i;
                while (m < n && /[0-9_.xXbBlLfFdDeE+-]/.test(source[m])) {
                    if ((source[m] === "+" || source[m] === "-") && !/[eE]/.test(source[m - 1])) break;
                    m += 1;
                }
                out += '<span class="tok-num">' + escapeHtml(source.slice(i, m)) + "</span>";
                i = m;
                continue;
            }

            /* 식별자: 키워드 / 타입(대문자 시작) */
            if (isIdentStart(ch)) {
                var p = i;
                while (p < n && isIdent(source[p])) p += 1;
                var word = source.slice(i, p);
                if (KEYWORDS.has(word)) {
                    out += '<span class="tok-kw">' + word + "</span>";
                } else if (/^[A-Z]/.test(word)) {
                    out += '<span class="tok-type">' + word + "</span>";
                } else {
                    out += escapeHtml(word);
                }
                i = p;
                continue;
            }

            out += escapeHtml(ch);
            i += 1;
        }

        return out;
    }

    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll("pre code.language-java").forEach(function (block) {
            try {
                block.innerHTML = highlightJava(block.textContent);
            } catch (e) {
                /* 강조 실패 시 원본 텍스트 유지 */
            }
        });
    });
})();
