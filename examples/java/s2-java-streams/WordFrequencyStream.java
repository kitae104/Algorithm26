import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 3강 WordFrequencySolution을 스트림으로 다시 쓴다.
 *
 * 함께 확인할 것 하나 — 스트림으로 바꾸면서 "순서"가 조용히 달라질 수 있다.
 * 3강에서 배운 "HashMap의 출력 순서에 주의"가 여기서도 그대로 적용된다.
 */
public class WordFrequencyStream {

    /* ── 3강의 반복문 버전 ─────────────────────────────────── */

    static Map<String, Integer> countLoop(String[] words) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
    }

    /** 첫 등장 순서를 유지한다 — 이것이 스트림 버전과 달라지는 지점이다. */
    static List<String> duplicatesLoop(String[] words) {
        List<String> duplicates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String word : words) {
            if (!seen.add(word) && !duplicates.contains(word)) {
                duplicates.add(word);
            }
        }
        return duplicates;
    }

    static String mostFrequentLoop(String[] words, Map<String, Integer> freq) {
        String best = null;
        for (String word : words) {
            if (best == null || freq.get(word) > freq.get(best)) {
                best = word;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        String post = "자바 공부 시작 자바 문법 공부 자바 프로젝트 시작";
        String[] words = post.split(" ");

        System.out.println("게시글: " + post);
        System.out.println("단어 수: " + words.length);
        System.out.println();

        /* ── 스트림 버전 ─────────────────────────────────── */
        Map<String, Long> freq = Arrays.stream(words)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        String best = freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())        // 4강 Comparator가 여기서 쓰인다
                .map(Map.Entry::getKey)
                .orElse(null);

        List<String> duplicates = freq.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        System.out.println("[스트림 버전]");
        System.out.println("  빈도표     : " + freq);
        System.out.println("  최다 단어  : " + best);
        System.out.println("  중복 단어  : " + duplicates + "  (사전순)");
        System.out.println();

        /* ── 반복문 버전과 비교 ───────────────────────────── */
        Map<String, Integer> freqLoop = countLoop(words);
        List<String> dupLoop = duplicatesLoop(words);

        System.out.println("[반복문 버전]");
        System.out.println("  빈도표     : " + freqLoop);
        System.out.println("  최다 단어  : " + mostFrequentLoop(words, freqLoop));
        System.out.println("  중복 단어  : " + dupLoop + "  (첫 등장 순)");
        System.out.println();

        boolean sameCounts = freqLoop.keySet().stream()
                .allMatch(k -> freq.get(k).longValue() == freqLoop.get(k).longValue());
        System.out.println("[검증]");
        System.out.println("  두 빈도표의 값이 모두 같은가: " + sameCounts);
        System.out.println("  중복 단어 목록이 같은가      : " + duplicates.equals(dupLoop));
        System.out.println("  → 값은 같지만 순서가 다를 수 있습니다. HashMap은 순서를 보장하지 않습니다.");
        System.out.println();

        /* ── 순서가 요구사항이라면 자료구조를 지정한다 ───── */
        Map<String, Long> ordered = Arrays.stream(words)
                .collect(Collectors.groupingBy(
                        w -> w,
                        LinkedHashMap::new,          // 첫 등장 순서를 유지하는 맵으로 모은다
                        Collectors.counting()));

        List<String> dupOrdered = ordered.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey)
                .toList();

        System.out.println("[LinkedHashMap으로 첫 등장 순서 유지]");
        System.out.println("  빈도표    : " + ordered);
        System.out.println("  중복 단어 : " + dupOrdered);
        System.out.println("  반복문 버전과 순서까지 같은가: " + dupOrdered.equals(dupLoop));
    }
}
