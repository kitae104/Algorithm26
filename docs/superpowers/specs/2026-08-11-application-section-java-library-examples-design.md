# 실제 데이터 응용 예제 — 실전 자바 라이브러리 버전 설계

작성일: 2026-08-11

## 배경

13개 강의 모두 15번 섹션(`id="sec-application"`, "실제 데이터 응용 예제")에서 그 강의가
가르친 알고리즘을 손으로 짠 코드로 실제와 비슷한 데이터에 적용해 보여준다. 그런데 실무에서는
이 알고리즘들 상당수를 직접 구현하지 않고 JDK 표준 라이브러리 호출로 해결한다. 학생이
"원리는 배웠지만 실전에서는 이렇게 쓴다"는 감각을 갖도록, 응용 섹션을 표준 라이브러리
버전으로 바꾸고 싶다.

프로젝트 자체 규칙(`docs/adding-lessons.md` 6장)이 이미 이를 허용한다:
"핵심 알고리즘을 `Arrays.sort()` 등 라이브러리 호출로 대체 금지 (**비교용으로 분리 제공은
허용**)". 15번 섹션은 정확히 이 "분리 제공" 자리이고, 7~11번(핵심 구현) 섹션은 손대지 않는다.

## 원칙

- 문제를 그대로 풀어내는 **진짜 JDK 표준 라이브러리 API**가 있으면, 응용 섹션의 손짜 로직을
  그 API 호출로 **교체**한다. 문제 상황·데이터 모델은 최대한 유지하고, 알고리즘 실행부만
  바꾼다.
- 대체할 표준 라이브러리가 없거나, 그 섹션의 학습 목표 자체가 라이브러리로는 보여줄 수 없는
  것(예: 연산 횟수 직접 측정)이면 **그대로 둔다**.
- 핵심 강의 섹션(7~11번: 단계별 동작·의사코드·점진적 구현·완성 코드·추적)은 이 작업의 대상이
  아니며 전혀 수정하지 않는다.
- 해설(코드 해설과 복잡도 분석)에 "직접 구현 vs 실전 라이브러리"를 명시적으로 대비하고,
  복잡도가 실제로 달라지는 경우(예: O(n²) → O(n log n))는 그 사실을 밝힌다.

## 대상 강의 조사 결과

13개 강의의 15번 섹션과 그 배경 Java 파일(`examples/java/NN-*/*Application.java`)을 모두
검토했다.

| 강의 | 처리 | 근거 |
|---|---|---|
| 1강 알고리즘 기초 | 유지 | `StudentLookupApplication`은 비교 횟수를 직접 세어 O(n) vs O(1)을 보여주는 것이 학습 목표. 라이브러리 호출(`List.indexOf` 등)은 내부 비교 횟수를 셀 수 없어 이 목표를 가린다. |
| 2강 배열과 리스트 | **교체** | `BookLoanApplication.returnBook`이 `equals` 선형탐색 + `remove(i)`를 손으로 구현 — 이는 정확히 `ArrayList.remove(Object)`가 하는 일. |
| 3강 문자열·해시 | **교체** | `NoticeAnalysisApplication.topWords`가 "최댓값 찾기"를 topN번 반복(O(topN·n)) — `Map.Entry` 스트림 정렬(`entrySet().stream().sorted(...).limit(topN)`)로 대체 가능. |
| 4강 정렬 | **교체** | `ProductSortApplication`이 손으로 짠 제네릭 삽입 정렬(`insertionSort<T>`)을 사용 — `Arrays.sort(T[], Comparator)`로 대체. TimSort 사용으로 O(n²) → O(n log n) 복잡도 개선도 해설에 명시. |
| 5강 탐색 | 유지 (사용자 확정) | `BookSearchApplication`은 이진탐색의 비교 횟수(`compareCount`)를 직접 측정해 "10만 건에서도 20번 이내"를 보여주는 것이 핵심. `Arrays.binarySearch`로 바꾸면 비교 횟수를 셀 수 없어 이 핵심 장치가 사라짐. 1강과 같은 이유로 제외. |
| 6강 스택·큐 | 유지 | `BankQueueApplication`이 이미 `ArrayDeque`(FIFO 큐)와 `PriorityQueue`(VIP 우선순위)를 실제로 사용 중 — 이미 준수 상태. |
| 7강 재귀·백트래킹 | 유지 | JDK 표준 라이브러리에 순열 생성/제약 기반 백트래킹 유틸리티가 없음(`Collections2.permutations`는 Guava, JDK 아님). |
| 8강 트리 구조 | 유지 | `OrgChartApplication`은 일반 N진 트리(조직도, 부모-자식 임의 구조)를 다룸. `TreeMap`/`TreeSet`은 정렬된 key-value 구조라 이 계층 구조에 맞지 않음. |
| 9강 그래프 탐색 | 유지 | JDK 표준 라이브러리에 그래프 BFS/DFS 유틸리티가 없음(JGraphT 등은 서드파티). |
| 10강 그리디 | 유지 | `ShortestJobApplication`이 이미 `PriorityQueue`+`Comparator`로 실제 그리디를 구현 중 — 이미 준수 상태. `GreedyFailApplication`은 그리디 실패를 보여주는 의도된 반례라 대체 대상이 아님. |
| 11강 동적 계획법 | 유지 | JDK 표준 라이브러리에 DP/메모이제이션 프레임워크가 없음. |
| 12강 최단 경로 | 유지 | `ShortestPathApplication`이 이미 최소값 추출에 `PriorityQueue`를 사용 중이나, 다익스트라 전체를 대체할 JDK 표준 라이브러리는 없음(JGraphT 등은 서드파티). |
| 13강 종합 프로젝트 | **교체** | `ProductManagerApplication`이 4강·5강에서 손으로 짠 삽입 정렬·이진 탐색을 재사용 — `List.sort(Comparator)` / `Collections.binarySearch`로 대체. |

**교체 대상 4개 강의**: 2, 3, 4, 13강. **유지 9개 강의**: 1, 5, 6, 7, 8, 9, 10, 11, 12강.

## 강의별 구체 변경 (교체 대상 4개)

### 2강 — `BookLoanApplication.java`

- `returnBook(ArrayList<String> loans, String record)`의 선형탐색+`remove(i)` 반복문을
  `loans.remove(record)` 한 줄로 교체(`ArrayList.remove(Object)`가 반환하는 `boolean`을 그대로
  사용).
- `printLoansOf`(조건 검색+출력)는 인덱스 번호를 함께 출력하는 표시 로직이라 유지한다 —
  탐색 알고리즘 자체가 아니라 출력 형식이므로 대체 대상이 아님.
- 해설에 "복잡도는 그대로 O(n)이다 — 바뀐 것은 그 선형탐색을 누가 하느냐(개발자 코드 vs
  라이브러리 내부)뿐"이라는 점을 명시(문법이 바뀌어도 복잡도는 그대로라는 이 저장소의 기존
  원칙과 일치).

### 3강 — `NoticeAnalysisApplication.java`

- `topWords(Map<String,Integer> freq, List<String> order, int topN)`의 반복 최댓값 탐색을
  `freq.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
  .limit(topN).map(Map.Entry::getKey).toList()`로 교체.
- 표본 데이터(공지사항 문장)에서 상위 3개 단어의 빈도가 모두 다르므로(4/3/2회) 동점 처리
  방식 차이가 출력에 영향을 주지 않음을 확인했다 — 다만 결정성을 위해
  `.thenComparing(Map.Entry::getKey)`로 동점 시 사전순 규칙을 명시적으로 추가한다.
- `countFrequencies`(HashMap 누적)와 `isPalindrome`은 이미 각각 실제 해싱과 순수 알고리즘
  로직이라 유지한다.
- 해설에 "정렬 기반이라 O(n log n)"이라는 복잡도와, 참고로 "상위 K개만 필요하면
  `PriorityQueue`로 O(n log k)까지 줄일 수 있다"는 실무 팁을 한 줄 덧붙인다.

### 4강 — `ProductSortApplication.java`

- 손으로 짠 제네릭 `insertionSort(T[] arr, Comparator<? super T> comp)` 메서드를 삭제하고,
  세 번의 정렬 호출(`byPrice`/`byRating`/`byName`)을 각각
  `Arrays.sort(byPrice, Comparator.naturalOrder())` /
  `Arrays.sort(byRating, byRatingDesc)` / `Arrays.sort(byName, ...)`로 교체.
- `Product`(Comparable 구현)와 `Comparator` 조립 코드(`comparingDouble().reversed()
  .thenComparing(...)`)는 그대로 유지 — 이번 교체의 핵심은 "정렬 알고리즘 자체"이지 비교
  기준 설계가 아니다.
- 해설에 "직접 구현한 삽입 정렬은 O(n²)이지만 JDK의 `Arrays.sort(Object[], Comparator)`는
  TimSort를 사용해 O(n log n)이다 — 실전에서 라이브러리를 쓰는 이유 중 하나가 바로 이
  복잡도 차이"라고 명시한다.

### 13강 — `ProductManagerApplication.java`

- 재사용해 온 `insertionSort(List<Book> list, Comparator<Book> comparator)`를 삭제하고
  `books.sort(comparator)`(`List.sort`)로 교체.
- `binarySearchById(List<Book> sorted, int targetId)`를 `Collections.binarySearch(sorted,
  probe, comparator)` 호출로 교체한다. `Collections.binarySearch`는 검색 대상과 같은 타입의
  "탐색용 키 객체"가 필요하므로, `id`만 채운 `Book` probe 객체(`new Book(id, "", "", 0)`)를
  만들어 비교에 사용한다 — 실무에서 자주 쓰이는 패턴임을 해설에 짧게 설명한다.
- 해설에 "정렬은 O(n log n)(TimSort), 탐색은 여전히 O(log n)"이라고 명시하고, 4강·5강에서
  손으로 배운 원리가 바로 이 라이브러리 호출 뒤에 있다는 점을 강조한다.

## 구현 방식

- 각 `*Application.java` 파일을 **같은 파일명으로 그 자리에서** 수정한다(새 파일 추가 아님).
  파일 개수가 바뀌지 않으므로 `assets/js/algorithms-data.js` / `data/algorithms.json`의
  `examples` 카운트는 수정하지 않는다.
- 대응하는 `algorithms/NN-*.html`의 15번 섹션(`sec-application`) 안 코드 카드·출력 카드를
  갱신하고, "코드 해설과 복잡도 분석" 문단에 위에서 정리한 대비·복잡도 설명을 반영한다.
- 문제 상황·데이터 모델(`<h3>` 하위 설명, 표본 데이터)은 가능한 한 그대로 유지해 강의 흐름을
  보존한다.
- 각 수정 파일은 `javac -encoding UTF-8 *.java` 컴파일 후 `java -Dfile.encoding=UTF-8
  클래스명`으로 실행해 검증하고, **실제 출력**을 그대로 HTML 출력 카드에 반영한다(이 저장소의
  기존 규칙 — 검증된 실행 결과와 HTML이 항상 일치해야 함).
- 4개 강의 작업이 끝나면 `npm run build`(`scripts/validate.mjs`)로 전체 구조를 검증한다.

## 하지 않는 것

- 7~11번(핵심 알고리즘 학습) 섹션은 전혀 건드리지 않는다.
- 1, 5, 6, 7, 8, 9, 10, 11, 12강의 15번 섹션은 수정하지 않는다.
- `examples`/`ALGORITHMS` 메타데이터, 새 `.java` 파일 추가, 섹션 개수·순서 변경은 하지 않는다
  (교체 대상 강의도 여전히 20개 섹션 구조를 유지).
- 기존 `sec-modernize`(람다·스트림 수정 문제) 섹션과는 무관 — 이번 작업은 15번 섹션만
  대상으로 한다.

## 검증 계획

- 4개 수정 `.java` 파일 전부 컴파일·실행 성공, HTML 출력과 실제 실행 결과 일치.
- `npm run build` 통과.
- 변경 후 커밋하고, 문제 없으면 사용자 요청대로 GitHub(Vercel 자동 배포 연동)로 푸시.
