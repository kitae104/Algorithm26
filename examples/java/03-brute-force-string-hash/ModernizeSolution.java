import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 3강 「람다·스트림 수정 문제」 정답.
 *
 * WordAnalysisComplete.java의 빈도 누적을 스트림으로 다시 쓴 것이다.
 * 해시를 쓴다는 사실은 양쪽 모두 같다 — O(n)이라는 성과는 문법과 무관하다.
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

    /** 가장 많이 나온 단어 하나 (동점이면 문장에서 먼저 만난 쪽) */
    static String mostFrequentLoop(String[] words, Map<String, Integer> freq) {
        String best = null;
        for (String word : words) {
            if (best == null || freq.get(word) > freq.get(best)) {
                best = word;
            }
        }
        return best;
    }

    /** 2번 이상 나온 단어를 빈도 내림차순, 동점이면 사전순으로 (4강 삽입 정렬) */
    static List<String> repeatedLoop(Map<String, Integer> freq) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            if (e.getValue() >= 2) {
                result.add(e.getKey());
            }
        }
        for (int i = 1; i < result.size(); i++) {
            String key = result.get(i);
            int j = i - 1;
            while (j >= 0 && comesAfter(result.get(j), key, freq)) {
                result.set(j + 1, result.get(j));
                j--;
            }
            result.set(j + 1, key);
        }
        return result;
    }

    /** a가 b보다 뒤에 와야 하면 true */
    static boolean comesAfter(String a, String b, Map<String, Integer> freq) {
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
        Map<String, Long> freqStream = Arrays.stream(words)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        System.out.println("== 문제 ① 단어 빈도 ==");
        System.out.println("  반복문 " + new TreeMap<>(freqLoop));
        System.out.println("  스트림 " + new TreeMap<>(freqStream));

        boolean sameFreq = freqLoop.size() == freqStream.size();
        for (Map.Entry<String, Integer> e : freqLoop.entrySet()) {
            Long fromStream = freqStream.get(e.getKey());
            if (fromStream == null || fromStream != e.getValue().longValue()) {
                sameFreq = false;
            }
        }
        System.out.println("  같은가 " + sameFreq);

        /* ─────────── 문제 ② 최빈 단어와 반복 단어 정렬 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 최빈 단어와 2회 이상 목록 ==");

        String bestStream = freqStream.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        String bestLoop = mostFrequentLoop(words, freqLoop);
        System.out.println("  최빈 단어 반복문 " + bestLoop + " | 스트림 " + bestStream
                + " | 같은가 " + bestLoop.equals(bestStream));

        // 빈도 내림차순, 동점이면 사전순 — 기준을 조립해서 만든다
        Comparator<Map.Entry<String, Long>> byCountDescThenWord =
                Map.Entry.<String, Long>comparingByValue()
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
        System.out.println("3강의 성과는 이중 반복문 O(n^2)을 해시 O(n)으로 바꾼 것이고,");
        System.out.println("그 성과는 스트림으로 쓰든 반복문으로 쓰든 그대로다.");
    }
}
