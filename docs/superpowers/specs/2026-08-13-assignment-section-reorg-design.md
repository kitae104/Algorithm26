# 프로그램 실습 과제 섹션 재구성 설계

작성일: 2026-08-13

## 배경

13개 강의 페이지는 모두 같은 20섹션 구조(`docs/adding-lessons.md`)를 공유한다. 그중
`sec-practice`(14번, 따라 하기 실습), `sec-application`(15번, 실제 데이터 응용 예제),
`sec-final`(16번, 최종 프로그램 작성 문제), `sec-answer`(17번, 최종 문제 정답과 해설)가
`sec-quiz`(확인 퀴즈) **앞**에 놓여 있다.

`sec-practice`와 `sec-final`/`sec-answer`는 강의를 "완료로 표시"해야 정답이 열리는
(`details.answer-box[data-locked-until-complete]`) 실습 성격의 콘텐츠다. 이 둘을
"프로그램 실습 과제"로 명확히 재포장하고 확인 퀴즈 **뒤**로 옮겨, 이론 학습(1~13번) →
응용 감상(15번) → 자기 점검(퀴즈) → 직접 실습(과제) 순서로 학습 흐름을 재배치한다.

## 범위

- 대상: `algorithms/01-*.html` ~ `algorithms/13-*.html` 전체 13개 파일.
- 대상 밖(현재 위치 유지): `sec-application`(실제 데이터 응용 예제), `sec-modernize`(람다·스트림
  수정 문제, 2·3·4·5·10·13강에만 존재).

## 구조 변경

### 섹션 병합 및 이동

- `sec-final`(최종 프로그램 작성 문제)과 `sec-answer`(최종 문제 정답과 해설)를 하나의 섹션으로
  합친다. `sec-answer`의 내용(정답 코드 + 해설)은 `sec-final` 섹션 끝에 붙는
  `details.answer-box[data-locked-until-complete]`가 된다 — 기존 실습 3번이 이미 쓰고 있는
  패턴과 동일. 병합 후 `id="sec-answer"`는 사라지고 `id="sec-final"` 하나만 남는다.
- `sec-practice`(따라 하기 실습)와 병합된 `sec-final`을 `sec-quiz` 바로 뒤, `sec-summary` 앞으로
  옮긴다.
- 결과: 20개 섹션 → **19개 섹션**. 새 순서(공통 부분만 표기):
  `... → sec-complexity → sec-application → [sec-modernize] → sec-quiz → sec-practice →
  sec-final → sec-summary → sec-next`

### 정답 잠금

기존 `data-locked-until-complete` 메커니즘(`assets/js/common.js`)을 그대로 사용한다. 이 속성이
붙은 `details.answer-box`는 위치와 무관하게 강의를 완료로 표시하기 전까지 잠긴 채로 남는다 —
동작 변경 없음, 위치만 이동.

### 과제 묶음 표기

`sec-practice` 섹션 시작 직전에 안내 배너를 삽입한다(별도 `.lesson-section`이 아닌 장식용
`div`, 목차에는 나타나지 않음):

```html
<div class="assignment-banner">
    <span class="assignment-banner__title">📝 프로그램 실습 과제</span>
    <p>지금까지 배운 내용을 직접 코드로 적용해 보는 과제입니다. 두 과제로 구성되며, 각 과제의
    정답은 이 강의를 완료로 표시해야 열립니다.</p>
</div>
```

### 섹션 번호 배지 재계산

`<span class="section-no">` 값은 하드코딩된 2자리 숫자다(목차 번호는 JS가 DOM 순서로 자동
계산하므로 손대지 않아도 되지만, 배지는 손으로 갱신해야 함). 재배치 후 새 DOM 순서대로
01부터 다시 채운다. `<!-- N. 제목 -->` HTML 주석도 같은 번호로 갱신한다(주석은 렌더링되지
않지만 유지보수 편의를 위해 맞춘다).

## 시각적 구분 (색상)

`assets/css/common.css`에 새 토큰 쌍을 보라(violet) 계열로 추가한다. 기존 팔레트(brand=파랑,
state-done=초록, state-compare=주황, state-error=빨강)와 겹치지 않아 확인 퀴즈(파랑 액센트
유지)와 과제 섹션이 색으로 구분된다.

- `--assignment`, `--assignment-bg` — 라이트 기본(`:root`), 다크 미디어 쿼리, `[data-theme="light"]`,
  `[data-theme="dark"]` 4곳 모두 정의 (기존 `--brand` 패턴과 동일하게 별도 accessible-ink 변형
  없이 자체 대비로 충분한 값 사용). 값:
  - 라이트: `--assignment: #6d28d9`, `--assignment-bg: #efe7fe`
  - 다크: `--assignment: #c4a3ff`, `--assignment-bg: #2c2350`
- `assets/css/print.css`의 `@media print` 블록에도 인쇄 안전 값으로 추가 (`scripts/validate.mjs`가
  `:root`의 모든 색 토큰이 print.css에 재정의됐는지 검사하므로 필수): `--assignment: #4c1d95`,
  `--assignment-bg: #efe7fe`.

`.lesson-section--assignment` 클래스를 병합된 `sec-practice`, `sec-final` 두 섹션에 부여:

```css
.lesson-section--assignment {
    border-left: 4px solid var(--assignment);
}
.lesson-section--assignment > h2 .section-no {
    color: var(--assignment);
    background: var(--assignment-bg);
}
```

`.assignment-banner`도 같은 토큰으로 스타일링(기존 `.note-box` 계열과 유사한 형태, 좌측 강조선
+ 패딩).

## 부수 변경

### `scripts/validate.mjs`

- `REQUIRED_SECTIONS` 배열에서 `sec-answer` 제거, 새 순서(`sec-application`, `sec-quiz`,
  `sec-practice`, `sec-final`, `sec-summary`, `sec-next` 순)로 재배열 — 섹션 순서 경고 검사
  (147~153행)가 새 구조를 기준으로 통과하도록.
- 그 외 검사(코드 카드 id, 링크, 람다·스트림 검사 등)는 `sec-answer`를 직접 참조하지 않으므로
  수정 불필요.

### `docs/adding-lessons.md`, `docs/curriculum.md`

"20개 섹션" 표/설명을 새 19개 섹션·새 순서·병합된 `sec-final` 설명(정답이 그 안의 잠금 상자로
포함됨을 명시)으로 갱신한다. 향후 강의 추가 시 기준이 되는 문서이므로 실제 구조와 어긋나면 안
된다.

## 구현 방식

13개 파일이 동일한 마커(`<!-- N. 제목 -->` 주석, `id="sec-..."`, `data-toc-label`)를 공유하므로
Node 스크립트로 일괄 처리한다:

1. 각 파일에서 `sec-practice`, `sec-final`, `sec-answer` 세 `<section>` 블록을 추출.
2. `sec-answer`의 본문(정답 `details.answer-box`)을 `sec-final` 끝에 이식하고 `sec-answer`
   섹션 래퍼는 제거.
3. `sec-practice` + 병합된 `sec-final`에 `lesson-section--assignment` 클래스 추가, 안내 배너를
   `sec-practice` 앞에 삽입.
4. 두 섹션을 원래 자리에서 제거하고, `sec-quiz` 섹션 뒤에 재삽입.
5. 파일 전체를 다시 스캔해 `section-no` 배지와 `<!-- N. -->` 주석을 새 순서로 재계산.
6. `sec-modernize` 유무는 섹션 존재 여부로 자동 판단(있으면 그 자리 그대로 유지, 번호만 밀림).

처리 후 13개 파일 diff를 눈으로 검수하고 `node scripts/validate.mjs`로 검증한다.

## 배포

모든 검증을 통과하면 `main`에 커밋 후 `git push` — Vercel이 정적 사이트를 자동 배포(별도 빌드
없음)하므로 push가 곧 배포. 실제 push 전에 사용자에게 재확인한다.

## 테스트 계획

- `node scripts/validate.mjs` 통과 (섹션 id 존재, code-copy id, 내부 링크, 순서 경고 없음 등).
- 13개 파일 중 최소 2개(람다·스트림 섹션 있는 강의 1개 + 없는 강의 1개)를 브라우저로 열어
  확인: 목차 순서·번호, 완료 표시 전/후 정답 잠금 동작, 과제 섹션 색상, 라이트/다크 테마 모두.
