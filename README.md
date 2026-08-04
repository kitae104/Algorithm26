# 고급알고리즘 — 인하공업전문대학 컴퓨터시스템공학과 전공심화과정

인하공업전문대학 컴퓨터시스템공학과 전공심화과정 「고급알고리즘」 수업에서 쓰는 **Java 기반 13강 웹 강의 사이트**입니다.
각 강의는 이론·실습형 자료로, 단계별 시각화·복사 가능한 실행 코드·오류 찾기·실습 문제·확인 퀴즈를 포함합니다.

- 대상: 인하공업전문대학 컴퓨터시스템공학과 전공심화과정 「고급알고리즘」 수강생
- 언어: Java 17 이상 / 설명은 한국어
- 난이도: 초급 → 중급
- 배포: Vercel 정적 호스팅

## 13개 강좌 목차

| # | 강의 | 분류 | 난이도 |
|---|------|------|--------|
| 1 | 알고리즘과 효율적인 문제 해결 | 알고리즘 기초 | 초급 |
| 2 | 배열과 리스트를 활용한 데이터 처리 | 자료구조 | 초급 |
| 3 | 완전 탐색과 문자열·해시 처리 | 알고리즘 기초 | 초급 |
| 4 | 정렬 알고리즘과 객체 정렬 | 정렬 | 초급 |
| 5 | 순차 탐색과 이진 탐색 | 탐색 | 초급 |
| 6 | 스택과 큐를 활용한 작업 처리 | 자료구조 | 초급 |
| 7 | 재귀 호출과 백트래킹 | 탐색 | 중급 |
| 8 | 트리 구조와 트리 순회 | 자료구조 | 중급 |
| 9 | 그래프와 DFS·BFS 탐색 | 그래프 | 중급 |
| 10 | 그리디 알고리즘 | 설계 기법 | 중급 |
| 11 | 동적 계획법 | 설계 기법 | 중급 |
| 12 | 최단 경로 알고리즘 | 그래프 | 중급 |
| 13 | 알고리즘 종합 프로젝트 | 프로젝트 | 중급 |

강좌 간 연결 구조는 [docs/curriculum.md](docs/curriculum.md) 참고.

## 기술 구성

- **HTML + CSS + Vanilla JavaScript** — 빌드 도구 없는 순수 정적 사이트 (프레임워크·백엔드·DB 없음)
- 문법 강조: 외부 라이브러리 없이 자체 경량 하이라이터 (`assets/js/code-copy.js`) — 실패해도 코드는 그대로 읽힘
- 시각화: 자체 단계(step) 기반 엔진 (`assets/js/visualization.js`)
- 학습 기록: `localStorage` (로그인·개인정보 없음)
- 배포: Vercel 정적 배포 (`vercel.json`)

## 디렉터리 구조

```text
algorithm-learning-lab/
├─ index.html                  # 랜딩 페이지 (강의 카드, 검색·필터, 진행률)
├─ algorithms/                 # 13개 강의 페이지 (01-…13-)
├─ assets/
│  ├─ css/                     # common/landing/lesson/code/visualization/quiz/print
│  └─ js/                      # theme/common/algorithms-data/landing/code-copy/
│                              # visualization/quiz/progress
├─ examples/java/              # 강의별 Java 예제 (단계·완성·추적·응용·시작·정답)
├─ data/algorithms.json        # 강의 메타데이터(JS와 동기화)
├─ docs/
│  ├─ curriculum.md            # 교육과정 연결 구조
│  └─ adding-lessons.md        # 새 강의 추가 절차(제작 계약서)
├─ scripts/validate.mjs        # 구조·링크 검증 스크립트
├─ package.json
└─ vercel.json
```

## 설치와 로컬 실행

정적 사이트라 설치할 의존성이 없습니다. (Node 18+ 권장 — 검증 스크립트용)

```bash
npm install          # 의존성 없음(스크립트 등록용)
npm run dev          # http://localhost:4173 로컬 서버 (npx serve)
```

또는 아무 정적 서버로 프로젝트 루트를 서빙하면 됩니다 (예: `python -m http.server`).

## 빌드(검증)

```bash
npm run build        # scripts/validate.mjs 실행
```

빌드 = 검증입니다. 다음을 확인합니다:
13개 강좌 데이터 정합성, 강의별 20개 필수 섹션, 내부 링크 깨짐, 코드 복사 대상 id, id 중복, Java 예제 폴더 존재.

## Java 예제 실행 방법

각 강의의 예제는 `examples/java/NN-강의id/`에 있습니다.

```bash
cd examples/java/04-sorting-algorithms
javac -encoding UTF-8 *.java
java SelectionSortComplete
```

Windows 콘솔에서 한글이 깨지면 `chcp 65001` 후 `java -Dfile.encoding=UTF-8 클래스명`으로 실행하세요.

## 새로운 강의 추가 방법

절차 요약 (자세한 규칙은 [docs/adding-lessons.md](docs/adding-lessons.md)):

1. **데이터 등록** — `assets/js/algorithms-data.js`의 `window.ALGORITHMS`에 항목 추가, `data/algorithms.json`에도 동일하게 반영.
2. **Java 예제** — `examples/java/NN-id/`에 단계별·완성·추적·응용·시작·정답 코드를 작성하고 컴파일·실행 검증.
3. **강의 HTML** — `algorithms/NN-id.html`을 1강을 본보기로 20개 섹션 구조로 작성.
4. **검증** — `npm run build`.

### 코드 카드 추가

```html
<article class="code-card">
    <header class="code-card__header">
        <div class="code-card__info">
            <span class="code-card__language">Java</span>
            <strong class="code-card__filename">Example.java</strong>
        </div>
        <button type="button" class="copy-code-button" data-copy-target="ex-code"
                aria-label="Example.java 코드 복사">코드 복사</button>
    </header>
    <pre><code id="ex-code" class="language-java">/* 코드 — &lt; &gt; &amp; 는 이스케이프 */</code></pre>
</article>
```

복사 버튼·문법 강조는 `code-copy.js`가 자동 처리합니다.

### 퀴즈 추가

강의 페이지 하단 인라인 스크립트에서:

```js
AlgoQuiz.init("#quiz-root", "강의id", [
    { type: "mc", q: "질문", choices: ["보기1", "보기2"], answer: 0, explain: "해설" },
    { type: "tf", q: "참거짓 질문", answer: true, explain: "해설" }
]);
```

문항 형식 전체는 `assets/js/quiz.js` 상단 주석 참고. 정답률 70% 이상이면 강의가 자동으로 완료 처리됩니다.

### 시각화 추가

```js
AlgoViz.create({
    mount: "#viz-my-algo",
    title: "제목",
    legend: ["compare", "done"],
    initialInput: [5, 3, 8],
    makeSteps: function (input) { /* 실제 알고리즘을 수행하며 step 배열 반환 */ }
});
```

## Vercel 배포 방법

```bash
npm install -g vercel   # CLI가 없다면
vercel login            # 최초 1회 (브라우저/이메일 인증)
vercel --prod           # 프로젝트 루트에서 프로덕션 배포
```

정적 사이트 설정은 `vercel.json`에 있습니다 (빌드 명령 없음, 루트 그대로 서빙).

## 문제 해결

| 증상 | 해결 |
|------|------|
| 강의 카드가 안 보임 | 브라우저 콘솔 확인 — `algorithms-data.js` 문법 오류 여부, `npm run build`로 검증 |
| 코드 복사가 안 됨 | HTTPS 또는 localhost에서만 클립보드 API 동작 (그 외에는 자동 폴백) |
| 다크 모드가 저장 안 됨 | 시크릿 모드에서는 localStorage가 제한될 수 있음 |
| Java 실행 시 한글 깨짐 | `chcp 65001` + `java -Dfile.encoding=UTF-8 …` |
| 진행률 초기화하고 싶음 | 개발자 도구 → Application → localStorage에서 `all-progress-v1` 삭제 |
| 새로고침 404 (Vercel) | 이 사이트는 실제 파일 경로만 사용하므로 발생하지 않음. 커스텀 라우팅 추가 시 `vercel.json` 확인 |
