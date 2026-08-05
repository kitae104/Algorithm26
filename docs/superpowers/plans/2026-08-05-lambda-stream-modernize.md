# 람다·스트림 「수정 문제」 연습 코너 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 6개 강의 페이지에 그 강의의 실제 예제 코드를 람다·스트림으로 고쳐 보는 「수정 문제」 섹션을 추가하고, 정답을 실행 가능한 Java 파일로 남긴다.

**Architecture:** 강의 HTML에 `sec-modernize` 섹션을 17번(정답과 해설)과 18번(퀴즈) 사이에 삽입하고 뒤 섹션 번호 3개를 밀어 준다. 정답은 강의 폴더의 `ModernizeSolution.java` 하나에 모으고, `main`에서 원본(반복문) 방식과 새(람다·스트림) 방식의 결과가 같음을 출력으로 증명한다. `scripts/validate.mjs`에 범위·개수·어휘 검사를 추가해 규칙이 앞으로도 지켜지게 한다.

**Tech Stack:** 정적 HTML + CSS + Vanilla JS(ES5), Java 17+, Node 18+ 검증 스크립트(`npm run build`).

**Spec:** `docs/superpowers/specs/2026-08-05-lambda-stream-modernize-design.md`

## Global Constraints

- **어휘 잠금** — 수정 문제의 정답 코드에 쓰는 모든 API는 `supplements/lambda-expressions.html` 또는 `supplements/java-streams.html`이 이미 가르친 것이어야 한다. 금지 어휘: `Map.merge`, `computeIfAbsent`, `Collectors.averagingInt`, `Collectors.summingInt`, `Collectors.summarizingInt`, `IntStream.of`, `flatMap`, `takeWhile`, `dropWhile`, `parallelStream`, `Comparator.naturalOrder`, `mapToLong`.
- **대상 강의는 정확히 6개** — `02-arrays-and-lists`, `03-brute-force-string-hash`, `04-sorting-algorithms`, `05-search-algorithms`, `10-greedy-algorithms`, `13-algorithm-project`. 나머지 7개 강의는 손대지 않는다.
- **기존 예제 코드는 고치지 않는다.** 원본이 남아 있어야 "이전 · 이후"가 성립한다.
- **원본 발췌는 실제 파일에서** 가져온다. 보충 자료의 "이전" 코드는 각색본이므로 베끼지 않는다.
- HTML 코드 블록 안에서 `<`, `>`, `&`는 각각 `&lt;`, `&gt;`, `&amp;`로 이스케이프한다.
- 모든 `code-card`의 `data-copy-target`은 그 페이지 안에서 유일한 id를 가리켜야 한다. 새 id는 `code-mod-*` 접두사를 쓴다.
- `assets/js/algorithms-data.js`와 `data/algorithms.json`은 **완전히 동일**해야 한다(`validate.mjs`가 문자열 비교).
- Java 파일은 `javac -encoding UTF-8`로 컴파일된다. 클래스명은 파일명과 같아야 한다.
- 커밋 메시지는 한국어로, 기존 이력(`feat:`/`fix:` 접두사 혼용)을 따른다.

---

### Task 1: 보충 자료 표의 빈 항목 메우기

수정 문제 정답이 쓰는 `min()`과 `max(Comparator)`가 스트림 문서 "최종 연산" 표에 없다. 표만 보고 푸는 학생이 막히므로 먼저 메운다.

**Files:**
- Modify: `supplements/java-streams.html` (최종 연산 표, 약 285-294줄)

**Interfaces:**
- Consumes: 없음
- Produces: 이후 모든 강의 Task가 `min()`과 `max(Comparator)`를 정답에 쓸 수 있게 된다.

- [ ] **Step 1: 현재 표 확인**

Run: `grep -n "findFirst()" supplements/java-streams.html`
Expected: `<tr><td><code>findFirst()</code></td>...` 한 줄이 보인다.

- [ ] **Step 2: 표에 두 줄 추가**

`<tr><td><code>sum()</code> · <code>average()</code> · <code>max()</code></td>...` 줄을 아래처럼 바꾼다.

```html
<tr><td><code>sum()</code> · <code>average()</code> · <code>max()</code> · <code>min()</code></td><td>숫자 / <code>Optional</code></td><td>2강 합계·평균·최댓값·최솟값</td></tr>
<tr><td><code>max(Comparator)</code> · <code>min(Comparator)</code></td><td><code>Optional&lt;T&gt;</code></td><td>객체 스트림에서 기준을 주고 고를 때 (3강 최빈 단어)</td></tr>
```

- [ ] **Step 3: 검증**

Run: `npm run build`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 4: 커밋**

```bash
git add supplements/java-streams.html
git commit -m "docs: 스트림 최종 연산 표에 min과 max(Comparator) 추가"
```

---

### Task 2: 2강 — 집계 반복문을 스트림으로

**Files:**
- Create: `examples/java/02-arrays-and-lists/ModernizeSolution.java`
- Modify: `algorithms/02-arrays-and-lists.html` (1688줄 `</section>` 뒤에 삽입, 이후 section-no 18→19, 19→20, 20→21)
- Modify: `assets/js/algorithms-data.js` (2강 `examples: 9` → `10`)
- Modify: `data/algorithms.json` (동일)

**Interfaces:**
- Consumes: Task 1이 추가한 `min()` 표 항목
- Produces: `sec-modernize` 섹션 구조와 `code-mod-*` id 규칙 — Task 3~7이 그대로 따른다.

- [ ] **Step 1: 원본 코드 확인**

Run: `grep -n "static int sum\|static int max\|static int min\|countAtLeast\|collectBelow" examples/java/02-arrays-and-lists/ScoreStatsComplete.java`
Expected: 5개 메서드가 각각 독립된 for 순회를 돈다.

- [ ] **Step 2: 정답 Java 파일 작성**

Create `examples/java/02-arrays-and-lists/ModernizeSolution.java`:

```java
import java.util.Arrays;
import java.util.IntSummaryStatistics;

/**
 * 2강 「람다·스트림 수정 문제」 정답.
 *
 * ScoreStatsComplete.java의 반복문 집계를 스트림으로 다시 쓴 것이다.
 * 반복문 버전을 그대로 남겨 두고, 두 결과가 같은지 실행 결과로 확인한다.
 * 복잡도는 어느 쪽도 O(n)이다 — 문법이 바뀐 것이지 알고리즘이 바뀐 것이 아니다.
 */
public class ModernizeSolution {

    /* ─────────── 이전: ScoreStatsComplete와 같은 반복문 코드 (비교 기준) ─────────── */

    static int sumLoop(int[] data) {
        int total = 0;
        for (int i = 0; i < data.length; i++) {
            total = total + data[i];
        }
        return total;
    }

    static double averageLoop(int[] data) {
        return (double) sumLoop(data) / data.length;
    }

    static int maxLoop(int[] data) {
        int candidate = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] > candidate) {
                candidate = data[i];
            }
        }
        return candidate;
    }

    static int minLoop(int[] data) {
        int candidate = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] < candidate) {
                candidate = data[i];
            }
        }
        return candidate;
    }

    static int countAtLeastLoop(int[] data, int threshold) {
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] >= threshold) {
                count++;
            }
        }
        return count;
    }

    /** 배열은 크기가 고정이라 (1) 개수를 세고 (2) 크기를 정해 (3) 채운다 — 두 번 순회한다. */
    static int[] collectBelowLoop(int[] data, double limit) {
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < limit) count++;
        }
        int[] result = new int[count];
        int pos = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < limit) {
                result[pos] = data[i];
                pos++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        /* ─────────── 문제 ① 집계 네 번을 한 번으로 ─────────── */
        // 이전에는 sum, average, max, min이 각자 배열을 훑어 모두 네 번 순회했다.
        // summaryStatistics()는 한 번 훑으면서 다섯 값을 한꺼번에 모은다.
        IntSummaryStatistics stat = Arrays.stream(scores).summaryStatistics();

        System.out.println("== 문제 ① 집계 ==");
        System.out.println("  합계   반복문 " + sumLoop(scores) + " | 스트림 " + stat.getSum()
                + " | 같은가 " + (sumLoop(scores) == stat.getSum()));
        System.out.println("  평균   반복문 " + averageLoop(scores) + " | 스트림 " + stat.getAverage()
                + " | 같은가 " + (Math.abs(averageLoop(scores) - stat.getAverage()) < 1e-9));
        System.out.println("  최고점 반복문 " + maxLoop(scores) + " | 스트림 " + stat.getMax()
                + " | 같은가 " + (maxLoop(scores) == stat.getMax()));
        System.out.println("  최저점 반복문 " + minLoop(scores) + " | 스트림 " + stat.getMin()
                + " | 같은가 " + (minLoop(scores) == stat.getMin()));

        // 하나만 필요하다면 summaryStatistics까지 갈 것 없이 이렇게 쓴다.
        // 빈 배열이면 값이 없으므로 Optional이 나온다 — orElse로 없을 때를 반드시 적게 만든다.
        System.out.println("  (최저점만 필요할 때) "
                + Arrays.stream(scores).min().orElse(0));

        /* ─────────── 문제 ② 조건 세기와 조건 수집 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 조건 검색 ==");

        long countStream = Arrays.stream(scores).filter(s -> s >= 80).count();
        System.out.println("  80점 이상 반복문 " + countAtLeastLoop(scores, 80)
                + " | 스트림 " + countStream
                + " | 같은가 " + (countAtLeastLoop(scores, 80) == countStream));

        double average = stat.getAverage();
        int[] belowLoop = collectBelowLoop(scores, average);
        int[] belowStream = Arrays.stream(scores).filter(s -> s < average).toArray();
        System.out.println("  평균 미만 반복문 " + Arrays.toString(belowLoop));
        System.out.println("  평균 미만 스트림 " + Arrays.toString(belowStream));
        System.out.println("  같은가 " + Arrays.equals(belowLoop, belowStream));

        System.out.println();
        System.out.println("두 방식 모두 배열을 훑는 횟수만 다를 뿐 O(n)이다.");
        System.out.println("스트림은 표현을 바꾼 것이지 복잡도를 바꾼 것이 아니다.");
    }
}
```

> `average`를 람다 안에서 쓰므로 **한 번 대입하고 바꾸지 않는 지역 변수**여야 한다(effectively final). 람다 문서 "자주 하는 실수 ①"이 다루는 규칙이다.

- [ ] **Step 3: 컴파일과 실행으로 검증**

```bash
cd examples/java/02-arrays-and-lists && javac -encoding UTF-8 ModernizeSolution.java && java ModernizeSolution
```
Expected: 모든 "같은가" 줄이 `true`. 합계 620, 평균 77.5, 최고점 93, 최저점 55, 80점 이상 4명.

- [ ] **Step 4: 강의 HTML에 섹션 삽입**

`algorithms/02-arrays-and-lists.html`의 1688줄 `</section>`(17번 섹션 끝) 바로 뒤,
`<!-- 18. 확인 퀴즈 -->` 앞에 삽입한다.

```html

            <!-- 18. 람다·스트림 수정 문제 -->
            <section class="lesson-section" id="sec-modernize" aria-labelledby="h-modernize">
                <h2 id="h-modernize" data-toc-label="람다·스트림 수정 문제"><span class="section-no">18</span>람다·스트림 수정 문제</h2>
                <p>
                    지금까지 쓴 코드를 <strong>같은 결과를 내는 다른 문법</strong>으로 고쳐 보는 문제입니다.
                    필요한 문법은 추가 정보의
                    <a href="../supplements/java-streams.html">자바 스트림</a> 문서에 모두 있습니다.
                    먼저 스스로 고쳐 본 뒤 정답을 펼치세요.
                </p>
                <div class="note-box note-box--warn">
                    <span class="note-box__title">문법을 바꿔도 복잡도는 그대로다</span>
                    <p>
                        아래 문제의 정답은 전부 <strong>O(n)</strong>입니다. 반복문 버전도 O(n)입니다.
                        스트림으로 바꿨다고 빨라지지 않습니다. 바뀌는 것은
                        <strong>"무엇을 하는 코드인지가 얼마나 잘 읽히는가"</strong>뿐입니다.
                        이 강의의 주제인 <strong>순회 O(n)</strong>은 문법과 무관하다는 점을 잊지 마세요.
                    </p>
                </div>

                <h3>수정 문제 ① — 배열을 네 번 훑는 집계를 한 번으로</h3>
                <p>
                    <code>ScoreStatsComplete.java</code>의 통계 메서드들은 각자 배열을 처음부터 끝까지 훑습니다.
                    합계·평균·최고점·최저점을 모두 구하면 <strong>배열을 네 번</strong> 지나갑니다.
                </p>
                <article class="code-card">
                    <header class="code-card__header">
                        <div class="code-card__info">
                            <span class="code-card__language">Java</span>
                            <strong class="code-card__filename">ScoreStatsComplete.java — 지금 코드</strong>
                        </div>
                        <button type="button" class="copy-code-button" data-copy-target="code-mod-q1" aria-label="수정 문제 1 원본 코드 복사">코드 복사</button>
                    </header>
                    <pre><code id="code-mod-q1" class="language-java">static int sum(int[] data) {
    int total = 0;
    for (int i = 0; i &lt; data.length; i++) {
        total = total + data[i];
    }
    return total;
}

static int max(int[] data) {
    int candidate = data[0];
    for (int i = 1; i &lt; data.length; i++) {
        if (data[i] &gt; candidate) {
            candidate = data[i];
        }
    }
    return candidate;
}

// min도 부등호 방향만 다른 같은 구조, average는 sum을 한 번 더 부른다</code></pre>
                </article>
                <p><strong>요구사항</strong></p>
                <ul>
                    <li>합계·평균·최고점·최저점을 <strong>배열을 한 번만 훑어</strong> 모두 구할 것</li>
                    <li>결과는 반복문 버전과 정확히 같아야 함 (합계 620, 평균 77.5, 최고 93, 최저 55)</li>
                    <li>힌트 — <a href="../supplements/java-streams.html#sec-collect">모으기와 그룹핑</a> 절의 <code>summaryStatistics()</code></li>
                </ul>

                <details class="answer-box">
                    <summary>정답과 해설 보기 — 수정 문제 ①</summary>
                    <article class="code-card">
                        <header class="code-card__header">
                            <div class="code-card__info">
                                <span class="code-card__language">Java</span>
                                <strong class="code-card__filename">ModernizeSolution.java — 정답</strong>
                            </div>
                            <button type="button" class="copy-code-button" data-copy-target="code-mod-a1" aria-label="수정 문제 1 정답 코드 복사">코드 복사</button>
                        </header>
                        <pre><code id="code-mod-a1" class="language-java">IntSummaryStatistics stat = Arrays.stream(scores).summaryStatistics();

stat.getSum();       // 620  — 합계
stat.getAverage();   // 77.5 — 평균
stat.getMax();       // 93   — 최고점
stat.getMin();       // 55   — 최저점
stat.getCount();     // 8    — 개수

// 하나만 필요하다면 여기까지 갈 것 없다
int lowest = Arrays.stream(scores).min().orElse(0);</code></pre>
                    </article>
                    <p>
                        네 번의 순회가 한 번이 됩니다. 다만 <strong>복잡도는 그대로 O(n)</strong>입니다 —
                        4 × O(n)도 O(n)이기 때문입니다. 1강에서 "상수 배는 Big-O에서 버린다"고 배운 그대로입니다.
                        실제 걸리는 시간은 줄지만 <strong>증가하는 모양은 같습니다.</strong>
                    </p>
                    <p>
                        <code>min()</code>이 <code>OptionalInt</code>를 돌려주는 것에도 이유가 있습니다.
                        반복문 버전의 <code>data[0]</code>은 <strong>빈 배열이면 예외</strong>가 났습니다.
                        <code>orElse(0)</code>은 "없을 때 어떻게 할지"를 반드시 적게 만듭니다.
                    </p>
                </details>

                <h3>수정 문제 ② — 두 번 훑던 조건 수집을 한 줄로</h3>
                <p>
                    <code>collectBelow</code>는 배열 크기를 미리 알아야 해서
                    <strong>개수를 세는 순회</strong>와 <strong>값을 채우는 순회</strong>를 따로 돕니다.
                    이것은 자바 배열의 제약이지 알고리즘의 요구가 아닙니다.
                </p>
                <article class="code-card">
                    <header class="code-card__header">
                        <div class="code-card__info">
                            <span class="code-card__language">Java</span>
                            <strong class="code-card__filename">ScoreStatsComplete.java — 지금 코드</strong>
                        </div>
                        <button type="button" class="copy-code-button" data-copy-target="code-mod-q2" aria-label="수정 문제 2 원본 코드 복사">코드 복사</button>
                    </header>
                    <pre><code id="code-mod-q2" class="language-java">static int countAtLeast(int[] data, int threshold) {
    int count = 0;
    for (int i = 0; i &lt; data.length; i++) {
        if (data[i] &gt;= threshold) {
            count++;
        }
    }
    return count;
}

static int[] collectBelow(int[] data, double limit) {
    int count = 0;
    for (int i = 0; i &lt; data.length; i++) {
        if (data[i] &lt; limit) count++;            // 1차 순회: 개수 세기
    }
    int[] result = new int[count];               // 정확한 크기의 새 배열
    int pos = 0;
    for (int i = 0; i &lt; data.length; i++) {
        if (data[i] &lt; limit) {
            result[pos] = data[i];               // 2차 순회: 값 채우기
            pos++;
        }
    }
    return result;
}</code></pre>
                </article>
                <p><strong>요구사항</strong></p>
                <ul>
                    <li>"80점 이상인 학생 수"를 한 줄로 구할 것</li>
                    <li>"평균 미만인 점수만 담은 새 배열"을 <strong>크기 계산 없이</strong> 만들 것</li>
                    <li>원본 배열 <code>scores</code>는 바뀌지 않아야 함</li>
                    <li>힌트 — <a href="../supplements/java-streams.html#sec-intermediate">중간 연산</a>의 <code>filter</code>,
                        <a href="../supplements/java-streams.html#sec-terminal">최종 연산</a>의 <code>count()</code>·<code>toArray()</code></li>
                </ul>

                <details class="answer-box">
                    <summary>정답과 해설 보기 — 수정 문제 ②</summary>
                    <article class="code-card">
                        <header class="code-card__header">
                            <div class="code-card__info">
                                <span class="code-card__language">Java</span>
                                <strong class="code-card__filename">ModernizeSolution.java — 정답</strong>
                            </div>
                            <button type="button" class="copy-code-button" data-copy-target="code-mod-a2" aria-label="수정 문제 2 정답 코드 복사">코드 복사</button>
                        </header>
                        <pre><code id="code-mod-a2" class="language-java">long count = Arrays.stream(scores).filter(s -&gt; s &gt;= 80).count();

double average = Arrays.stream(scores).average().orElse(0);
int[] below = Arrays.stream(scores).filter(s -&gt; s &lt; average).toArray();

// 주의: average를 람다 안에서 쓰려면 한 번 대입한 뒤 바꾸지 않아야 한다.
//       (람다 문서의 "자주 하는 실수 ①" — effectively final)</code></pre>
                    </article>
                    <p>
                        <code>count</code>가 <code>long</code>인 것에 주의하세요.
                        원소가 <code>int</code> 범위를 넘을 수 있는 스트림도 있기 때문입니다.
                    </p>
                    <p>
                        "개수를 먼저 세고 크기를 정해 채운다"는 절차가 통째로 사라졌습니다.
                        하지만 <code>toArray()</code>도 내부에서는 결국 크기를 맞추는 일을 합니다 —
                        <strong>일이 없어진 것이 아니라 우리가 적지 않게 된 것</strong>입니다.
                        복잡도는 여전히 O(n)입니다.
                    </p>
                </details>

                <p>
                    정답 전체와 "반복문 결과와 같은지" 확인 코드는
                    <code>examples/java/02-arrays-and-lists/ModernizeSolution.java</code>에 있습니다.
                    직접 실행해 모든 비교가 <code>true</code>로 나오는지 확인하세요.
                </p>
            </section>
```

- [ ] **Step 5: 뒤 섹션 번호 밀기**

같은 파일에서 세 곳을 고친다.

| 주석 | `section-no` |
|---|---|
| `<!-- 18. 확인 퀴즈 -->` → `<!-- 19. 확인 퀴즈 -->` | `18` → `19` |
| `<!-- 19. 오늘의 핵심 정리 -->` → `<!-- 20. ... -->` | `19` → `20` |
| `<!-- 20. 다음 강의 연결 -->` → `<!-- 21. ... -->` | `20` → `21` |

- [ ] **Step 6: 예제 개수 데이터 갱신**

`assets/js/algorithms-data.js`의 2강 항목과 `data/algorithms.json`의 같은 항목에서
`"examples": 9` → `10`. **두 파일을 똑같이** 고쳐야 한다(문자열 비교로 검증됨).

- [ ] **Step 7: 검증**

Run: `npm run build`
Expected: `모든 검증 통과 ✓` — 특히 `id 중복`과 `복사 대상 id 없음` 오류가 없어야 한다.

Run: `grep -c "section-no" algorithms/02-arrays-and-lists.html`
Expected: `21`

- [ ] **Step 8: 커밋**

```bash
git add examples/java/02-arrays-and-lists/ModernizeSolution.java algorithms/02-arrays-and-lists.html assets/js/algorithms-data.js data/algorithms.json
git commit -m "feat: 2강에 람다·스트림 수정 문제 추가"
```

---

### Task 3: 3강 — 빈도 누적과 빈도 정렬

**Files:**
- Create: `examples/java/03-brute-force-string-hash/ModernizeSolution.java`
- Modify: `algorithms/03-brute-force-string-hash.html`
- Modify: `assets/js/algorithms-data.js` (3강 `9` → `10`), `data/algorithms.json`

**Interfaces:**
- Consumes: Task 1의 `max(Comparator)` 표 항목, Task 2가 정한 섹션 구조와 `code-mod-*` id 규칙
- Produces: 없음 (강의별로 독립)

- [ ] **Step 1: 정답 Java 파일 작성**

Create `examples/java/03-brute-force-string-hash/ModernizeSolution.java`:

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 3강 「람다·스트림 수정 문제」 정답.
 *
 * WordAnalysisComplete.java의 빈도 누적을 스트림으로 다시 쓴 것이다.
 * 해시를 쓰는 것은 양쪽 모두 같다 — O(n)이라는 사실은 문법과 무관하다.
 */
public class ModernizeSolution {

    /* ─────────── 이전: WordAnalysisComplete와 같은 반복문 코드 ─────────── */

    static Map<String, Integer> countFrequenciesLoop(String[] words) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
    }

    /** 가장 많이 나온 단어 하나 (동점이면 먼저 만난 쪽) */
    static String mostFrequentLoop(String[] words, Map<String, Integer> freq) {
        String best = null;
        for (String word : words) {
            if (best == null || freq.get(word) > freq.get(best)) {
                best = word;
            }
        }
        return best;
    }

    /** 2번 이상 나온 단어를 빈도 내림차순, 동점이면 사전순으로 */
    static List<String> repeatedLoop(Map<String, Integer> freq) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            if (e.getValue() >= 2) {
                result.add(e.getKey());
            }
        }
        // 삽입 정렬로 빈도 내림차순, 동점이면 사전순
        for (int i = 1; i < result.size(); i++) {
            String key = result.get(i);
            int j = i - 1;
            while (j >= 0 && worseThan(result.get(j), key, freq)) {
                result.set(j + 1, result.get(j));
                j--;
            }
            result.set(j + 1, key);
        }
        return result;
    }

    /** a가 b보다 뒤에 와야 하면 true */
    static boolean worseThan(String a, String b, Map<String, Integer> freq) {
        if (!freq.get(a).equals(freq.get(b))) {
            return freq.get(a) < freq.get(b);     // 빈도가 낮으면 뒤로
        }
        return a.compareTo(b) > 0;                // 동점이면 사전순
    }

    public static void main(String[] args) {
        String sentence = "apple banana apple orange banana apple kiwi orange plum kiwi";
        String[] words = sentence.split(" ");

        /* ─────────── 문제 ① 빈도 누적 ─────────── */
        Map<String, Integer> freqLoop = countFrequenciesLoop(words);

        // groupingBy(무엇으로 묶을까, 묶은 것을 어떻게 셀까)
        // 값이 Long인 이유: counting()은 원소 수를 long으로 센다.
        Map<String, Long> freqStream = java.util.Arrays.stream(words)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        System.out.println("== 문제 ① 단어 빈도 ==");
        System.out.println("  반복문 " + new java.util.TreeMap<>(freqLoop));
        System.out.println("  스트림 " + new java.util.TreeMap<>(freqStream));
        boolean sameFreq = freqLoop.size() == freqStream.size();
        for (Map.Entry<String, Integer> e : freqLoop.entrySet()) {
            if (freqStream.get(e.getKey()) == null
                    || freqStream.get(e.getKey()) != e.getValue().longValue()) {
                sameFreq = false;
            }
        }
        System.out.println("  같은가 " + sameFreq);

        /* ─────────── 문제 ② 최빈 단어와 반복 단어 정렬 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 최빈 단어 ==");

        String bestStream = freqStream.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        System.out.println("  반복문 " + mostFrequentLoop(words, freqLoop)
                + " | 스트림 " + bestStream
                + " | 같은가 " + mostFrequentLoop(words, freqLoop).equals(bestStream));

        // 빈도 내림차순, 동점이면 사전순 — 기준을 조립해서 만든다
        Comparator<Map.Entry<String, Long>> byCountDescThenWord =
                Comparator.<Map.Entry<String, Long>, Long>comparing(Map.Entry::getValue)
                          .reversed()
                          .thenComparing(Map.Entry::getKey);

        List<String> repeatedStream = freqStream.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .sorted(byCountDescThenWord)
                .map(Map.Entry::getKey)
                .toList();

        List<String> repeatedLoopResult = repeatedLoop(freqLoop);
        System.out.println("  2회 이상 반복문 " + repeatedLoopResult);
        System.out.println("  2회 이상 스트림 " + repeatedStream);
        System.out.println("  같은가 " + repeatedLoopResult.equals(repeatedStream));

        System.out.println();
        System.out.println("빈도 계산은 양쪽 모두 O(n)이다. 정렬을 붙이면 양쪽 모두 O(k log k)가 더해진다.");
        System.out.println("스트림은 '무엇을 하는지'를 드러낼 뿐, 완전 탐색을 해시로 바꾼 3강의 성과와는 별개다.");
    }
}
```

> `Comparator.<Map.Entry<String, Long>, Long>comparing(...)`처럼 타입을 명시하는 이유: `Map.Entry::getValue`만 보고는 컴파일러가 원소 타입을 정할 수 없다. 람다 문서 "문법 한눈에" 절이 지적한 그 상황이다.

- [ ] **Step 2: 컴파일과 실행으로 검증**

```bash
cd examples/java/03-brute-force-string-hash && javac -encoding UTF-8 ModernizeSolution.java && java ModernizeSolution
```
Expected: 세 "같은가" 줄이 모두 `true`. 최빈 단어 `apple`(3회), 2회 이상 = `[apple, banana, kiwi, orange]`.

- [ ] **Step 3: 강의 HTML에 섹션 삽입**

Task 2 Step 4의 섹션 골격을 그대로 쓰되 다음을 바꾼다.

- 도입 문단의 링크: `<a href="../supplements/java-streams.html">자바 스트림</a>` 유지
- 경고 상자 본문: "빈도 계산이 O(n)인 것은 **해시** 덕분이지 문법 덕분이 아닙니다. 3강의 성과는 이중 반복문 O(n²)을 해시 O(n)으로 바꾼 것이고, 그것은 스트림으로 쓰든 반복문으로 쓰든 그대로입니다."
- 문제 ① 제목: `수정 문제 ① — getOrDefault 누적을 한 줄로`
  - 원본 발췌(`code-mod-q1`): `WordAnalysisComplete.java`의 `countFrequencies` 메서드 전체
  - 요구사항: 결과가 반복문 버전과 같을 것 / `HashMap`을 직접 만들지 말 것 / 힌트는 `#sec-collect`의 `groupingBy` + `counting()`
  - 정답(`code-mod-a1`): 위 Java 파일의 `freqStream` 3줄. 해설에 **값 타입이 `Integer`가 아니라 `Long`으로 바뀐다**는 점과 그 이유(`counting()`은 `long`으로 센다)를 밝힌다.
- 문제 ② 제목: `수정 문제 ② — 최빈 단어와 "2회 이상" 목록`
  - 원본 발췌(`code-mod-q2`): 위 Java 파일의 `mostFrequentLoop` + `repeatedLoop`(삽입 정렬 부분 포함)
  - 요구사항: 최빈 단어 1개 / 2회 이상 단어를 **빈도 내림차순, 동점이면 사전순**으로 / 힌트는 `#sec-terminal`의 `max(Comparator)`와 람다 문서의 `reversed()`·`thenComparing()`
  - 정답(`code-mod-a2`): 위 Java 파일의 `bestStream`과 `byCountDescThenWord` + `repeatedStream`
  - 해설에 **`HashMap`은 순서를 보장하지 않으므로 정렬을 명시해야 한다**는 3강의 함정을 다시 짚는다.
- 마무리 문단의 파일 경로: `examples/java/03-brute-force-string-hash/ModernizeSolution.java`

- [ ] **Step 4: 뒤 섹션 번호 밀기 · 데이터 갱신**

Task 2 Step 5·6과 같은 방식. 3강 `examples: 9` → `10`.

- [ ] **Step 5: 검증**

Run: `npm run build`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 6: 커밋**

```bash
git add examples/java/03-brute-force-string-hash/ModernizeSolution.java algorithms/03-brute-force-string-hash.html assets/js/algorithms-data.js data/algorithms.json
git commit -m "feat: 3강에 람다·스트림 수정 문제 추가"
```

---

### Task 4: 4강 — 블록 람다를 Comparator 조립으로

**Files:**
- Create: `examples/java/04-sorting-algorithms/ModernizeSolution.java`
- Modify: `algorithms/04-sorting-algorithms.html`
- Modify: `assets/js/algorithms-data.js` (4강 `10` → `11`), `data/algorithms.json`

**Interfaces:**
- Consumes: Task 2가 정한 섹션 구조
- Produces: 없음

- [ ] **Step 1: 원본 확인**

Run: `grep -n "PRICE_ASC\|RATING_DESC" examples/java/04-sorting-algorithms/ProductSorterSolution.java`
Expected: 세 개의 `static final Comparator<Product> ... = (a, b) -> { ... };`

- [ ] **Step 2: 정답 Java 파일 작성**

Create `examples/java/04-sorting-algorithms/ModernizeSolution.java`:

```java
import java.util.Arrays;
import java.util.Comparator;

/**
 * 4강 「람다·스트림 수정 문제」 정답.
 *
 * ProductSorterSolution.java의 블록 몸통 람다 세 개를
 * Comparator의 조립 메서드로 다시 쓴 것이다.
 * 정렬 알고리즘(삽입 정렬)은 한 줄도 바뀌지 않는다.
 */
public class ModernizeSolution {

    static class Product {
        String name;
        int price;
        double rating;

        Product(String name, int price, double rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
        }

        String getName() { return name; }

        @Override
        public String toString() {
            return name + " (" + price + "원, 평점 " + rating + ")";
        }
    }

    /* ─────────── 이전: ProductSorterSolution의 블록 몸통 람다 ─────────── */

    static final Comparator<Product> PRICE_ASC_OLD = (a, b) -> {
        return Integer.compare(a.price, b.price);
    };

    static final Comparator<Product> RATING_DESC_OLD = (a, b) -> {
        return Double.compare(b.rating, a.rating);
    };

    static final Comparator<Product> RATING_DESC_THEN_NAME_OLD = (a, b) -> {
        int byRating = Double.compare(b.rating, a.rating);
        if (byRating != 0) {
            return byRating;
        }
        return a.name.compareTo(b.name);
    };

    /* ─────────── 이후: 조립해서 만든 기준 ─────────── */

    // "무엇으로 비교할지"만 준다. 부호를 따질 일이 없다.
    static final Comparator<Product> PRICE_ASC_NEW =
            Comparator.comparingInt(p -> p.price);

    // 내림차순은 부호를 뒤집는 것이 아니라 reversed()로 말한다.
    static final Comparator<Product> RATING_DESC_NEW =
            Comparator.comparingDouble((Product p) -> p.rating).reversed();

    // "평점으로 비교 → 뒤집기 → 동점이면 이름으로"가 순서대로 읽힌다.
    static final Comparator<Product> RATING_DESC_THEN_NAME_NEW =
            Comparator.comparingDouble((Product p) -> p.rating)
                      .reversed()
                      .thenComparing(Product::getName);

    /** ProductSorterSolution과 같은 삽입 정렬 — 이 코드는 바뀌지 않는다 */
    static void insertionSort(Product[] arr, Comparator<Product> comp) {
        for (int i = 1; i < arr.length; i++) {
            Product key = arr[i];
            int j = i - 1;
            while (j >= 0 && comp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    static String namesOf(Product[] items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(items[i].name);
        }
        return sb.toString();
    }

    /** 같은 배열에 두 기준을 각각 적용해 결과 순서가 같은지 확인한다 */
    static boolean sameOrder(Product[] source,
                             Comparator<Product> oldRule,
                             Comparator<Product> newRule) {
        Product[] a = Arrays.copyOf(source, source.length);
        Product[] b = Arrays.copyOf(source, source.length);
        insertionSort(a, oldRule);
        insertionSort(b, newRule);
        return Arrays.equals(a, b);
    }

    public static void main(String[] args) {
        Product[] products = {
            new Product("무선 마우스", 23000, 4.5),
            new Product("기계식 키보드", 89000, 4.8),
            new Product("USB 허브", 15000, 4.2),
            new Product("모니터 받침대", 23000, 4.7),
            new Product("노트북 파우치", 18000, 4.5),
            new Product("웹캠", 54000, 4.2)
        };

        System.out.println("== 문제 ① 단일 기준 ==");
        System.out.println("  가격 오름차순 같은가 "
                + sameOrder(products, PRICE_ASC_OLD, PRICE_ASC_NEW));
        System.out.println("  평점 내림차순 같은가 "
                + sameOrder(products, RATING_DESC_OLD, RATING_DESC_NEW));

        System.out.println();
        System.out.println("== 문제 ② 다중 기준 ==");
        System.out.println("  평점 내림차순 + 동점 시 이름순 같은가 "
                + sameOrder(products, RATING_DESC_THEN_NAME_OLD, RATING_DESC_THEN_NAME_NEW));

        Product[] sorted = Arrays.copyOf(products, products.length);
        insertionSort(sorted, RATING_DESC_THEN_NAME_NEW);
        System.out.println("  결과: " + namesOf(sorted));

        System.out.println();
        System.out.println("== 안정성 확인 ==");
        // 가격이 같은 23000원 두 상품이 입력 순서를 지키는지 — 안정 정렬의 성질
        Product[] byPrice = Arrays.copyOf(products, products.length);
        insertionSort(byPrice, PRICE_ASC_NEW);
        System.out.println("  " + namesOf(byPrice));
        System.out.println("  23000원 두 상품이 입력 순서(무선 마우스 → 모니터 받침대)를 유지: "
                + (byPrice[2].name.equals("무선 마우스") && byPrice[3].name.equals("모니터 받침대")));

        System.out.println();
        System.out.println("정렬 코드(insertionSort)는 한 줄도 바뀌지 않았다.");
        System.out.println("비교 횟수도 같다 — 삽입 정렬은 여전히 최악 O(n²)이다.");
    }
}
```

- [ ] **Step 3: 컴파일과 실행으로 검증**

```bash
cd examples/java/04-sorting-algorithms && javac -encoding UTF-8 ModernizeSolution.java && java ModernizeSolution
```
Expected: 세 "같은가" 줄이 모두 `true`, 안정성 확인도 `true`.

- [ ] **Step 4: 강의 HTML에 섹션 삽입**

Task 2 Step 4의 골격을 쓰되 다음을 바꾼다.

- 도입 문단 링크: `<a href="../supplements/lambda-expressions.html">람다식</a>`
- 경고 상자 본문: "`Comparator`를 어떻게 쓰든 **삽입 정렬은 최악 O(n²)** 그대로입니다. 4강의 성과는 정렬 알고리즘 자체이고, 람다는 **기준을 갈아 끼우기 쉽게** 만들 뿐입니다."
- 문제 ① 제목: `수정 문제 ① — 부호를 따지는 비교를 "무엇으로 비교할지"로`
  - 원본(`code-mod-q1`): `PRICE_ASC`와 `RATING_DESC` 두 상수 (`ProductSorterSolution.java`에서 그대로)
  - 요구사항: 중괄호와 `return`을 없앨 것 / 내림차순을 `b`, `a` 순서 뒤집기가 아닌 방법으로 표현할 것 / 가능한 곳은 메서드 참조까지 / 힌트는 람다 문서 `#sec-standard`와 `#sec-methodref`
  - 정답(`code-mod-a1`): `PRICE_ASC_NEW`, `RATING_DESC_NEW`. 해설에 **3단계 축약**(블록 람다 → 식 람다 → `comparingInt` → 메서드 참조)을 단계별로 보여 주고, `(Product p)`처럼 타입을 명시해야 하는 이유를 설명한다.
- 문제 ② 제목: `수정 문제 ② — if 분기로 쓴 다중 기준을 thenComparing으로`
  - 원본(`code-mod-q2`): `RATING_DESC_THEN_NAME` 상수 전체
  - 요구사항: `if` 없이 쓸 것 / **안정 정렬이 깨지지 않아야 함**(원본 주석이 지적한 `> 0` 조건) / 힌트는 `reversed()`·`thenComparing()`
  - 정답(`code-mod-a2`): `RATING_DESC_THEN_NAME_NEW`. 해설에 "익명 클래스·블록 람다 버전은 부호를 읽어야 의미가 드러나지만, 조립 버전은 **평점으로 비교 → 뒤집기 → 동점이면 이름으로**가 순서대로 읽힌다"를 쓴다.
- 마무리 문단 경로: `examples/java/04-sorting-algorithms/ModernizeSolution.java`

- [ ] **Step 5: 뒤 섹션 번호 밀기 · 데이터 갱신 (4강 `10` → `11`)**

- [ ] **Step 6: 검증**

Run: `npm run build`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 7: 커밋**

```bash
git add examples/java/04-sorting-algorithms/ModernizeSolution.java algorithms/04-sorting-algorithms.html assets/js/algorithms-data.js data/algorithms.json
git commit -m "feat: 4강에 람다·스트림 수정 문제 추가"
```

---

### Task 5: 5강 — 조건을 값으로 받는 탐색

**Files:**
- Create: `examples/java/05-search-algorithms/ModernizeSolution.java`
- Modify: `algorithms/05-search-algorithms.html`
- Modify: `assets/js/algorithms-data.js` (5강 `11` → `12`), `data/algorithms.json`

**Interfaces:**
- Consumes: Task 2가 정한 섹션 구조
- Produces: 없음

- [ ] **Step 1: 정답 Java 파일 작성**

Create `examples/java/05-search-algorithms/ModernizeSolution.java`:

```java
import java.util.function.Predicate;

/**
 * 5강 「람다·스트림 수정 문제」 정답.
 *
 * 순차 탐색의 "무엇을 찾는가"를 Predicate로 분리한다.
 * 알고리즘은 그대로 O(n)이며, 이진 탐색은 이 방식으로 일반화되지 않는다.
 */
public class ModernizeSolution {

    /** BookSearchApplication과 같은 구조 */
    static class Book {
        int number;
        String title;
        int stock;

        Book(int number, String title, int stock) {
            this.number = number;
            this.title = title;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return "[" + number + "] " + title + " (재고 " + stock + ")";
        }
    }

    /* ─────────── 이전: 찾는 것이 바뀔 때마다 메서드가 늘어난다 ─────────── */

    static int findByNumberLoop(Book[] books, int number) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].number == number) return i;
        }
        return -1;
    }

    static int findByTitleLoop(Book[] books, String title) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].title.equals(title)) return i;
        }
        return -1;
    }

    static int findInStockLoop(Book[] books) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].stock > 0) return i;
        }
        return -1;
    }

    /* ─────────── 이후: 순회는 한 번만 쓰고, 조건은 받는다 ─────────── */

    /**
     * 조건에 맞는 첫 도서의 위치. 없으면 -1.
     * 순차 탐색 그대로다 — 찾는 즉시 return하는 조기 중단도 그대로 O(n).
     */
    static int findFirstIndex(Book[] books, Predicate<Book> match) {
        for (int i = 0; i < books.length; i++) {
            if (match.test(books[i])) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Book[] books = {
            new Book(1001, "알고리즘 첫걸음", 3),
            new Book(1203, "자바 프로그래밍 입문", 5),
            new Book(1450, "자료구조의 이해", 2),
            new Book(2088, "데이터베이스 개론", 0),
            new Book(2311, "운영체제 원리", 4),
            new Book(2754, "컴퓨터 네트워크", 1)
        };

        System.out.println("== 문제 ① 조건을 값으로 받는 순차 탐색 ==");

        int byNumberOld = findByNumberLoop(books, 2311);
        int byNumberNew = findFirstIndex(books, b -> b.number == 2311);
        System.out.println("  번호 2311  이전 " + byNumberOld + " | 이후 " + byNumberNew
                + " | 같은가 " + (byNumberOld == byNumberNew));

        int byTitleOld = findByTitleLoop(books, "자료구조의 이해");
        int byTitleNew = findFirstIndex(books, b -> b.title.equals("자료구조의 이해"));
        System.out.println("  제목 검색  이전 " + byTitleOld + " | 이후 " + byTitleNew
                + " | 같은가 " + (byTitleOld == byTitleNew));

        int inStockOld = findInStockLoop(books);
        int inStockNew = findFirstIndex(books, b -> b.stock > 0);
        System.out.println("  재고 있음  이전 " + inStockOld + " | 이후 " + inStockNew
                + " | 같은가 " + (inStockOld == inStockNew));

        // 메서드를 새로 만들지 않고 새 조건을 바로 쓸 수 있다
        int cheapAndStocked = findFirstIndex(books, b -> b.stock >= 3 && b.number > 1100);
        System.out.println("  새 조건(재고 3권 이상 + 번호 1100 초과): "
                + (cheapAndStocked >= 0 ? books[cheapAndStocked].toString() : "없음"));

        System.out.println();
        System.out.println("== 문제 ② 이진 탐색은 왜 이렇게 못 바꾸는가 ==");
        System.out.println("  Predicate가 답할 수 있는 것은 '맞다 / 아니다' 둘뿐이다.");
        System.out.println("  이진 탐색에 필요한 답은 '같다 / 왼쪽으로 / 오른쪽으로' 셋이다.");
        System.out.println("  게다가 그 판정은 배열이 정렬된 기준과 같아야 한다.");
        System.out.println("  그래서 이진 탐색을 일반화하려면 Predicate가 아니라 Comparator를 받아야 한다.");
        System.out.println();
        System.out.println("  findFirstIndex는 여전히 O(n)이다. 람다를 썼다고 이진 탐색이 되지 않는다.");
        System.out.println("  O(log n)을 만드는 것은 '정렬해 두었다'는 조건이지 문법이 아니다.");
    }
}
```

- [ ] **Step 2: 컴파일과 실행으로 검증**

```bash
cd examples/java/05-search-algorithms && javac -encoding UTF-8 ModernizeSolution.java && java ModernizeSolution
```
Expected: 세 "같은가" 줄이 모두 `true`. 새 조건 결과는 `[1203] 자바 프로그래밍 입문 (재고 5)`.

- [ ] **Step 3: 강의 HTML에 섹션 삽입**

Task 2 Step 4의 골격을 쓰되:

- 도입 문단 링크: `<a href="../supplements/lambda-expressions.html">람다식</a>`
- 경고 상자 본문: "`Predicate`로 바꾼 순차 탐색은 **여전히 O(n)**입니다. 5강의 성과는 '정렬해 두면 O(log n)으로 줄일 수 있다'이고, 그것은 문법과 아무 상관이 없습니다."
- 문제 ① 제목: `수정 문제 ① — 찾는 조건이 바뀔 때마다 메서드를 만들지 않으려면`
  - 원본(`code-mod-q1`): 위 Java 파일의 `findByNumberLoop` / `findByTitleLoop` / `findInStockLoop` 세 메서드
  - 요구사항: 순회 코드는 **한 번만** 쓸 것 / 세 검색이 모두 같은 메서드를 부를 것 / 조기 중단(`return`)을 유지할 것 / 힌트는 람다 문서 `#sec-standard`의 `Predicate<T>`
  - 정답(`code-mod-a1`): `findFirstIndex` + 세 호출. 해설에 "메서드가 늘던 자리에 **인자**가 늘게 되었다", "비교 횟수는 한 번도 줄지 않았다"를 쓴다.
- 문제 ② 제목: `수정 문제 ② — 판단 문제: 이진 탐색도 이렇게 바꿀 수 있을까?`
  - 원본(`code-mod-q2`): `BookSearchSolution.java`의 `binarySearch` 메서드 전체
  - 요구사항: **코드를 쓰지 말고 글로 답할 것.** `binarySearch`도 `Predicate<Book>`을 받는 형태로 일반화할 수 있는가? 없다면 무엇이 부족한가?
  - 정답(`<details>` 안, 코드 카드 없이 문단으로): `Predicate`는 참·거짓 두 가지만 답한다. 이진 탐색은 `==`, `<`, `>` **세 갈래** 판정이 필요하고, 그 판정 기준이 배열이 정렬된 기준과 반드시 같아야 한다. 그래서 일반화하려면 `Predicate`가 아니라 `Comparator`(또는 키를 뽑는 `Function`)를 받아야 한다. **"조건을 값으로 넘긴다"는 아이디어가 모든 알고리즘에 똑같이 적용되지는 않는다**는 것이 이 문제의 요점이다.
- 마무리 문단 경로: `examples/java/05-search-algorithms/ModernizeSolution.java`

- [ ] **Step 4: 뒤 섹션 번호 밀기 · 데이터 갱신 (5강 `11` → `12`)**

- [ ] **Step 5: 검증**

Run: `npm run build`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 6: 커밋**

```bash
git add examples/java/05-search-algorithms/ModernizeSolution.java algorithms/05-search-algorithms.html assets/js/algorithms-data.js data/algorithms.json
git commit -m "feat: 5강에 람다·스트림 수정 문제 추가"
```

---

### Task 6: 10강 — 그리디의 선택 기준을 값으로

**Files:**
- Create: `examples/java/10-greedy-algorithms/ModernizeSolution.java`
- Modify: `algorithms/10-greedy-algorithms.html`
- Modify: `assets/js/algorithms-data.js` (10강 `11` → `12`), `data/algorithms.json`

**Interfaces:**
- Consumes: Task 2가 정한 섹션 구조
- Produces: 없음

- [ ] **Step 1: 정답 Java 파일 작성**

Create `examples/java/10-greedy-algorithms/ModernizeSolution.java`:

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 10강 「람다·스트림 수정 문제」 정답.
 *
 * MeetingRoomComplete.java의 sortByEndTime은 "종료 시각 기준"이 코드에 박혀 있어
 * 다른 전략을 시험해 보려면 메서드를 통째로 복사해야 했다.
 * 기준을 Comparator로 받으면 세 전략을 같은 코드로 비교할 수 있다.
 */
public class ModernizeSolution {

    static class Meeting {
        String name;
        int start;
        int end;

        Meeting(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

    /* ─────────── 이전: 기준이 박힌 정렬 ─────────── */

    static void sortByEndTimeOld(Meeting[] meetings) {
        for (int i = 1; i < meetings.length; i++) {
            Meeting key = meetings[i];
            int j = i - 1;
            while (j >= 0 && meetings[j].end > key.end) {
                meetings[j + 1] = meetings[j];
                j--;
            }
            meetings[j + 1] = key;
        }
    }

    /* ─────────── 이후: 기준을 받는 정렬 ─────────── */

    /** 같은 삽입 정렬이다. 바뀐 것은 "무엇으로 비교하는가"를 밖에서 받는다는 점뿐이다. */
    static void sortBy(Meeting[] meetings, Comparator<Meeting> rule) {
        for (int i = 1; i < meetings.length; i++) {
            Meeting key = meetings[i];
            int j = i - 1;
            while (j >= 0 && rule.compare(meetings[j], key) > 0) {
                meetings[j + 1] = meetings[j];
                j--;
            }
            meetings[j + 1] = key;
        }
    }

    /** 그리디 선택 — 이 코드는 어떤 기준을 쓰든 바뀌지 않는다 */
    static List<Meeting> selectMeetings(Meeting[] sorted) {
        List<Meeting> selected = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;
        for (Meeting m : sorted) {
            if (m.start >= lastEnd) {
                selected.add(m);
                lastEnd = m.end;
            }
        }
        return selected;
    }

    static String namesOf(List<Meeting> meetings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < meetings.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(meetings.get(i).name);
        }
        return sb.toString();
    }

    static Meeting[] sampleMeetings() {
        return new Meeting[] {
            new Meeting("전략 기획", 8, 12),
            new Meeting("디자인 리뷰", 9, 10),
            new Meeting("개발 스탠드업", 10, 11),
            new Meeting("고객 미팅", 11, 13),
            new Meeting("채용 면접", 12, 14),
            new Meeting("팀 회고", 13, 15)
        };
    }

    public static void main(String[] args) {
        /* ─────────── 문제 ① 기준을 밖에서 받기 ─────────── */
        System.out.println("== 문제 ① 이전 방식과 같은 결과인가 ==");

        Meeting[] oldWay = sampleMeetings();
        sortByEndTimeOld(oldWay);
        List<Meeting> oldResult = selectMeetings(oldWay);

        Comparator<Meeting> byEndTime = Comparator.comparingInt(m -> m.end);
        Meeting[] newWay = sampleMeetings();
        sortBy(newWay, byEndTime);
        List<Meeting> newResult = selectMeetings(newWay);

        System.out.println("  이전 " + namesOf(oldResult));
        System.out.println("  이후 " + namesOf(newResult));
        System.out.println("  같은가 " + namesOf(oldResult).equals(namesOf(newResult)));

        /* ─────────── 세 전략을 같은 코드로 비교 ─────────── */
        System.out.println();
        System.out.println("== 기준만 바꿔 세 전략 비교 ==");

        Comparator<Meeting> byStartTime = Comparator.comparingInt(m -> m.start);
        Comparator<Meeting> byDuration = Comparator.comparingInt(m -> m.end - m.start);

        String[] labels = {"종료 시각 순 (정답 전략)", "시작 시각 순", "소요 시간 순"};
        List<Comparator<Meeting>> rules = Arrays.asList(byEndTime, byStartTime, byDuration);

        for (int i = 0; i < rules.size(); i++) {
            Meeting[] copy = sampleMeetings();
            sortBy(copy, rules.get(i));
            List<Meeting> picked = selectMeetings(copy);
            System.out.println("  " + labels[i] + " → " + picked.size() + "개: " + namesOf(picked));
        }
        System.out.println("  기준 하나를 바꿨을 뿐인데 결과가 달라진다 — 그리디의 성패는 기준에 달려 있다.");

        /* ─────────── 문제 ② 동점 처리를 명시하기 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 종료 시각이 같을 때 ==");

        Meeting[] tie = {
            new Meeting("긴 회의", 8, 12),
            new Meeting("짧은 회의", 11, 12),
            new Meeting("오후 회의", 12, 14)
        };

        // 종료 시각이 같으면 늦게 시작한(= 짧은) 회의를 앞에 둔다
        Comparator<Meeting> byEndThenLateStart =
                Comparator.comparingInt((Meeting m) -> m.end)
                          .thenComparing(Comparator.comparingInt((Meeting m) -> m.start).reversed());

        Meeting[] copy = Arrays.copyOf(tie, tie.length);
        sortBy(copy, byEndThenLateStart);
        System.out.println("  정렬 결과: " + namesOf(Arrays.asList(copy)));
        System.out.println("  선택 결과: " + namesOf(selectMeetings(copy)));
        System.out.println("  동점 규칙을 적지 않으면 입력 순서에 따라 결과가 달라진다.");
        System.out.println("  thenComparing은 그 '적지 않은 규칙'을 코드에 드러낸다.");

        System.out.println();
        System.out.println("정렬은 여전히 삽입 정렬 O(n²), 선택은 O(n)이다. 복잡도는 그대로다.");
    }
}
```

- [ ] **Step 2: 컴파일과 실행으로 검증**

```bash
cd examples/java/10-greedy-algorithms && javac -encoding UTF-8 ModernizeSolution.java && java ModernizeSolution
```
Expected: 문제 ①의 "같은가"가 `true`. 세 전략의 선택 개수가 서로 다르게 나온다(종료 시각 순이 가장 많음).

- [ ] **Step 3: 강의 HTML에 섹션 삽입**

Task 2 Step 4의 골격을 쓰되:

- 도입 문단 링크: `<a href="../supplements/lambda-expressions.html">람다식</a>`
- 경고 상자 본문: "기준을 `Comparator`로 뽑아도 **정렬은 그대로 O(n²), 선택은 O(n)**입니다. 10강의 주제는 '어떤 기준이 최적해를 주는가'이고, 람다는 그 기준을 **바꿔 끼워 실험하기 쉽게** 만들 뿐입니다."
- 문제 ① 제목: `수정 문제 ① — 선택 기준을 코드에서 빼내기`
  - 원본(`code-mod-q1`): `MeetingRoomComplete.java`의 `sortByEndTime` 메서드 전체
  - 요구사항: 정렬 로직은 그대로 두고 **비교 기준만 인자로 받을 것** / 종료 시각 기준으로 부르면 이전과 결과가 같아야 함 / 같은 메서드로 시작 시각·소요 시간 전략도 시험해 볼 것 / 힌트는 람다 문서 `#sec-standard`의 `Comparator`와 `comparingInt`
  - 정답(`code-mod-a1`): `sortBy` + 세 `Comparator` + 비교 루프. 해설에 "그리디가 실패하는 사례를 직접 만들어 볼 때 이 형태가 특히 쓸모 있다"를 쓴다.
- 문제 ② 제목: `수정 문제 ② — 종료 시각이 같은 회의는 누가 먼저인가`
  - 원본(`code-mod-q2`): `while (j >= 0 && meetings[j].end > key.end)` 조건 한 줄과 "동점일 때 무슨 일이 일어나는가"라는 질문
  - 요구사항: 종료 시각이 같으면 **늦게 시작한 회의를 앞에** 두는 기준을 만들 것 / `if` 없이 조립으로 표현할 것
  - 정답(`code-mod-a2`): `byEndThenLateStart`. 해설에 "동점 규칙을 적지 않으면 결과가 입력 순서에 좌우된다 — 그 암묵적 규칙을 코드에 드러내는 것이 `thenComparing`이다"를 쓴다.
- 마무리 문단 경로: `examples/java/10-greedy-algorithms/ModernizeSolution.java`

- [ ] **Step 4: 뒤 섹션 번호 밀기 · 데이터 갱신 (10강 `11` → `12`)**

- [ ] **Step 5: 검증**

Run: `npm run build`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 6: 커밋**

```bash
git add examples/java/10-greedy-algorithms/ModernizeSolution.java algorithms/10-greedy-algorithms.html assets/js/algorithms-data.js data/algorithms.json
git commit -m "feat: 10강에 람다·스트림 수정 문제 추가"
```

---

### Task 7: 13강 — 카테고리 집계를 그룹핑으로

**Files:**
- Create: `examples/java/13-algorithm-project/ModernizeSolution.java`
- Modify: `algorithms/13-algorithm-project.html`
- Modify: `assets/js/algorithms-data.js` (13강 `9` → `10`), `data/algorithms.json`

**Interfaces:**
- Consumes: Task 2가 정한 섹션 구조
- Produces: 없음

- [ ] **Step 1: 정답 Java 파일 작성**

Create `examples/java/13-algorithm-project/ModernizeSolution.java`:

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 13강 「람다·스트림 수정 문제」 정답.
 *
 * Step4CategoryStats.java의 3단 누적(containsKey → put(0) → put(get + 1))을
 * 그룹핑으로 다시 쓴 것이다.
 *
 * 주의: 원본의 "재고 자산"(가격 x 재고의 long 누적)은 여기서 다루지 않는다.
 * 그 계산에 필요한 mapToLong은 추가 정보 문서가 다루지 않으므로,
 * 배우지 않은 문법을 쓰지 않기 위해 반복문 그대로 두는 것이 옳다.
 */
public class ModernizeSolution {

    static class Product {
        int code;
        String name;
        String category;
        int price;
        int stock;

        Product(int code, String name, String category, int price, int stock) {
            this.code = code;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        String summary() {
            return "[" + code + "] " + name + " | " + category
                    + " | " + price + "원 | 재고 " + stock + "개";
        }
    }

    static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(2005, "유선 키보드", "전자", 23000, 12));
        products.add(new Product(1003, "무선 마우스", "전자", 18000, 25));
        products.add(new Product(3010, "머그컵", "생활", 7000, 2));
        products.add(new Product(1007, "마우스 패드", "잡화", 4000, 18));
        products.add(new Product(2002, "USB 메모리", "전자", 9000, 5));
        products.add(new Product(3001, "텀블러", "생활", 12000, 30));
        products.add(new Product(1010, "노트북 파우치", "잡화", 15000, 3));
        products.add(new Product(2008, "웹캠", "전자", 45000, 4));
        return products;
    }

    /* ─────────── 이전: Step4CategoryStats의 3단 누적 ─────────── */

    static Map<String, Integer> countByCategoryLoop(List<Product> products) {
        Map<String, Integer> countByCategory = new HashMap<>();
        for (Product p : products) {
            if (!countByCategory.containsKey(p.category)) {
                countByCategory.put(p.category, 0);
            }
            countByCategory.put(p.category, countByCategory.get(p.category) + 1);
        }
        return countByCategory;
    }

    static Map<String, Double> averagePriceLoop(List<Product> products) {
        Map<String, Integer> sum = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();
        for (Product p : products) {
            sum.put(p.category, sum.getOrDefault(p.category, 0) + p.price);
            count.put(p.category, count.getOrDefault(p.category, 0) + 1);
        }
        Map<String, Double> average = new HashMap<>();
        for (String category : sum.keySet()) {
            average.put(category, (double) sum.get(category) / count.get(category));
        }
        return average;
    }

    static List<Product> lowStockLoop(List<Product> products) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.stock < 5) {
                result.add(p);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<Product> products = loadProducts();

        /* ─────────── 문제 ① 카테고리별 상품 수 ─────────── */
        System.out.println("== 문제 ① 카테고리별 상품 수 ==");

        Map<String, Long> countStream = products.stream()
                .collect(Collectors.groupingBy(p -> p.category, Collectors.counting()));

        Map<String, Integer> countLoop = countByCategoryLoop(products);
        System.out.println("  반복문 " + new TreeMap<>(countLoop));
        System.out.println("  스트림 " + new TreeMap<>(countStream));

        boolean sameCount = countLoop.size() == countStream.size();
        for (Map.Entry<String, Integer> e : countLoop.entrySet()) {
            Long fromStream = countStream.get(e.getKey());
            if (fromStream == null || fromStream != e.getValue().longValue()) {
                sameCount = false;
            }
        }
        System.out.println("  같은가 " + sameCount);

        /* ─────────── 문제 ② 카테고리별 평균 가격 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 카테고리별 평균 가격 ==");

        // 1단계: 카테고리로 묶는다.  2단계: 각 묶음 안에서 다시 스트림을 돌려 평균을 낸다.
        Map<String, List<Product>> grouped = products.stream()
                .collect(Collectors.groupingBy(p -> p.category));

        Map<String, Double> averageStream = new HashMap<>();
        for (Map.Entry<String, List<Product>> entry : grouped.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(p -> p.price)
                    .average()
                    .orElse(0);
            averageStream.put(entry.getKey(), avg);
        }

        Map<String, Double> averageLoopResult = averagePriceLoop(products);
        System.out.println("  반복문 " + new TreeMap<>(averageLoopResult));
        System.out.println("  스트림 " + new TreeMap<>(averageStream));

        boolean sameAverage = averageLoopResult.size() == averageStream.size();
        for (Map.Entry<String, Double> e : averageLoopResult.entrySet()) {
            Double fromStream = averageStream.get(e.getKey());
            if (fromStream == null || Math.abs(fromStream - e.getValue()) > 1e-9) {
                sameAverage = false;
            }
        }
        System.out.println("  같은가 " + sameAverage);

        /* ─────────── 문제 ③ 재고 부족 목록 ─────────── */
        System.out.println();
        System.out.println("== 문제 ③ 재고 5개 미만 ==");

        List<Product> lowStream = products.stream()
                .filter(p -> p.stock < 5)
                .toList();
        long lowCount = products.stream().filter(p -> p.stock < 5).count();

        List<Product> lowLoopResult = lowStockLoop(products);
        System.out.println("  반복문 " + lowLoopResult.size() + "종");
        for (Product p : lowLoopResult) {
            System.out.println("    " + p.summary());
        }
        System.out.println("  스트림 " + lowCount + "종");
        System.out.println("  같은가 " + (lowLoopResult.equals(lowStream)
                && lowLoopResult.size() == lowCount));

        System.out.println();
        System.out.println("집계는 어느 쪽도 O(n)이다. 13강의 성과는 '어떤 자료구조를 골랐는가'이지");
        System.out.println("'어떤 문법으로 적었는가'가 아니다.");
        System.out.println();
        System.out.println("참고: 원본의 '재고 자산'(가격 x 재고의 long 합)은 스트림으로 바꾸지 않았다.");
        System.out.println("      필요한 mapToLong을 추가 정보 문서에서 다루지 않기 때문이다.");
        System.out.println("      배우지 않은 문법을 끌어오느니 반복문을 두는 편이 낫다.");
    }
}
```

- [ ] **Step 2: 컴파일과 실행으로 검증**

```bash
cd examples/java/13-algorithm-project && javac -encoding UTF-8 ModernizeSolution.java && java ModernizeSolution
```
Expected: 세 "같은가" 줄이 모두 `true`. 전자 4종, 생활 2종, 잡화 2종. 재고 5개 미만 3종.

- [ ] **Step 3: 강의 HTML에 섹션 삽입**

Task 2 Step 4의 골격을 쓰되:

- 도입 문단 링크: 람다식과 자바 스트림 **양쪽** 모두
- 경고 상자 본문: "13강은 지금까지 배운 것을 조합하는 강의입니다. 문법을 바꿔도 **정렬은 O(n log n), 탐색은 O(log n), 집계는 O(n)** 그대로입니다. 자료구조 선택이 성능을 정하고, 문법은 읽기 쉬움을 정합니다."
- 문제 ① 제목: `수정 문제 ① — containsKey · put · get 3단 누적을 한 줄로`
  - 원본(`code-mod-q1`): `Step4CategoryStats.java`의 `for (Product p : products) { if (!countByCategory.containsKey ... }` 블록
  - 요구사항: 카테고리별 상품 수를 한 줄로 / `categoryOrder` 보조 리스트 없이 / 힌트는 `#sec-collect`의 `groupingBy` + `counting()`
  - 정답(`code-mod-a1`): `countStream`. 해설에 값 타입이 `Long`이 되는 이유와, **출력 순서를 보장하려면 `TreeMap`으로 감싸거나 정렬을 명시해야 한다**는 점(원본이 `categoryOrder`로 해결하던 문제)을 쓴다.
- 문제 ② 제목: `수정 문제 ② — 카테고리별 평균 가격 (스트림 안의 스트림)`
  - 원본(`code-mod-q2`): 위 Java 파일의 `averagePriceLoop`
  - 요구사항: `groupingBy`로 묶은 뒤 **각 묶음에서 다시 평균을 낼 것** / 힌트는 `mapToInt` + `average()` + `orElse`
  - 정답(`code-mod-a2`): `grouped` + 평균 계산 루프. 해설에 "`Collectors`에는 평균을 바로 내주는 도구도 있지만, 여기서는 **이미 배운 `mapToInt().average()`를 그룹마다 한 번 더 쓰는 방식**으로 풉니다 — 스트림을 겹쳐 쓰는 감각을 익히는 것이 목적입니다"를 쓴다.
- 문제 ③ 제목: `수정 문제 ③ — 재고 부족 목록`
  - 원본(`code-mod-q3`): `Step4CategoryStats.java`의 재고 5개 미만 검색 블록
  - 요구사항: 목록과 개수를 각각 구할 것 / 힌트는 `filter` + `toList()` + `count()`
  - 정답(`code-mod-a3`): `lowStream` / `lowCount`. 해설에 **스트림은 한 번만 소비되므로 목록과 개수를 모두 원하면 스트림을 두 번 만들거나 목록에서 `size()`를 읽어야 한다**(스트림 문서 "자주 하는 실수 ①")를 쓴다.
- 마무리 문단에 **바꾸지 않은 것**을 명시: 원본의 "재고 자산" 집계는 `mapToLong`이 필요해 추가 정보 문서 범위를 벗어나므로 반복문 그대로 둔다. `<div class="note-box note-box--tip">`으로 감싼다.
- 마무리 문단 경로: `examples/java/13-algorithm-project/ModernizeSolution.java`

- [ ] **Step 4: 뒤 섹션 번호 밀기 · 데이터 갱신 (13강 `9` → `10`)**

- [ ] **Step 5: 검증**

Run: `npm run build`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 6: 커밋**

```bash
git add examples/java/13-algorithm-project/ModernizeSolution.java algorithms/13-algorithm-project.html assets/js/algorithms-data.js data/algorithms.json
git commit -m "feat: 13강에 람다·스트림 수정 문제 추가"
```

---

### Task 8: 검증 스크립트에 범위·개수·어휘 검사 추가

이 규칙들이 스크립트에 없으면, 나중에 누군가 수정 문제를 손볼 때 조용히 깨진다. 어휘 잠금이 이번 요청("보충 자료에 있는 것만 쓸 것")을 앞으로도 지켜 준다.

**Files:**
- Modify: `scripts/validate.mjs` (5번 "Java 예제 폴더" 절 근처)

**Interfaces:**
- Consumes: Task 2~7이 만든 6개 `sec-modernize` 섹션과 `ModernizeSolution.java`
- Produces: 없음 (최종 게이트)

- [ ] **Step 1: 검사 추가**

`scripts/validate.mjs`의 `/* ---------- 5. Java 예제 폴더 ---------- */` 블록 안 `for (const lesson of lessons)` 루프에서 `javaFiles.length < 4` 경고 줄을 아래로 교체한다.

```javascript
    /* 강의 카드에 찍히는 예제 수는 실제 파일 수와 같아야 한다.
       수정 문제로 ModernizeSolution.java가 늘면서 어긋나기 쉬워졌다. */
    if (javaFiles.length !== lesson.examples) {
        fail(`examples/java/${num}-${lesson.id}: .java 파일이 ${javaFiles.length}개인데 ` +
            `데이터에는 examples: ${lesson.examples}로 적혀 있음`);
    }
```

그 아래(같은 5번 절 끝, 보충 자료 검사 앞)에 다음을 추가한다.

```javascript
/* ---------- 6. 람다·스트림 수정 문제 ----------
   커리큘럼 밖 문법(람다·스트림)을 끌어오는 자리이므로 규칙이 셋 있다.
   (1) 정해진 6개 강의에만 있어야 한다 — 재귀·DP 강의로 번지면
       "람다를 쓰면 무조건 좋다"는 오해를 준다.
   (2) 정답을 실제로 돌려 볼 수 있어야 한다(ModernizeSolution.java).
   (3) 정답에 쓰는 API는 추가 정보 문서가 가르친 것뿐이어야 한다.
       가르치지 않은 API가 정답에 나오면 학생은 풀 수가 없다. */
const MODERNIZE_LESSONS = new Set([
    "arrays-and-lists",
    "brute-force-string-hash",
    "sorting-algorithms",
    "search-algorithms",
    "greedy-algorithms",
    "algorithm-project"
]);

/* 추가 정보(람다식·자바 스트림) 문서가 다루지 않는 API */
const UNTAUGHT_APIS = [
    ".merge(", "computeIfAbsent", "averagingInt", "averagingDouble",
    "summingInt", "summingLong", "summarizingInt",
    "IntStream.of", "flatMap", "takeWhile", "dropWhile",
    "parallelStream", "naturalOrder", "mapToLong"
];

for (const lesson of lessons) {
    const num = String(lesson.order).padStart(2, "0");
    const html = existsSync(join(ROOT, lesson.path))
        ? readFileSync(join(ROOT, lesson.path), "utf8")
        : "";
    const solutionPath = join(ROOT, "examples", "java", `${num}-${lesson.id}`,
        "ModernizeSolution.java");
    const hasSection = html.includes('id="sec-modernize"');
    const hasSolution = existsSync(solutionPath);

    if (MODERNIZE_LESSONS.has(lesson.id)) {
        if (!hasSection) {
            fail(`${lesson.path}: 수정 문제 섹션(sec-modernize) 누락`);
        }
        if (!hasSolution) {
            fail(`examples/java/${num}-${lesson.id}: ModernizeSolution.java 없음`);
        }

        /* 어휘 잠금 — 섹션 안의 코드와 정답 파일 양쪽을 본다 */
        const sectionMatch = html.match(/id="sec-modernize"[\s\S]*?\n {12}<\/section>/);
        const sources = [];
        if (sectionMatch) sources.push([`${lesson.path} (sec-modernize)`, sectionMatch[0]]);
        if (hasSolution) {
            sources.push([`examples/java/${num}-${lesson.id}/ModernizeSolution.java`,
                readFileSync(solutionPath, "utf8")]);
        }
        for (const [label, source] of sources) {
            for (const api of UNTAUGHT_APIS) {
                if (source.includes(api)) {
                    fail(`${label}: 추가 정보 문서가 가르치지 않는 API — ${api}`);
                }
            }
        }
    } else {
        if (hasSection) {
            fail(`${lesson.path}: 수정 문제 대상 강의가 아닌데 sec-modernize가 있음`);
        }
        if (hasSolution) {
            fail(`examples/java/${num}-${lesson.id}: 대상 강의가 아닌데 ModernizeSolution.java가 있음`);
        }
    }
}
```

> `UNTAUGHT_APIS`에 `mapToLong`이 들어 있으므로 13강 정답 파일의 **주석에도** 그 단어를 쓸 수 없다. Task 7의 주석은 "필요한 mapToLong을"이라고 적혀 있으니, 이 검사를 넣은 뒤 실패하면 주석 문구를 "long 전용 변환 연산을"로 바꾼다. (HTML 해설도 동일.)

- [ ] **Step 2: 검사가 통과하는지 확인**

Run: `npm run build`
Expected: `모든 검증 통과 ✓` — 실패하면 Step 1의 주석 문구를 위 지침대로 고친다.

- [ ] **Step 3: 검사가 실제로 잡아내는지 확인 (일부러 깨뜨리기)**

```bash
cp examples/java/02-arrays-and-lists/ModernizeSolution.java examples/java/07-recursion-and-backtracking/ModernizeSolution.java
npm run build
```
Expected: FAIL — `examples/java/07-recursion-and-backtracking: 대상 강의가 아닌데 ModernizeSolution.java가 있음`

```bash
rm examples/java/07-recursion-and-backtracking/ModernizeSolution.java
npm run build
```
Expected: `모든 검증 통과 ✓`

- [ ] **Step 4: 어휘 검사도 실제로 잡아내는지 확인**

`examples/java/02-arrays-and-lists/ModernizeSolution.java` 맨 끝 주석에 임시로 `// flatMap` 한 줄을 넣고:

Run: `npm run build`
Expected: FAIL — `... ModernizeSolution.java: 추가 정보 문서가 가르치지 않는 API — flatMap`

그 줄을 지우고 다시:

Run: `npm run build`
Expected: `모든 검증 통과 ✓`

- [ ] **Step 5: 커밋**

```bash
git add scripts/validate.mjs
git commit -m "test: 수정 문제의 적용 범위·예제 수·어휘 잠금 검사 추가"
```

---

### Task 9: 최종 검증

**Files:** 없음 (검증만)

- [ ] **Step 1: 전체 검증 스크립트**

Run: `npm run build`
Expected: `검사한 강의: 13개, 보충 자료: 2개` + `모든 검증 통과 ✓`, 경고 0건

- [ ] **Step 2: 6개 정답 파일 전부 컴파일·실행**

```bash
for d in 02-arrays-and-lists 03-brute-force-string-hash 04-sorting-algorithms 05-search-algorithms 10-greedy-algorithms 13-algorithm-project; do
  echo "=== $d ==="
  (cd "examples/java/$d" && javac -encoding UTF-8 ModernizeSolution.java && java ModernizeSolution | grep -i "같은가")
done
```
Expected: 모든 `같은가` 줄이 `true`. `false`가 하나라도 있으면 그 강의의 정답이 원본과 다른 결과를 내는 것이므로 반드시 고친다.

- [ ] **Step 3: 컴파일 산출물 정리**

Run: `git status --short`
Expected: `.class` 파일이 새로 추적되지 않는지 확인. `.gitignore` 확인 후 필요하면 추가한다.

- [ ] **Step 4: 섹션 번호 육안 확인**

Run: `grep -o 'section-no">[0-9]*' algorithms/02-arrays-and-lists.html | tail -5`
Expected: `18`, `19`, `20`, `21`로 끝난다(중복·건너뜀 없음). 나머지 5개 강의도 같은 방식으로 확인.

- [ ] **Step 5: 대상이 아닌 강의는 그대로인지 확인**

Run: `grep -l "sec-modernize" algorithms/*.html | wc -l`
Expected: `6`

- [ ] **Step 6: 최종 커밋**

```bash
git add -A
git commit -m "docs: 람다·스트림 수정 문제 설계·계획 문서 추가"
```

---

## Self-Review 결과

**Spec coverage** — 스펙의 모든 절이 Task에 대응한다: 어휘 잠금(Global Constraints + Task 8), 보충 자료 틈 메우기(Task 1), 6개 강의 문제(Task 2~7), 파일 변경 5종(Task 2~8), 검증(Task 9), 하지 않는 것(Task 8의 else 분기가 강제).

**주의할 상호작용** — Task 8의 `UNTAUGHT_APIS`에 `mapToLong`과 `naturalOrder`가 들어 있다. 13강 정답의 주석과 해설이 `mapToLong`을 **언급**하면 검사에 걸린다. Task 8 Step 1의 주석이 이 점을 명시하고 대체 문구를 지정해 두었다.

**타입 일관성** — `countStream`은 `Map<String, Long>`(`counting()`이 `long`), `countLoop`은 `Map<String, Integer>`이므로 비교할 때 `longValue()`로 맞춘다. 3강·13강 정답 코드가 모두 이 방식을 쓴다. `stat.getSum()`은 `long`, `sumLoop`은 `int`이므로 `==` 비교 시 자동 확대 변환된다.
