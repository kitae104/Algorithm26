import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 3강 「람다·스트림 변경」.
 *
 * WordAnalysisComplete.java(실습 코드)를 같은 결과가 나오도록 람다와 스트림으로 다시 쓴 것이다.
 */
public class ModernizeSolution {

    /** 방법 A: 모든 쌍 (i, j)를 비교해 중복 단어 목록을 찾는다. */
    static List<String> findDuplicatesByPairs(String[] words) {
        int n = words.length;

        List<String> duplicates = IntStream.range(0, n)
                .boxed()
                .filter(i -> IntStream.range(i + 1, n)
                        .anyMatch(j -> words[i].equals(words[j])))
                .map(i -> words[i])
                .distinct()
                .toList();

        int pairCount = IntStream.range(0, n).map(i -> n - 1 - i).sum();
        System.out.println("  [방법 A: 이중 반복문] 비교 쌍의 수 = " + pairCount);
        return duplicates;
    }

    /** 방법 B: HashSet으로 중복 단어 목록을 찾는다. */
    static List<String> findDuplicatesByHashSet(String[] words) {
        Set<String> seen = new HashSet<>();

        List<String> duplicates = Arrays.stream(words)
                .filter(word -> !seen.add(word))
                .distinct()
                .toList();

        System.out.println("  [방법 B: HashSet]     검사 횟수 = " + words.length);
        return duplicates;
    }

    /** 단어 빈도: 단어 자신을 기준으로 묶어 개수를 센다. */
    static Map<String, Long> countFrequencies(String[] words) {
        return Arrays.stream(words)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
    }

    public static void main(String[] args) {
        String sentence = "apple banana apple orange banana apple kiwi orange plum kiwi";
        String[] words = sentence.split(" ");
        int n = words.length;

        System.out.println("문장: " + sentence);
        System.out.println("단어 수 n = " + n);
        System.out.println();

        System.out.println("== 1. 중복 단어 찾기: 완전 탐색 vs HashSet ==");
        List<String> dupA = findDuplicatesByPairs(words);
        List<String> dupB = findDuplicatesByHashSet(words);
        System.out.println("  방법 A 결과: " + dupA);
        System.out.println("  방법 B 결과: " + dupB);
        System.out.println("  두 방법의 결과 일치 = " + dupA.equals(dupB));
        System.out.println();

        System.out.println("== 2. 단어 빈도 (HashMap) ==");
        Map<String, Long> freq = countFrequencies(words);
        Arrays.stream(words)
                .distinct()
                .forEach(word -> System.out.println("  " + word + " : " + freq.get(word) + "회"));
        System.out.println();

        System.out.println("n = " + n + "일 때: 완전 탐색은 " + (n * (n - 1) / 2)
                + "번, 해시는 " + n + "번. n이 커질수록 차이는 극적으로 벌어진다.");
    }
}
