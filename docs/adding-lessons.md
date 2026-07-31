# 새 강의 추가 가이드 (제작 계약서)

이 문서는 Algorithm Learning Lab에 새 강의를 추가할 때 반드시 따라야 하는 규칙이다.
**모범 예시는 `algorithms/01-algorithm-basics.html`** — 새 강의는 이 파일의 구조를 그대로 따른다.

## 1. 추가 절차 요약

1. `assets/js/algorithms-data.js`의 `window.ALGORITHMS` 배열에 강의 메타데이터 추가
2. `data/algorithms.json`에 같은 항목 추가 (두 파일은 항상 동기화)
3. `examples/java/NN-강의id/` 폴더에 Java 예제 작성 → **컴파일·실행 검증**
4. `algorithms/NN-강의id.html` 작성 (아래 구조 준수)
5. `node scripts/validate.mjs`로 검증

## 2. 강의 HTML 필수 구조

### head / body 뼈대

- `<html lang="ko">`, UTF-8, viewport 메타 필수
- `<title>N강. 제목 — 초보 개발자를 위한 필수 알고리즘</title>`
- CSS(순서대로): Pretendard CDN → `../assets/css/common.css` → `lesson.css` → `code.css` → `visualization.css` → `quiz.css` → `print.css`
- `<head>` 마지막에 `<script src="../assets/js/theme.js"></script>` (동기 로드)
- `<body data-lesson-id="강의id" data-lesson-order="N">`
- body 시작: skip-link → `<header class="site-header" data-site-header></header>` (내용은 common.js가 채움)
- `<div class="lesson-layout">` 안에:
  - `<section class="lesson-hero">` (eyebrow `LESSON NN / 13`, h1, 영문 제목, 배지, 소개)
    - 인쇄 버튼은 두지 않는다. 화면에서 인쇄 기능을 제공하지 않으며, `print.css`는 브라우저 자체 인쇄(Ctrl+P) 대비용으로만 남아 있다.
  - `<nav class="lesson-toc"><p class="lesson-toc__title">이 강의의 목차</p><ol id="lesson-toc-list"></ol></nav>` (목차는 자동 생성)
  - `<div class="lesson-body">` 안에 20개 섹션
- body 끝 스크립트(순서대로): `algorithms-data.js`, `progress.js`, `common.js`, `code-copy.js`, `visualization.js`, `quiz.js`, 그리고 인라인 `<script>`(시각화 + 퀴즈 초기화)

### 20개 섹션 (id와 순서 고정)

각 섹션은 `<section class="lesson-section" id="..."><h2 data-toc-label="..."><span class="section-no">NN</span>제목</h2>` 형태.

| # | id | 내용 |
|---|----|------|
| 1 | `sec-intro` | 강의 소개 (문단 2개, 표 없음) |
| 2 | `sec-objectives` | 학습 목표 4~6개 (`ul.objective-list`, 행동 동사로) |
| 3 | `sec-prereq` | 선수 지식 표 |
| 4 | `sec-problem` | 실제 문제 상황 (3~6문장 스토리) |
| 5 | `sec-hand` | 사람이 직접 해결 (`div.hand-step` + 질문은 `details.reveal-box`로 접기) |
| 6 | `sec-concepts` | 핵심 개념 |
| 7 | `sec-steps` | 단계별 동작 과정 — AlgoViz 시각화 마운트 `<div id="viz-..."></div>` |
| 8 | `sec-pseudo` | 의사코드 (code-card, 언어 배지 `Pseudo`) |
| 9 | `sec-impl` | 점진적 Java 구현 (구현 1~4단계, 각 단계 code-card + 출력 card) |
| 10 | `sec-complete` | 완성 Java 코드 + 실행 결과 |
| 11 | `sec-trace` | 실행 과정 추적 코드 + 실행 결과 |
| 12 | `sec-bugs` | 잘못된 코드 2개 이상 (`article.bug-card`, 해설은 `details.answer-box`) |
| 13 | `sec-complexity` | 복잡도 및 특성 (입력 크기·주요 연산·시간·공간·특성) |
| 14 | `sec-practice` | 따라 하기 실습 2~3개 (실습3은 TODO 코드 + `details.answer-box` 정답) |
| 15 | `sec-application` | 실제 데이터 응용 예제 (문제 상황→데이터 모델→전체 코드→출력→해설→복잡도) |
| 16 | `sec-final` | 최종 프로그램 작성 문제 (제목·배경 4~7문장·목표·필수 요구사항·입력·예상 출력·제한·구현 단계 안내·시작 코드·테스트 3종 표·자기 점검표 `ul.checklist`·추가 도전 2~3개) |
| 17 | `sec-answer` | 정답과 해설 — 반드시 `details.answer-box` 내부에 (정답 코드·예상 출력·구현 순서·핵심 적용부·복잡도·자주 나는 오류·다른 방법) |
| 18 | `sec-quiz` | `<div id="quiz-root"></div>` 만 두고 인라인 스크립트에서 초기화 |
| 19 | `sec-summary` | 오늘의 핵심 정리 + `<div id="lesson-complete-slot"></div>` |
| 20 | `sec-next` | 다음 강의 연결 + `<div id="lesson-pager"></div>` |

### 코드 카드

```html
<article class="code-card">
    <header class="code-card__header">
        <div class="code-card__info">
            <span class="code-card__language">Java</span>
            <strong class="code-card__filename">ClassName.java</strong>
        </div>
        <button type="button" class="copy-code-button" data-copy-target="고유-id"
                aria-label="ClassName.java 코드 복사">코드 복사</button>
    </header>
    <pre><code id="고유-id" class="language-java">...코드 (&lt; &gt; &amp; 는 HTML 이스케이프!)...</code></pre>
</article>
```

- 실행 결과는 별도 카드: `<article class="code-card code-card--output">`, 언어 배지 `출력`, code에 `class` 없음.
- **`<`, `>`, `&`는 반드시 `&lt;` `&gt;` `&amp;`로 이스케이프** (제네릭 `List<Integer>` 주의!)
- 코드 중간 생략(`...`) 금지. 모든 카드가 독립적으로 컴파일 가능해야 함.
- `data-copy-target` id는 페이지 내에서 고유해야 함.

### 시각화 (AlgoViz)

인라인 스크립트에서 `AlgoViz.create({ mount, title, legend, initialInput, makeSteps, makeInput })` 호출.
`makeSteps(input)`는 **실제 알고리즘을 그대로 수행하며** 단계 배열을 만든다 (시각화와 코드의 동작이 달라선 안 됨).

step: `{ caption, counters: {"비교": 3}, view: [group...] }`
view group 종류: `array`(cells: {v, sub, cls}), `stack`/`queue`(items), `table`(head, rows), `graph`(nodes {id,x,y,cls,sub}, edges {from,to,label,cls,directed}), `frames`(호출 스택), `note`(text).
cls: `compare`(앰버=비교) | `current`(블루=현재) | `done`(그린=확정) | `found` | `error` | `min` | `visited` | `updated` | `faded`.
legend에는 사용한 상태만: `["compare","current","done"]` 등.

### 퀴즈 (AlgoQuiz)

`AlgoQuiz.init("#quiz-root", "강의id", [문항...])` — 8~12문항.
권장 구성: mc 3, tf 2, predict 2, fill 1, debug 1, think 1.
문항 형식은 `assets/js/quiz.js` 상단 주석 참고. think는 `modelAnswer` 필수.
fill의 `accept`는 허용 표기를 2~3개 넣어 관대하게.

## 3. Java 예제 규칙 (`examples/java/NN-강의id/`)

- 파일 구성(권장): `Step1~4*.java`(점진 단계), `*Complete.java`(완성), `*Trace.java`(추적), `*Application.java`(실제 데이터 응용), `*Starter.java`(학생 시작 코드, **TODO 상태로도 컴파일 가능해야 함** — 스텁은 기본값 반환), `*Solution.java`(정답)
- **한 폴더의 모든 최상위 클래스 이름은 서로 달라야 한다** (`javac *.java`로 폴더 전체를 한 번에 컴파일하므로 보조 클래스는 `static` 중첩 클래스로)
- Java 17 문법 범위, 필요한 import 포함, 클래스명 = 파일명, `main()` 포함, 의미 있는 변수명, 핵심 주석, 입력 데이터 포함
- 검증: `javac -encoding UTF-8 *.java` 후 각 클래스 `java -Dfile.encoding=UTF-8 클래스명` 실행
- HTML에 싣는 코드와 출력은 **검증된 파일 내용·실제 출력과 동일**해야 한다

## 4. 데이터 등록

`assets/js/algorithms-data.js`의 항목 필드: `order, id, title, englishTitle, category, difficulty(초급|중급), examples(예제 .java 파일 수), language, description, path`.
`path`는 `algorithms/NN-id.html` (앞에 `/` 없음). 같은 내용을 `data/algorithms.json`에도 반영.

## 5. 접근성·금지 사항 체크리스트

- 색상만으로 상태 구분 금지 (텍스트·아이콘 병행)
- 버튼에 `aria-label`, 표에 `scope`, 접기에는 요약 텍스트
- 정답·해설은 반드시 `details.answer-box` 안에 (바로 노출 금지)
- 완성 코드를 처음부터 제시 금지 — 반드시 점진 단계(구현 1→4단계) 후 완성
- 핵심 알고리즘을 `Arrays.sort()` 등 라이브러리 호출로 대체 금지 (비교용으로 분리 제공은 허용)
- 시각화의 동작과 실제 코드의 동작이 일치해야 함
