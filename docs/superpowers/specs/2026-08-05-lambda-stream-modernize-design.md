# 람다·스트림 「수정 문제」 연습 코너 — 설계

작성일: 2026-08-05

## 배경

강의 예제는 전부 for 반복문과 익명 클래스로 쓰여 있다. 커리큘럼 밖 보충 자료
(`supplements/lambda-expressions.html`, `supplements/java-streams.html`)가 람다와 스트림을
가르치고 "강의 코드 바꿔 보기" 절에서 **정답을 보여 주지만**, 학생이 **직접 고쳐 보는 자리**는
없다. 이 설계는 그 자리를 만든다.

## 목표

강의 페이지 안에서, 그 강의의 실제 예제 코드를 학생이 람다·스트림으로 고쳐 보게 한다.

## 제약 — 어휘 잠금 (이 설계의 핵심 규칙)

수정 문제의 **정답 코드에 등장하는 모든 API는 두 보충 자료가 이미 가르친 것이어야 한다.**
가르치지 않은 API가 정답에 나오면 학생은 풀 수 없다.

### 허용 어휘 (보충 자료에서 확인한 목록)

람다 문서(`supplements/lambda-expressions.html`):

- 람다 5형태(`() ->`, `s ->`, `(a, b) ->`, 식 몸통, 중괄호+`return`), 명시적 매개변수 타입
- `@FunctionalInterface`, 사용자 정의 함수형 인터페이스
- `Predicate<T>` · `Function<T,R>` · `Consumer<T>` · `Supplier<T>` · `Comparator<T>` ·
  `BinaryOperator<T>` · `Runnable`
- `Comparator.comparing` · `comparingInt` · `comparingDouble` · `reversed()` · `thenComparing()`
- 메서드 참조 4종(정적 / 특정 객체 / 임의 객체 / 생성자)

스트림 문서(`supplements/java-streams.html`):

- 생성: `Arrays.stream` · `list.stream()` · `Stream.of` · `IntStream.range` ·
  `IntStream.rangeClosed` · `map.entrySet().stream()`
- 중간: `filter` · `map` · `mapToInt` · `sorted` · `distinct` · `limit` · `skip` · `boxed` ·
  `asLongStream`
- 최종: `toList` · `collect` · `count` · `sum` · `average` · `max` · `toArray` · `findFirst` ·
  `anyMatch` / `allMatch` / `noneMatch` · `reduce` · `forEach`
- `Optional`: `orElse` · `orElseThrow` · `map`
- `Collectors`: `toSet` · `toMap` · `groupingBy` · `counting` · `partitioningBy` · `joining` ·
  `toCollection`
- `summaryStatistics()` → `getCount` · `getSum` · `getAverage` · `getMax` · `getMin`
- `Map.Entry.comparingByValue()` · `Map.Entry::getKey`

### 금지 어휘 (보충 자료에 없음 — 정답에 쓰지 않는다)

`Map.merge` · `Map.computeIfAbsent` · `Collectors.averagingInt` · `Collectors.summingInt` ·
`Collectors.summarizingInt` · `IntStream.of` · `flatMap` · `takeWhile` / `dropWhile` ·
`parallelStream` · `Comparator.naturalOrder`

### 보충 자료를 손보는 곳 (틈 메우기)

두 항목은 보충 자료의 **예제 코드에는 있으나 표에는 빠져** 있다. 표만 보고 푸는 학생이 막히므로
`supplements/java-streams.html`의 "최종 연산" 표에 각각 한 줄을 추가한다.

| 추가할 항목 | 필요한 곳 |
|---|---|
| `min()` | 2강 문제 ① 최솟값 |
| `max(Comparator)` | 3강 문제 ② 최빈 단어 (본문 코드 459줄에 이미 쓰이고 있음) |

## 대상 강의 — 6개

람다·스트림으로 바꾸는 것이 **자연스러운** 강의에만 넣는다. 억지로 넣으면 "람다를 쓰면 무조건
좋다"는 오해를 준다.

| 강의 | 원본 예제 | 바꿀 대상 |
|---|---|---|
| 2강 배열과 리스트 | `ScoreStatsComplete.java` | 집계 4종의 개별 순회, 조건 개수 세기, 2회 순회 배열 수집 |
| 3강 문자열·해시 | `WordAnalysisComplete.java` | `getOrDefault` 빈도 누적, 빈도 정렬 |
| 4강 정렬 | `ProductSorterSolution.java` | 블록 몸통 람다 3개(`PRICE_ASC` · `RATING_DESC` · `RATING_DESC_THEN_NAME`) |
| 5강 탐색 | `BookSearchSolution.java` + `BookSearchApplication.java`의 `Book` | `==`로 고정된 순차 탐색 |
| 10강 그리디 | `MeetingRoomComplete.java` | `sortByEndTime`에 박힌 선택 기준 |
| 13강 종합 | `Step4CategoryStats.java` | `containsKey`/`put`/`get` 3단 누적, 조건 검색 |

> **주의 — 보충 자료의 "이전 코드"는 각색본이다.** 보충 자료 "강의 코드 바꿔 보기" 절의
> "이전" 코드(익명 `Comparator` 클래스, `findByTitle`/`findByAuthor`)는 설명을 위해 지어낸
> 것이고 실제 예제 파일에는 없다. 수정 문제의 원본 발췌는 반드시 **실제 파일에서** 가져온다.
> 특히 `ProductSortApplication.java`는 이미 람다를 쓰고 있으므로 대상이 아니다.
>
> 덧붙여 `ProductSortApplication.java`가 쓰는 `Comparator.naturalOrder()`는 보충 자료가
> 가르치지 않는다. 기존 코드이므로 그대로 두되, 수정 문제 정답에는 쓰지 않는다.

**제외한 강의와 이유**

- 1강 — 연산 횟수를 직접 세는 것이 학습 목표라 스트림이 그것을 가린다.
- 6강 · 12강 — `BankQueueApplication`, `DijkstraComplete`가 이미 `Comparator.comparingInt(...)`
  람다를 쓴다. 바꿀 것이 없다.
- 7 · 8 · 9 · 11강 — 재귀 · 트리 순회 · 그래프 탐색 · DP는 반복문과 재귀가 맞는 자리다.

## 문제 목록 (전부 허용 어휘로 검증됨)

### 2강

1. `sum` · `average` · `max` · `min`이 배열을 네 번 훑는다 → `summaryStatistics()` 한 번으로.
   어휘: `Arrays.stream`, `summaryStatistics()`, `getSum` / `getAverage` / `getMax` / `getMin`
2. `countAtLeast`의 조건 세기와 `collectBelow`의 "개수 먼저 세고 배열 다시 채우기" 제거.
   어휘: `filter().count()`, `filter().toArray()`

### 3강

1. `freq.put(w, freq.getOrDefault(w, 0) + 1)` 누적 → 한 줄.
   어휘: `Arrays.stream`, `collect(groupingBy(w -> w, counting()))`
2. 2회 이상 등장한 단어를 **빈도 내림차순, 동점이면 사전순**으로.
   어휘: `entrySet().stream()`, `filter`, `sorted(Map.Entry.comparingByValue().reversed()
   .thenComparing(...))`, `map(Map.Entry::getKey)`, `toList()`

### 4강

1. `PRICE_ASC`와 `RATING_DESC`의 블록 몸통 람다를 **3단계로** 줄이기 —
   식 몸통 람다 → `comparingInt` / `comparingDouble().reversed()` → 메서드 참조.
   어휘: `Comparator.comparingInt`, `comparingDouble`, `reversed()`, 메서드 참조
2. `RATING_DESC_THEN_NAME`의 `if (byRating != 0)` 분기를 `thenComparing()`으로.
   **안정 정렬이 깨지지 않는지**까지 확인한다(원본 코드의 주석이 지적하는 부분).

### 5강

1. `linearSearch(int[] numbers, int target)`는 비교가 `==`로 박혀 있다.
   `BookSearchApplication`의 `Book`을 대상으로 `findFirstIndex(Book[], Predicate<Book>)`로
   일반화해, 번호 · 제목 · 재고 세 가지 조건을 같은 메서드로 검색한다.
   어휘: `Predicate<T>`, `test`
2. **판단 문제** — 순차 탐색은 `Predicate`로 일반화되는데 이진 탐색은 왜 안 되는가.
   (코드 없이 서술로 답한다. 정답 요지: 이진 탐색은 "맞다/아니다"가 아니라 "어느 쪽으로 갈까"라는
   **삼분 판정**이 필요하고, 그 판정이 배열의 정렬 기준과 같아야 하기 때문이다.)

### 10강

1. `sortByEndTime`은 `meetings[j].end > key.end`로 기준이 박혀 있다.
   `sortBy(Meeting[], Comparator<Meeting>)`로 일반화해 종료 시각 · 시작 시각 · 소요 시간
   **세 전략의 결과를 같은 코드로 비교**하기. 어휘: `Comparator.comparingInt`
2. 종료 시각이 같은 회의의 처리 규칙을 `thenComparing()`으로 명시하기.

### 13강

`Step4CategoryStats`의 `containsKey` → `put(0)` → `put(get + 1)` 3단 누적과
`categoryOrder` 보조 리스트가 대상이다.

1. 카테고리별 상품 수. 어휘: `collect(groupingBy(p -> p.category, counting()))`
2. 카테고리별 평균 가격. `averagingInt`가 금지 어휘이므로 `groupingBy`로 묶은 뒤 각 그룹에서
   `stream().mapToInt(p -> p.price).average().orElse(0)`으로 구한다 — 스트림 중첩 연습이 된다.
3. 재고 5개 미만 상품 목록과 개수. 어휘: `filter`, `toList()`, `count()`

> 원본의 "카테고리별 재고 자산"은 `long` 누적이라 `mapToLong`이 필요한데 이는 보충 자료에
> 없으므로 문제로 내지 않는다. 대신 해설에서 "여기는 스트림으로 바꾸지 않는다"고 밝힌다.

## 문제 카드 구조

각 문제는 네 부분이다.

1. 원본 코드 카드 — 강의 예제 파일에서 **그대로** 발췌하고 파일명을 밝힌다.
2. 요구사항 목록 — 무엇을 만족해야 하는지.
3. 힌트 — 보충 자료의 해당 절로 가는 링크(`../supplements/...#sec-...`).
4. `<details class="answer-box">`로 접은 정답 코드 카드 + 해설.

섹션 머리에는 경고 상자를 둔다: **문법이 바뀌어도 복잡도는 그대로다.** 알고리즘 과목이므로 이
구분이 흐려지면 안 된다.

## 파일 변경

### 1. 강의 HTML 6개

`sec-answer`(17번) 뒤, `sec-quiz` 앞에 `id="sec-modernize"` 섹션을 넣는다.

```
17 최종 문제 정답과 해설
18 람다·스트림 수정 문제   ← 신설
19 확인 퀴즈              (기존 18)
20 오늘의 핵심 정리        (기존 19)
21 다음 강의 연결          (기존 20)
```

`<span class="section-no">` 숫자 3개를 강의마다 다시 매긴다. 목차는 `data-toc-label`로 자동
생성되므로 손대지 않는다. 나머지 7개 강의는 20개 섹션 그대로다.

`validate.mjs`의 `REQUIRED_SECTIONS` 순서 검사는 필수 섹션들 사이의 상대 순서만 보므로 이
삽입에 영향받지 않는다.

### 2. Java 예제 6개

각 강의 폴더에 `ModernizeSolution.java`를 하나 추가한다. 그 강의 수정 문제의 정답을 전부 담고,
`main`에서 **원본 방식과 새 방식의 결과가 같음을 출력으로 증명**한다. `javac -encoding UTF-8`로
컴파일하고 실행해 검증한다.

### 3. 보충 자료 1개

`supplements/java-streams.html`의 최종 연산 표에 `min()`과 `max(Comparator)` 두 줄 추가.

### 4. 데이터 2개

`.java` 파일이 1개씩 늘었으므로 `assets/js/algorithms-data.js`와 `data/algorithms.json`의
`examples`를 함께 고친다 (두 파일은 완전히 동일해야 한다).

| 강의 | 이전 → 이후 |
|---|---|
| 2강 | 9 → 10 |
| 3강 | 9 → 10 |
| 4강 | 10 → 11 |
| 5강 | 11 → 12 |
| 10강 | 11 → 12 |
| 13강 | 9 → 10 |

### 5. `scripts/validate.mjs`

세 가지 검사를 추가한다.

1. **범위 고정** — 지정한 6개 강의에는 `id="sec-modernize"`와 `ModernizeSolution.java`가 있어야
   하고, 나머지 7개 강의에는 **없어야** 한다. 조용히 번지는 것을 막는다.
2. **예제 수 일치** — 강의의 `examples` 값과 실제 `.java` 파일 수가 같아야 한다. 지금은 보충
   자료에만 있는 검사인데, 이번 변경으로 숫자가 틀어지기 쉬워 강의에도 적용한다.
3. **어휘 잠금** — `sec-modernize` 섹션과 `ModernizeSolution.java`에 금지 어휘 목록의 API가
   나오면 실패시킨다. 나중에 문제를 고칠 때 보충 자료에 없는 API가 들어오는 것을 막는 장치다.

## 검증

- `npm run build` 통과 (위 세 검사 포함).
- 6개 `ModernizeSolution.java` 전부 `javac -encoding UTF-8` 컴파일 성공, 실행 시 "원본과 결과
  동일" 출력 확인.
- 6개 강의 페이지를 열어 섹션 번호가 1~21로 이어지는지, 목차에 "람다·스트림 수정 문제"가 뜨는지,
  정답 접기가 동작하는지 확인.

## 하지 않는 것

- 나머지 7개 강의에는 넣지 않는다.
- 보충 자료의 "강의 코드 바꿔 보기" 절은 그대로 둔다. 그쪽은 **보여 주는** 자리, 이쪽은
  **풀어 보는** 자리로 역할이 다르다. 문제는 그 절과 겹치지 않게 비틀어 낸다.
- 강의 예제의 기존 코드는 고치지 않는다. 원본이 남아 있어야 "이전 · 이후"가 성립한다.
