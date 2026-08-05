/*
 * AlgoQuiz — 확인 퀴즈 렌더링/채점
 *
 * 사용법 (강의 페이지 인라인 스크립트):
 *   AlgoQuiz.init("#quiz-root", "lesson-id", [ 문항, ... ]);
 *
 * 문항 형식:
 *   { type:"mc", q:"질문", code:"(선택) 코드", choices:["A","B"], answer:0, explain:"해설" }
 *   { type:"tf", q:"...", answer:true, explain:"..." }
 *   { type:"predict", q:"...", code:"...", choices:[...], answer:1, explain:"..." }  // 코드 결과 예측
 *   { type:"fill", q:"...", code:"...____...", accept:["mid", "(low + high) / 2"], explain:"..." }
 *   { type:"debug", q:"...", code:"...", choices:[...], answer:2, explain:"..." }     // 오류 찾기
 *   { type:"think", q:"...", modelAnswer:"예시 답안" }                                 // 채점 제외
 *
 * 채점: think 제외 문항 수 기준. 정답률 70% 이상이면 강의 완료로 표시.
 * 모든 텍스트는 textContent로 삽입한다.
 */
(function () {
    "use strict";

    var TYPE_LABELS = {
        mc: "객관식",
        tf: "참 / 거짓",
        predict: "코드 결과 예측",
        fill: "빈칸 채우기",
        debug: "오류 찾기",
        think: "생각해 보기"
    };

    var PASS_RATE = 0.7;

    function el(tag, className, text) {
        var node = document.createElement(tag);
        if (className) node.className = className;
        if (text !== undefined && text !== null) node.textContent = String(text);
        return node;
    }

    function normalize(text) {
        return String(text || "").replace(/\s+/g, " ").trim().toLowerCase();
    }

    function init(selector, lessonId, questions) {
        var root = typeof selector === "string" ? document.querySelector(selector) : selector;
        if (!root || !questions || !questions.length) return;

        root.textContent = "";

        var scorable = questions.filter(function (q) { return q.type !== "think"; }).length;

        var intro = el("p", "quiz__intro",
            "총 " + questions.length + "문제입니다. 채점 대상은 " + scorable +
            "문제이며, 정답률 70% 이상이면 이 강의가 완료로 기록됩니다. \"생각해 보기\" 문항은 채점하지 않습니다.");
        root.appendChild(intro);

        var itemNodes = [];

        questions.forEach(function (q, qi) {
            var item = el("article", "quiz-item quiz-" + q.type);
            var name = "quiz-" + lessonId + "-" + qi;

            var head = el("div", "quiz-item__head");
            head.appendChild(el("span", "quiz-item__no", "문제 " + (qi + 1)));
            head.appendChild(el("span", "quiz-item__type", TYPE_LABELS[q.type] || q.type));
            item.appendChild(head);

            item.appendChild(el("p", "quiz-item__q", q.q));

            if (q.code) {
                var pre = el("pre", "quiz-code");
                pre.textContent = q.code;
                item.appendChild(pre);
            }

            var choicesWrap = null;

            if (q.type === "mc" || q.type === "predict" || q.type === "debug") {
                choicesWrap = el("ul", "quiz-choices");
                q.choices.forEach(function (choice, ci) {
                    var li = el("li");
                    var label = el("label", "quiz-choice");
                    var radio = document.createElement("input");
                    radio.type = "radio";
                    radio.name = name;
                    radio.value = String(ci);
                    label.appendChild(radio);
                    label.appendChild(el("span", null, choice));
                    li.appendChild(label);
                    choicesWrap.appendChild(li);
                });
                item.appendChild(choicesWrap);
            } else if (q.type === "tf") {
                choicesWrap = el("ul", "quiz-choices");
                ["참 (O)", "거짓 (X)"].forEach(function (choice, ci) {
                    var li = el("li");
                    var label = el("label", "quiz-choice");
                    var radio = document.createElement("input");
                    radio.type = "radio";
                    radio.name = name;
                    radio.value = ci === 0 ? "true" : "false";
                    label.appendChild(radio);
                    label.appendChild(el("span", null, choice));
                    li.appendChild(label);
                    choicesWrap.appendChild(li);
                });
                item.appendChild(choicesWrap);
            } else if (q.type === "fill") {
                var input = document.createElement("input");
                input.type = "text";
                input.className = "quiz-fill-input";
                input.name = name;
                input.setAttribute("aria-label", "빈칸에 들어갈 코드 입력");
                input.placeholder = "빈칸에 들어갈 내용을 입력하세요";
                item.appendChild(input);
            } else if (q.type === "think") {
                var textarea = document.createElement("textarea");
                textarea.name = name;
                textarea.setAttribute("aria-label", "자유롭게 생각을 적어 보세요");
                textarea.placeholder = "자유롭게 생각을 적어 보세요. 이 문항은 채점하지 않습니다.";
                item.appendChild(textarea);
            }

            var result = el("div", "quiz-item__result");
            result.setAttribute("role", "status");
            item.appendChild(result);

            root.appendChild(item);
            itemNodes.push({ q: q, node: item, result: result, name: name });
        });

        /* 푸터: 채점/다시 풀기/결과 */
        var footer = el("div", "quiz__footer");
        var gradeBtn = el("button", "button button--primary quiz-grade-button", "채점하기");
        gradeBtn.type = "button";
        var retryBtn = el("button", "button quiz-retry-button", "다시 풀기");
        retryBtn.type = "button";
        retryBtn.hidden = true;

        var warning = el("p", "quiz__warning", "");
        var scoreBox = el("div", "quiz__score");
        scoreBox.setAttribute("aria-live", "polite");

        footer.appendChild(gradeBtn);
        footer.appendChild(retryBtn);
        footer.appendChild(warning);
        footer.appendChild(scoreBox);
        root.appendChild(footer);

        function getAnswerValue(entry) {
            var q = entry.q;
            if (q.type === "fill") {
                var input = entry.node.querySelector("input.quiz-fill-input");
                return input && input.value.trim() ? input.value : null;
            }
            if (q.type === "think") {
                return "n/a";
            }
            var checked = entry.node.querySelector("input[name='" + entry.name + "']:checked");
            return checked ? checked.value : null;
        }

        function showResult(entry, kind, verdictText, explainText) {
            entry.result.className = "quiz-item__result is-shown " + kind;
            entry.result.textContent = "";
            entry.result.appendChild(el("span", "verdict", verdictText));
            if (explainText) {
                entry.result.appendChild(el("p", "explain", explainText));
            }
        }

        gradeBtn.addEventListener("click", function () {
            /* 미응답 확인 */
            var unanswered = [];
            itemNodes.forEach(function (entry, i) {
                entry.node.classList.remove("is-unanswered");
                if (entry.q.type === "think") return;
                if (getAnswerValue(entry) === null) {
                    unanswered.push(i + 1);
                    entry.node.classList.add("is-unanswered");
                }
            });

            if (unanswered.length) {
                warning.textContent = "아직 답하지 않은 문제가 있습니다: " + unanswered.join(", ") +
                    "번. 모든 문제에 답한 뒤 채점해 주세요.";
                warning.classList.add("is-shown");
                var firstMissing = root.querySelector(".quiz-item.is-unanswered");
                if (firstMissing) firstMissing.scrollIntoView({ block: "center" });
                return;
            }
            warning.classList.remove("is-shown");

            var score = 0;

            itemNodes.forEach(function (entry) {
                var q = entry.q;
                if (q.type === "think") {
                    showResult(entry, "is-info", "ℹ 채점하지 않는 문항입니다.",
                        q.modelAnswer ? "예시 답안: " + q.modelAnswer : "");
                    return;
                }

                var value = getAnswerValue(entry);
                var correct = false;

                if (q.type === "tf") {
                    correct = (value === "true") === Boolean(q.answer);
                } else if (q.type === "fill") {
                    var normalized = normalize(value);
                    correct = (q.accept || []).some(function (acceptable) {
                        return normalize(acceptable) === normalized;
                    });
                } else {
                    correct = parseInt(value, 10) === q.answer;
                }

                if (correct) {
                    score += 1;
                    showResult(entry, "is-correct", "✅ 정답입니다!", q.explain || "");
                } else {
                    var correctText = "";
                    if (q.type === "tf") {
                        correctText = "정답: " + (q.answer ? "참 (O)" : "거짓 (X)") + ". ";
                    } else if (q.type === "fill") {
                        correctText = "정답: " + (q.accept && q.accept[0] ? q.accept[0] : "") + ". ";
                    } else if (q.choices) {
                        correctText = "정답: " + (q.answer + 1) + "번 — " + q.choices[q.answer] + ". ";
                    }
                    showResult(entry, "is-wrong", "❌ 오답입니다.", correctText + (q.explain || ""));
                }
            });

            var percent = scorable ? Math.round((score / scorable) * 100) : 0;
            var passed = scorable > 0 && score / scorable >= PASS_RATE;

            scoreBox.classList.add("is-shown");
            scoreBox.textContent = "";
            scoreBox.appendChild(el("p", "score-line")).append(
                "채점 결과: ",
                (function () { var b = el("b", null, score + " / " + scorable); return b; })(),
                " (정답률 " + percent + "%)"
            );
            /* 보충 자료(추가 정보) 페이지에는 완료라는 상태가 없다 — 13강
               진도에 세지 않기로 한 자료이므로 "완료로 기록됩니다"라고
               말해서도, 실제로 기록해서도 안 된다. 강의 페이지인지는
               data-lesson-id의 유무로 갈린다(common.js와 같은 기준). */
            var isLesson = Boolean(document.body.dataset.lessonId);

            var sub = el("p", "score-sub");
            if (passed && isLesson) {
                sub.appendChild(el("span", "pass", "✅ 통과! "));
                sub.appendChild(document.createTextNode("이 강의가 완료로 기록되었습니다. 다음 강의로 이동해 보세요."));
            } else if (passed) {
                sub.appendChild(el("span", "pass", "✅ 통과! "));
                sub.appendChild(document.createTextNode("이 문서의 내용을 충분히 이해했습니다. 강의 코드를 직접 바꿔 보세요."));
            } else if (isLesson) {
                sub.appendChild(document.createTextNode(
                    "정답률 70% 이상이면 강의가 완료로 기록됩니다. 해설을 읽고 \"다시 풀기\"로 재도전해 보세요."));
            } else {
                sub.appendChild(document.createTextNode(
                    "해설을 읽고 \"다시 풀기\"로 재도전해 보세요."));
            }
            scoreBox.appendChild(sub);

            if (window.AllProgress) {
                window.AllProgress.setQuizScore(lessonId, score, scorable);
                if (passed && isLesson) {
                    window.AllProgress.markCompleted(lessonId);
                }
            }

            retryBtn.hidden = false;
            scoreBox.scrollIntoView({ block: "nearest" });
        });

        retryBtn.addEventListener("click", function () {
            itemNodes.forEach(function (entry) {
                entry.node.classList.remove("is-unanswered");
                entry.result.className = "quiz-item__result";
                entry.result.textContent = "";
                entry.node.querySelectorAll("input[type=radio]").forEach(function (radio) {
                    radio.checked = false;
                });
                entry.node.querySelectorAll("input[type=text], textarea").forEach(function (field) {
                    field.value = "";
                });
            });
            scoreBox.classList.remove("is-shown");
            warning.classList.remove("is-shown");
            retryBtn.hidden = true;
            root.scrollIntoView({ block: "start" });
        });
    }

    window.AlgoQuiz = { init: init };
})();
