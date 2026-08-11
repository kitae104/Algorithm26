# 실제 데이터 응용 예제 — 실전 라이브러리화 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 2·3·4·13강의 15번 섹션(`sec-application`, "실제 데이터 응용 예제")에서 손으로 짠 알고리즘 호출을 실제 JDK 표준 라이브러리 호출로 교체하고, 대응하는 `.java` 예제와 강의 HTML을 함께 갱신한다.

**Architecture:** 각 강의는 독립적인 파일 쌍(`examples/java/NN-*/…Application.java` + `algorithms/NN-*.html`의 15번 섹션)을 가진다. 강의 4개를 각각 하나의 태스크로 다룬다. 새 파일을 추가하지 않으므로 `algorithms-data.js`/`data/algorithms.json`의 `examples` 카운트는 건드리지 않는다.

**Tech Stack:** Java 17 (표준 라이브러리만, 외부 의존성 없음), 정적 HTML/CSS/JS, `scripts/validate.mjs`(Node) 구조 검증.

## Global Constraints

- 대상은 오직 15번 섹션(`id="sec-application"`)이다. 7~11번(핵심 알고리즘 학습) 섹션은 절대 수정하지 않는다.
- `.java` 파일은 **같은 파일명으로 그 자리에서** 수정한다(새 파일 추가 금지) — `examples` 카운트 메타데이터를 바꾸지 않는다.
- HTML 코드 카드의 `<`, `>`, `&`는 반드시 `&lt;` `&gt;` `&amp;`로 이스케이프한다.
- 출력 카드(`id="out-app"`)는 반드시 **실제 컴파일·실행 결과와 정확히 일치**해야 한다 — 절대 손으로 추정해서 채우지 않는다.
- 4개 강의 작업이 모두 끝나면 `npm run build`(`scripts/validate.mjs`)를 실행해 통과를 확인한다.
- 1, 5, 6, 7, 8, 9, 10, 11, 12강의 15번 섹션은 수정하지 않는다.

---

### Task 1: 2강 — `BookLoanApplication.java` (returnBook → ArrayList.remove(Object))

**Files:**
- Modify: `examples/java/02-arrays-and-lists/BookLoanApplication.java`
- Modify: `algorithms/02-arrays-and-lists.html:1139-1277` (`sec-application` 섹션)

**Interfaces:** 없음 (독립 파일, main()만 실행)

- [ ] **Step 1: `returnBook` 메서드를 라이브러리 호출로 교체**

`examples/java/02-arrays-and-lists/BookLoanApplication.java`에서 아래 메서드를:

```java
    /** 반납 처리: record와 일치하는 기록 하나를 찾아 삭제한다 — O(n) */
    static boolean returnBook(ArrayList<String> loans, String record) {
        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i).equals(record)) {
                loans.remove(i);   // 삭제하면 뒤 원소가 한 칸씩 앞으로 밀려온다
                return true;       // 하나만 지우고 즉시 종료 (1강의 조기 중단!)
            }
        }
        return false;
    }
```

다음으로 교체한다:

```java
    /**
     * 반납 처리: record와 일치하는 기록 하나를 찾아 삭제한다.
     * ArrayList.remove(Object)가 내부에서 정확히 같은 일(선형탐색 + 첫 일치 삭제)을 한다 — O(n).
     */
    static boolean returnBook(ArrayList<String> loans, String record) {
        return loans.remove(record);
    }
```

- [ ] **Step 2: 컴파일·실행으로 검증**

```bash
cd examples/java/02-arrays-and-lists
javac -encoding UTF-8 *.java
java -Dfile.encoding=UTF-8 BookLoanApplication
```

기존 출력과 **동일한 결과**가 나와야 한다(알고리즘 동작이 같으므로). 출력을 저장해 둔다.

- [ ] **Step 3: HTML 코드 카드 갱신**

`algorithms/02-arrays-and-lists.html`의 `id="code-app"` 코드 블록에서 `returnBook` 부분을 Step 1과 동일한 내용으로 교체(HTML 이스케이프 유지).

`데이터 모델과 알고리즘 적용 기준` 목록의 세 번째 항목을:

```html
                    <li><strong>삭제:</strong> 1강의 조기 중단 — 일치하는 기록을 찾는 즉시 지우고 반환.
                        삭제 후 뒤 원소들이 밀려와 인덱스가 바뀐다는 것을 출력으로 직접 확인합니다.</li>
```

다음으로 교체:

```html
                    <li><strong>삭제:</strong> <code>ArrayList.remove(Object)</code> — "일치하는 기록을 찾아 즉시 삭제"라는
                        1강의 조기 중단 알고리즘을 라이브러리가 내부에서 그대로 수행합니다.
                        삭제 후 뒤 원소들이 밀려와 인덱스가 바뀐다는 것을 출력으로 직접 확인합니다.</li>
```

- [ ] **Step 4: 출력 카드 확인**

Step 2에서 얻은 실제 출력이 기존 `id="out-app"` 내용과 같은지 비교한다. 같다면 출력 카드는 그대로 둔다. 다르면 실제 출력으로 교체한다.

- [ ] **Step 5: 코드 해설과 복잡도 분석 갱신**

두 번째 li(`returnBook`은 조기 중단이 있으므로...)를 다음으로 교체:

```html
                    <li><code>printLoansOf</code>는 전체 순회 조건 검색이므로 항상 n번 검사 — O(n).
                        <code>returnBook</code>은 이제 <code>ArrayList.remove(Object)</code> 한 줄이지만,
                        내부 동작은 조기 중단 선형탐색으로 최선 1번, 최악 n번 — 여전히 O(n)입니다.
                        문법이 바뀌어도 복잡도는 그대로입니다 — 바뀐 것은 그 반복문을 누가 쓰느냐(개발자 vs 라이브러리)뿐입니다.</li>
```

- [ ] **Step 6: 커밋**

```bash
git add examples/java/02-arrays-and-lists/BookLoanApplication.java algorithms/02-arrays-and-lists.html
git commit -m "feat: 2강 응용 예제에서 반납 처리를 ArrayList.remove(Object) 호출로 교체"
```

---

### Task 2: 3강 — `NoticeAnalysisApplication.java` (topWords → Stream 정렬)

**Files:**
- Modify: `examples/java/03-brute-force-string-hash/NoticeAnalysisApplication.java`
- Modify: `algorithms/03-brute-force-string-hash.html:1138-1308` (`sec-application` 섹션)

**Interfaces:** 없음

- [ ] **Step 1: `topWords` 메서드를 스트림 기반으로 교체**

`examples/java/03-brute-force-string-hash/NoticeAnalysisApplication.java`에서:

```java
    /** 빈도 상위 topN 단어: "최댓값 찾기"(2강 패턴)를 topN번 반복한다. */
    static List<String> topWords(Map<String, Integer> freq, List<String> order, int topN) {
        List<String> result = new ArrayList<>();
        while (result.size() < topN && result.size() < order.size()) {
            String best = null;
            for (String word : order) {
                if (result.contains(word)) {
                    continue;                      // 이미 뽑은 단어는 건너뛴다
                }
                if (best == null || freq.get(word) > freq.get(best)) {
                    best = word;                   // 남은 단어 중 최다 빈도를 기록
                }
            }
            result.add(best);
        }
        return result;
    }
```

다음으로 교체:

```java
    /**
     * 빈도 상위 topN 단어: Map.Entry 스트림을 빈도 내림차순(동점이면 사전순)으로 정렬해
     * 앞에서 topN개를 취한다. 정렬 알고리즘의 원리는 4강에서 배우지만, 정렬된 결과 자체는
     * 지금도 라이브러리로 바로 얻을 수 있다.
     */
    static List<String> topWords(Map<String, Integer> freq, int topN) {
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();
    }
```

`main()`에서 호출부를 교체:

```java
        List<String> top3 = topWords(freq, order, 3);
```

다음으로:

```java
        List<String> top3 = topWords(freq, 3);
```

- [ ] **Step 2: 컴파일·실행으로 검증**

```bash
cd examples/java/03-brute-force-string-hash
javac -encoding UTF-8 *.java
java -Dfile.encoding=UTF-8 NoticeAnalysisApplication
```

표본 데이터의 상위 3개 단어 빈도(4/3/2회)가 모두 달라 동점이 없으므로, 출력은 기존과 **동일**해야 한다. 실제 출력을 저장해 둔다.

- [ ] **Step 3: HTML 코드 카드 갱신**

`id="code-app"` 블록에서 `topWords` 메서드와 `topWords(freq, order, 3)` 호출부를 Step 1과 동일하게 교체(HTML 이스케이프 유지, `Map.Entry.&lt;String, Integer&gt;comparingByValue()` 형태로 제네릭 이스케이프 주의).

`데이터 모델과 알고리즘 적용 기준` 목록의 세 번째 항목을:

```html
                    <li><strong>상위 3개:</strong> 2강의 "최댓값 찾기"를 3번 반복 — 이미 뽑은 단어는 건너뜁니다</li>
```

다음으로 교체:

```html
                    <li><strong>상위 3개:</strong> <code>entrySet().stream().sorted(...).limit(3)</code> —
                        Map.Entry를 빈도 내림차순(동점 시 사전순)으로 정렬해 앞에서 3개를 취합니다</li>
```

- [ ] **Step 4: 출력 카드 확인**

Step 2 실제 출력을 기존 `id="out-app"`과 비교해 동일하면 그대로, 다르면 교체한다.

- [ ] **Step 5: 코드 해설과 복잡도 분석 갱신**

세 번째 li를 다음으로 교체:

```html
                    <li><strong>상위 3개:</strong> 이제 반복 탐색 대신 <code>entrySet().stream().sorted(...).limit(3)</code>로
                        한 번에 구합니다 — 정렬 기반이라 단어 종류가 m개면 O(m log m)입니다.
                        3강 시점에는 정렬을 아직 배우지 않았지만, "정렬된 결과"는 라이브러리로 바로 쓸 수 있습니다.
                        상위 k개만 필요하고 m이 아주 크면 PriorityQueue로 O(m log k)까지 줄일 수도 있습니다.</li>
```

- [ ] **Step 6: 커밋**

```bash
git add examples/java/03-brute-force-string-hash/NoticeAnalysisApplication.java algorithms/03-brute-force-string-hash.html
git commit -m "feat: 3강 응용 예제에서 상위 단어 추출을 Stream 정렬로 교체"
```

---

### Task 3: 4강 — `ProductSortApplication.java` (insertionSort → Arrays.sort)

**Files:**
- Modify: `examples/java/04-sorting-algorithms/ProductSortApplication.java`
- Modify: `algorithms/04-sorting-algorithms.html:1178-1344` (`sec-application` 섹션)

**Interfaces:** 없음

- [ ] **Step 1: `insertionSort` 제거, 세 호출부를 `Arrays.sort`로 교체**

`examples/java/04-sorting-algorithms/ProductSortApplication.java`에서 다음 메서드를 **삭제**한다:

```java
    /**
     * 제네릭 삽입 정렬: 어떤 타입이든 Comparator만 갈아 끼우면 정렬할 수 있다.
     * comp.compare(a, b)가 양수이면 "a가 b보다 뒤에 와야 한다"는 뜻이다.
     */
    static <T> void insertionSort(T[] arr, Comparator<? super T> comp) {
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && comp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j]; // key보다 뒤에 와야 할 값을 한 칸 민다
                j--;
            }
            arr[j + 1] = key;
        }
    }
```

`main()`의 세 호출부를 교체한다:

```java
        insertionSort(byPrice, Comparator.naturalOrder());
```
→
```java
        Arrays.sort(byPrice, Comparator.naturalOrder());
```

```java
        insertionSort(byRating, byRatingDesc);
```
→
```java
        Arrays.sort(byRating, byRatingDesc);
```

```java
        insertionSort(byName, Comparator.comparing(p -> p.name));
```
→
```java
        Arrays.sort(byName, Comparator.comparing(p -> p.name));
```

마지막 안내 문구를 교체한다:

```java
        System.out.println("정렬 코드는 한 줄도 바꾸지 않았습니다. 바뀐 것은 Comparator뿐입니다.");
```
→
```java
        System.out.println("Arrays.sort 호출은 세 번 다 똑같습니다. 바뀐 것은 Comparator뿐입니다.");
```

(`java.util.Arrays`, `java.util.Comparator` import는 이미 있으므로 그대로 둔다.)

- [ ] **Step 2: 컴파일·실행으로 검증**

```bash
cd examples/java/04-sorting-algorithms
javac -encoding UTF-8 *.java
java -Dfile.encoding=UTF-8 ProductSortApplication
```

`Arrays.sort(Object[], Comparator)`도 안정 정렬(TimSort)이므로 정렬 결과 순서는 기존과 동일해야 한다. 마지막 줄 문구만 바뀐다. 실제 출력을 저장한다.

- [ ] **Step 3: HTML 코드 카드 갱신**

`id="code-app"` 블록에서 `insertionSort` 메서드 삭제, 세 호출부 교체, 마지막 안내 문구 교체를 Step 1과 동일하게 반영(HTML 이스케이프 유지).

`문제 상황` 문단 끝에 문장 하나를 추가한다. 기존:

```html
                    <em><strong>정렬 코드는 하나만 두고, 비교 기준(Comparator)만 갈아 끼우는 구조</strong>를 만듭니다.</em>
```

다음으로 교체:

```html
                    <em><strong>정렬 코드는 하나만 두고, 비교 기준(Comparator)만 갈아 끼우는 구조</strong>를 만듭니다.
                    이번에는 그 정렬 코드조차 직접 만들지 않고, JDK가 제공하는 <code>Arrays.sort()</code>를 그대로 씁니다.</em>
```

`데이터 모델과 알고리즘 적용 기준` 목록의 네 번째 항목을 교체:

```html
                    <li><strong>정렬 알고리즘:</strong> 15번 실습까지 다룬 삽입 정렬을 <strong>제네릭 + Comparator 버전</strong>으로 일반화 —
                        <strong>안정 정렬</strong>이므로 동점 상품의 입력 순서가 유지됩니다</li>
```

다음으로:

```html
                    <li><strong>정렬 알고리즘:</strong> 9~11번 섹션에서 직접 구현한 삽입 정렬 대신,
                        JDK 표준 라이브러리의 <code>Arrays.sort(T[], Comparator)</code>를 사용 —
                        내부의 <strong>TimSort는 안정 정렬</strong>이므로 동점 상품의 입력 순서가 그대로 유지됩니다</li>
```

- [ ] **Step 4: 출력 카드 갱신**

Step 2의 실제 출력으로 `id="out-app"` 내용을 교체한다(마지막 줄 문구가 바뀐다).

- [ ] **Step 5: 코드 해설과 복잡도 분석 갱신**

전체 `<ul>` 블록을 다음으로 교체:

```html
                <ul>
                    <li>세 번의 정렬 모두 <code>Arrays.sort(배열, Comparator)</code> 호출 한 줄입니다.
                        9~11번 섹션에서 직접 구현한 삽입 정렬과 원리(비교하며 자리 찾기)는 같지만,
                        이번에는 그 구현 자체를 JDK가 대신합니다.</li>
                    <li>기준 2는 <code>Comparator.comparingDouble(...).reversed().thenComparing(...)</code> —
                        Java가 제공하는 <strong>기준 조립 도구</strong>입니다. "평점으로 비교 → 뒤집기 → 동점이면 이름으로"를 그대로 읽을 수 있습니다.</li>
                    <li>출력에서 평점 4.6 동점인 "무선 이어폰"과 "보조 배터리"가 <strong>이름 가나다순(무 → 보)</strong>으로,
                        4.3 동점인 "블루투스 스피커"와 "스마트폰 거치대"도 이름순으로 정렬되었습니다 — 다중 기준이 동작한 증거입니다.</li>
                    <li><strong>복잡도:</strong> 직접 구현한 삽입 정렬은 비교 기준과 무관하게 평균·최악 O(n²)였지만,
                        <code>Arrays.sort(Object[], Comparator)</code>는 <strong>TimSort</strong>를 사용해 평균·최악 O(n log n),
                        공간 O(n)(병합용 보조 배열)입니다. 상품이 수백 개를 넘어 수만·수십만 개가 되면 이 차이가
                        체감됩니다 — 실전에서 정렬을 직접 구현하지 않고 라이브러리를 쓰는 이유입니다.</li>
                </ul>
```

- [ ] **Step 6: 커밋**

```bash
git add examples/java/04-sorting-algorithms/ProductSortApplication.java algorithms/04-sorting-algorithms.html
git commit -m "feat: 4강 응용 예제에서 삽입 정렬 직접 구현을 Arrays.sort로 교체"
```

---

### Task 4: 13강 — `ProductManagerApplication.java` (insertionSort/binarySearchById → List.sort/Collections.binarySearch)

**Files:**
- Modify: `examples/java/13-algorithm-project/ProductManagerApplication.java`
- Modify: `algorithms/13-algorithm-project.html:1558-1767` (`sec-application` 섹션)

**Interfaces:** 없음

- [ ] **Step 1: import 추가, 두 메서드 제거, main() 갱신**

`examples/java/13-algorithm-project/ProductManagerApplication.java` 상단 import에 추가:

```java
import java.util.Collections;
```

(기존 `ArrayList, Comparator, HashMap, List, Map` import는 그대로 둔다.)

다음 두 메서드를 **삭제**한다:

```java
    /** 삽입 정렬(4강) — 상품 관리와 같은 알고리즘, 타입만 Book */
    static void insertionSort(List<Book> list, Comparator<Book> comparator) {
        for (int i = 1; i < list.size(); i++) {
            Book key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    /** 이진 탐색(5강) — 등록 번호 오름차순 정렬이 전제 조건 */
    static Book binarySearchById(List<Book> sorted, int targetId) {
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int midId = sorted.get(mid).id;
            if (midId == targetId) {
                return sorted.get(mid);
            }
            if (midId < targetId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }
```

`main()`을 다음으로 교체한다:

```java
    public static void main(String[] args) {
        List<Book> books = loadBooks();
        Comparator<Book> byId = Comparator.comparingInt(b -> b.id);

        // 결정 1: 검색이 잦다 → 등록 번호 순 정렬 + 이진 탐색 (이번엔 라이브러리로)
        books.sort(byId);
        System.out.println("== 등록 번호 순 정렬 ==");
        for (Book b : books) {
            System.out.println("  " + b.summary());
        }

        System.out.println();
        System.out.println("== 도서 검색 (이진 탐색 + 예외 처리) ==");
        String[] requests = {"2205", "9999", "일구팔사"};
        for (String request : requests) {
            System.out.println("검색 요청 \"" + request + "\"");
            try {
                int id = Integer.parseInt(request.trim());
                Book probe = new Book(id, "", "", 0);   // 비교에만 쓰는 탐색용 키 객체
                int index = Collections.binarySearch(books, probe, byId);
                if (index >= 0) {
                    System.out.println("  → " + books.get(index).summary());
                } else {
                    System.out.println("  → 번호 " + id + " 도서 없음");
                }
            } catch (NumberFormatException e) {
                System.out.println("  → 오류: \"" + request + "\"은(는) 숫자가 아닙니다.");
            }
        }

        // 결정 2: 장르별 통계가 필요하다 → HashMap 집계 (3강)
        System.out.println();
        System.out.println("== 장르별 보유 현황 ==");
        Map<String, Integer> countByGenre = new HashMap<>();
        List<String> genreOrder = new ArrayList<>();
        for (Book b : books) {
            if (!countByGenre.containsKey(b.genre)) {
                genreOrder.add(b.genre);
                countByGenre.put(b.genre, 0);
            }
            countByGenre.put(b.genre, countByGenre.get(b.genre) + 1);
        }
        for (String genre : genreOrder) {
            System.out.println("  " + genre + " : " + countByGenre.get(genre) + "종");
        }

        // 결정 3: 대출 가능 권수가 부족한 책 → 조건 검색 (2강)
        System.out.println();
        System.out.println("== 대출 불가 임박 (1권 이하) ==");
        for (Book b : books) {
            if (b.copies <= 1) {
                System.out.println("  " + b.summary());
            }
        }

        System.out.println();
        System.out.println("도메인이 상품 → 도서로 바뀌어도, 요구사항 패턴이 같으면 같은 설계를 재사용할 수 있다.");
    }
```

- [ ] **Step 2: 컴파일·실행으로 검증**

```bash
cd examples/java/13-algorithm-project
javac -encoding UTF-8 *.java
java -Dfile.encoding=UTF-8 ProductManagerApplication
```

`List.sort`(TimSort, 안정 정렬)와 손으로 짠 삽입 정렬은 이 6권 데이터에서 같은 순서를 만든다. `Collections.binarySearch`도 같은 이진 탐색 알고리즘이므로 검색 결과가 동일해야 한다. 실제 출력을 저장한다.

- [ ] **Step 3: HTML 코드 카드 갱신**

`id="code-app"` 블록 전체를 Step 1의 최종 파일 내용으로 교체(HTML 이스케이프 유지, import 목록에 `java.util.Collections` 추가 반영).

클래스 상단 문서 주석 마지막 문장을 교체:

```html
 * 바뀐 것은 데이터 클래스(Product → Book)뿐이다.
 */
```
→
```html
 * 바뀐 것은 데이터 클래스(Product → Book)뿐이다.
 * 이번에는 정렬·탐색도 직접 구현 대신 JDK 표준 라이브러리(List.sort, Collections.binarySearch)를 그대로 쓴다.
 */
```

`데이터 모델과 알고리즘 적용 기준` 목록의 두 번째 항목을 교체:

```html
                    <li><strong>검색:</strong> 등록 번호 순 삽입 정렬(4강) 후 이진 탐색(5강) — 상품 관리와 같은 결정</li>
```

다음으로:

```html
                    <li><strong>검색:</strong> 등록 번호 순 정렬 후 이진 탐색 — 결정은 상품 관리와 같지만,
                        이번엔 4강·5강에서 손으로 만든 코드 대신 <code>List.sort(Comparator)</code> ·
                        <code>Collections.binarySearch(List, key, Comparator)</code>를 그대로 사용</li>
```

- [ ] **Step 4: 출력 카드 갱신**

Step 2의 실제 출력으로 `id="out-app"` 내용을 확인·필요 시 교체한다.

- [ ] **Step 5: 코드 해설과 복잡도 분석 갱신**

세 번째 li(복잡도)를 교체:

```html
                    <li>복잡도는 상품 관리와 동일: 정렬 O(n²) 1번 + 검색 O(log n)/건 + 집계 O(n).</li>
```

다음으로:

```html
                    <li>복잡도: <code>List.sort(Comparator)</code>는 TimSort로 O(n log n) 1번
                        (4강에서 직접 구현한 삽입 정렬의 O(n²)보다 데이터가 많아질수록 유리),
                        검색은 <code>Collections.binarySearch</code>로 여전히 O(log n)/건, 집계는 O(n)입니다.</li>
```

세 번째 li 뒤(핵심 교훈 li 앞)에 새 li를 추가:

```html
                    <li><strong>라이브러리 사용법:</strong> <code>Collections.binarySearch</code>는 비교에 쓸
                        "탐색용 키 객체"가 필요합니다. 그래서 id만 채운 임시 <code>Book</code>(<code>probe</code>)을
                        만들어 넘깁니다 — 실무에서도 자주 쓰는 패턴입니다.</li>
```

- [ ] **Step 6: 커밋**

```bash
git add examples/java/13-algorithm-project/ProductManagerApplication.java algorithms/13-algorithm-project.html
git commit -m "feat: 13강 응용 예제에서 정렬·탐색 직접 구현을 List.sort/Collections.binarySearch로 교체"
```

---

### Task 5: 전체 검증 및 배포

**Files:** 없음 (검증 전용)

- [ ] **Step 1: 4개 강의 Java 예제 전부 재검증**

```bash
for d in 02-arrays-and-lists 03-brute-force-string-hash 04-sorting-algorithms 13-algorithm-project; do
  echo "== $d =="
  (cd examples/java/$d && javac -encoding UTF-8 *.java && for f in *.java; do
      cls=$(basename "$f" .java)
      if grep -q "public static void main" "$f"; then java -Dfile.encoding=UTF-8 "$cls" > /dev/null && echo "$cls OK"; fi
  done)
done
```

폴더 안의 **다른** 예제 파일(Step/Complete/Trace/Starter/Solution/ModernizeSolution 등)도 같은 폴더에서 함께 컴파일되므로 전부 깨지지 않는지 확인한다.

- [ ] **Step 2: 구조 검증**

```bash
npm run build
```

`scripts/validate.mjs`가 13개 강좌 데이터 정합성, 20개 섹션, 내부 링크, 코드 복사 대상 id, id 중복, Java 예제 폴더 존재를 검사한다. 실패하면 원인을 고치고 다시 실행한다.

- [ ] **Step 3: 변경 요약 확인**

```bash
git log --oneline -6
git status
```

Task 1~4의 커밋 4개가 보이고 워킹 트리가 깨끗한지 확인한다.

- [ ] **Step 4: GitHub로 배포**

Step 1~3에 문제가 없으면(컴파일·실행·`npm run build` 모두 통과) 현재 브랜치를 원격으로 푸시한다:

```bash
git push origin main
```

이 저장소는 Vercel Git 연동이 되어 있으므로(README 참고) 푸시하면 자동 배포된다. 사용자에게 푸시 완료와 커밋 목록을 보고한다.
