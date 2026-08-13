# 7강 백트래킹 이해 보조 — 핵심 개념 미니 다이어그램 + 단계별 동작 과정 개선

## 배경

7강(재귀와 백트래킹) 학습자 피드백: "핵심 개념" 섹션의 선택·진행·취소(choose·explore·unchoose)
설명이 텍스트와 표뿐이라 이해하기 어렵고, "단계별 동작 과정" 섹션의 부분집합 백트래킹
시각화는 매 단계 chosen/호출 스택만 보여줄 뿐 "지금까지 어떤 결과물이 나왔는지"가 마지막
완료 단계에만 등장해 진행 상황을 놓치기 쉽다. 결정 트리 전체 구조를 보여주는 관점도 없다.

## 범위

`algorithms/07-recursion-and-backtracking.html`의 두 섹션만 대상으로 한다.

1. `sec-concepts` (섹션 06, 핵심 개념) — "백트래킹 — 선택 · 진행 · 취소" 소제목 아래에
   자동 재생 미니 다이어그램 추가.
2. `sec-steps` (섹션 07, 단계별 동작 과정) — 기존 "시각화 2: 부분집합 백트래킹" 수정
   (누적 발견 목록) + "시각화 3: 결정 트리" 신규 추가.

다른 강의, 다른 섹션은 건드리지 않는다. `AlgoViz` 엔진(`assets/js/visualization.js`)
자체는 수정하지 않고 기존에 지원하는 뷰 타입(`array`, `stack`, `frames`, `graph`, `note`)만 사용한다.

## 1. 섹션 06 — 미니 자동 재생 다이어그램

### 목적
선택·진행·취소 3단계를 텍스트만이 아니라 눈으로 한 사이클 보여주는 장식용 다이어그램.
섹션 07에 나올 실제 시각화의 "예고편" 역할도 겸한다(동일한 색 의미 사용).

### 마크업 위치
`algorithms/07-recursion-and-backtracking.html`의 "백트래킹 — 선택 · 진행 · 취소" `<h3>` 바로
아래, 기존 설명 `<p>` 다음(선택·진행·취소 표 앞)에 `<figure class="concept-loop" aria-hidden="true">`
를 삽입한다. 장식 전용이며 스크린 리더에는 이미 존재하는 본문 텍스트/표로 내용이 충분히
전달되므로 `aria-hidden="true"`로 전체를 숨긴다(기존 `.lesson-hero__motif`와 동일한 근거).

구조:
```html
<figure class="concept-loop" aria-hidden="true">
    <svg class="concept-loop__svg" viewBox="0 0 320 130" role="presentation">
        <!-- chosen 괄호 상자 (고정) -->
        <!-- 토큰 (원 + 텍스트, 애니메이션 대상) -->
        <!-- 재귀 호출 프레임 상자 (애니메이션 대상, 진행 단계에만 나타남) -->
    </svg>
    <div class="concept-loop__steps">
        <span class="concept-loop__step concept-loop__step--1">① 선택</span>
        <span class="concept-loop__step concept-loop__step--2">② 진행</span>
        <span class="concept-loop__step concept-loop__step--3">③ 취소</span>
    </div>
</figure>
```

### 애니메이션 타임라인 (6초 루프, `@keyframes` 퍼센트)

| 구간 | 토큰 | 재귀 프레임 상자 | 활성 캡션 |
|------|------|------------------|-----------|
| 0–2% | 바깥, opacity .6, 중립색 | 숨김 | (휴지) |
| 2–20% | 바깥 → chosen 안으로 이동, opacity 1, `--state-visit` 색 | 숨김 | ① 선택 |
| 20–53% | chosen 안에 정지 | 20→30%에 나타남, 45→53%에 사라짐 | ② 진행 |
| 53–76% | chosen 안 → 바깥으로 복귀, opacity .6, 중립색 | 숨김 | ③ 취소 |
| 76–100% | 바깥, 정지 | 숨김 | (휴지) |

- 애니메이션 대상 속성은 `transform`(translateX)과 `opacity`, `fill`/`stroke` 색상 값만 사용한다
  (레이아웃 속성 애니메이션 금지 — 기존 히어로 모티프 주석의 규칙을 그대로 따름).
- 캡션 3개는 위치 이동 없이 항상 보이며, 각자의 활성 구간에서만 `color: var(--state-visit)` +
  `font-weight: 800` + `opacity: 1`로 밝아지고 나머지 구간은 `var(--ink-faint)` + `opacity: .5`
  (기존 `m-drop-1/2/3` 순차 강조 패턴과 동일한 방식).

### 재생 제어
- `assets/js/common.js`에 히어로 모티프 재생 제어(`IntersectionObserver` + `is-playing` 토글)와
  같은 패턴을 `.concept-loop`에도 적용하는 범용 블록을 추가한다(뷰포트에 들어오면 재생,
  나가면 정지). `IntersectionObserver`가 없으면 즉시 재생 상태로 둔다.
- `@media (prefers-reduced-motion: reduce)`에서는 애니메이션을 끄고 "선택" 단계의 정지 프레임
  (토큰이 chosen 안, 캡션 ①만 밝음)으로 고정한다.
- `assets/css/print.css`의 장식 요소 숨김 목록(`.hero-viz` 등이 있는 셀렉터 목록)에
  `.concept-loop`를 추가해 인쇄 시 숨긴다.

### 색상
새 CSS 토큰을 만들지 않는다. 기존 `--state-visit`(현재/방문, 파랑) / `--state-visit-bg`,
`--ink-faint`, `--surface-2`, `--line-strong`만 사용해 섹션 07 시각화의 `current` 색과
시각적으로 통일한다.

## 2. 섹션 07 — 기존 "시각화 2: 부분집합 백트래킹" 수정

`viz-subsets`의 `makeSteps` 안 `view(currentIndex, noteText)` 함수가 반환하는 그룹 배열에
그룹을 하나 추가한다(호출 스택 그룹 다음, note 앞):

```js
{
    type: "array",
    label: "발견된 부분집합 (지금까지 " + found.length + "개)",
    cells: found.map(function (label, i) {
        return { v: label, sub: (i + 1) + "번째", cls: "done" };
    }),
    emptyText: "(아직 없음)"
}
```

`found` 배열은 이미 각 리프(종료 조건) 도달 시 `snap()` 호출 전에 `push`되고 있으므로,
`view()`가 호출되는 모든 스냅샷 시점에 그 시점까지의 정확한 목록을 반영한다(추가 상태
관리 불필요). 결과적으로 학습자는 1단계부터 마지막 단계까지 목록이 0개 → 8개(또는
2/3원소면 4개)로 늘어나는 것을 계속 볼 수 있다.

기존 마지막 "완료" 스텝의 별도 요약 블록("나열된 부분집합 (생성 순서)")은 그대로 둔다
(최종 정리 슬라이드로서 여전히 유효하며, 중간 단계와 형식이 달라도 문제 없다).

## 3. 섹션 07 — "시각화 3: 결정 트리" 신규 추가

### 마크업
"시각화 2" 블록(`viz-subsets`) 다음에 세 번째 소제목과 마운트 지점을 추가한다:

```html
<h3>시각화 3 — {1, 2, 3} 부분집합의 결정 트리</h3>
<p>
    같은 재귀를 트리 전체 관점에서 봅니다. 파란 경로는 지금 내려가 있는 가지,
    초록 노드는 이미 확정된 부분집합입니다. 트리를 완전히 다 그린 채로 시작해
    재귀가 그 위를 어떻게 훑고 지나가는지 보여줍니다.
</p>
<div id="viz-tree" aria-label="부분집합 백트래킹의 결정 트리 시각화"></div>
```

섹션 07 도입부 문단의 "두 개의 시각화로..."를 "세 개의 시각화로..."로 수정한다.

### 데이터 구조
`numbers`(길이 n, 2 또는 3)에 대해 정적 이진 트리를 미리 구성한다. 노드 id는
"이 노드까지 내려오며 결정한 비트열"(`"1"`=포함, `"0"`=제외) 문자열이며, 길이 = depth.
루트 id는 `""`.

- 리프 개수 = 2ⁿ, 전체 노드 수 = 2ⁿ⁺¹ − 1 (n=3이면 15개, n=2면 7개).
- 좌표 계산: `visit(id)` 재귀 함수가 `id + "1"`(포함 가지, 실제 코드에서 먼저 재귀 호출되는
  가지와 동일 순서) → `id + "0"`(제외 가지) 순서로 방문. 리프는 왼쪽부터 순서대로
  x좌표를 배정받고, 내부 노드의 x는 자식 x의 평균(post-order). y = depth 비례.
  좌우 여백(margin)과 상하 여백을 두어 `node-sub` 라벨이 SVG viewBox 밖으로 잘리지 않게 한다.
- 노드 라벨(`label`): 루트는 `"∅"`. 그 외 노드는 마지막 비트가 `"1"`이면 그 depth에
  대응하는 원소 값(`numbers[depth-1]`), `"0"`이면 `"×"`.
- 노드 서브 라벨(`sub`): 그 노드까지 결정된 부분집합 문자열, 예: `"{1,2}"`. 루트/모두
  제외 경로는 `"{}"`.
- 엣지: 모든 (parent id, child id) 쌍. `directed: false`로 단순 선.

### 단계 생성
기존 `viz-subsets`와 동일한 `subsets(index)` 재귀를 별도 IIFE 안에서 한 번 더 실행하되,
이번에는 `pathBits`(현재까지의 비트열 스택)를 함께 관리해 "지금 위치 노드 id"를
`pathBits.join("")`으로 얻는다. 매 스냅샷에서:

- 현재 노드 id부터 루트까지의 조상 노드 전체(`id.slice(0, i)`, i = 0..길이)에 `cls: "current"`.
- 루트→현재 노드 경로의 모든 엣지에 `cls: "current"`.
- 이미 확정된 리프 노드(종료 조건에 도달해 `found`에 기록된 것)에 `cls: "done"`.
- 나머지 노드/엣지는 `cls` 없음(중립).

캡션은 기존 선택/진행/취소 메시지를 트리 용어로 재서술한다(예: "(1) 선택: 1을(를) 넣고
파란 경로를 따라 자식 노드로 내려갑니다." / "(3) 취소: 반대쪽 자식(×) 가지로 이동합니다."
/ 리프 도달 시 "리프 노드 도달 — 부분집합 [1, 2, 3] 확정, 초록으로 칠해집니다.").

완료 스텝에서는 모든 리프가 `done`으로 칠해진 트리 전체를 보여주고, 경로 강조는 없앤다
(현재 위치가 없으므로).

### AlgoViz.create 설정
`viz-subsets`와 별개의 독립 인스턴스로 등록한다:
```js
AlgoViz.create({
    mount: "#viz-tree",
    title: "부분집합 결정 트리 — 경로와 확정된 리프",
    legend: ["current", "done"],
    initialInput: [1, 2, 3],
    makeSteps: makeSteps,
    makeInput: /* viz-subsets와 동일한 2~3개 랜덤 추출 로직 재사용(각각 독립 인스턴스이므로
                  "새 데이터" 버튼은 시각화 2와 별도로 동작해도 무방) */
});
```

## 영향받지 않는 부분

- `assets/js/visualization.js` (AlgoViz 엔진 자체) — 수정 없음, 기존 `graph`/`array` 뷰
  타입만 사용.
- 다른 12개 강의 페이지 — 변경 없음.
- 정답 잠금, 퀴즈, 진행률 로직 — 변경 없음.

## 검증 계획

- `node scripts/validate.mjs` 통과 확인(ES5-only 브라우저 JS 검사 포함 — 화살표 함수,
  `let`/`const`, 템플릿 리터럴 금지 규칙을 그대로 지킨다).
- 브라우저에서 라이트/다크 테마 모두 확인: 미니 다이어그램 자동 재생, 결정 트리 노드/엣지
  색상 및 좌표가 겹치지 않고 읽히는지, "발견된 부분집합" 목록이 단계마다 누적되는지,
  "새 데이터" 버튼으로 원소 개수(2/3개)가 바뀔 때 트리 레이아웃이 정상적으로 재계산되는지.
- `prefers-reduced-motion: reduce` 에뮬레이션으로 미니 다이어그램이 정지 프레임으로
  고정되는지 확인.
