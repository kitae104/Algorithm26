import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WordAnalysisComplete {

    /** 방법 A: 이중 반복문(완전 탐색)으로 중복 단어 목록을 찾는다. 비교는 n(n-1)/2번. */
    static List<String> findDuplicatesByPairs(String[] words) {
        List<String> duplicates = new ArrayList<>();
        int compareCount = 0;

        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                compareCount++;                       // 핵심 연산: 문자열 비교
                if (words[i].equals(words[j]) && !duplicates.contains(words[i])) {
                    duplicates.add(words[i]);         // 처음 확인된 중복만 기록
                }
            }
        }

        System.out.println("  [방법 A: 이중 반복문] 비교 횟수 = " + compareCount);
        return duplicates;
    }

    /** 방법 B: HashSet으로 중복 단어 목록을 찾는다. 집합 검사는 n번. */
    static List<String> findDuplicatesByHashSet(String[] words) {
        List<String> duplicates = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();       // 지금까지 본 단어들의 집합
        int checkCount = 0;

        for (String word : words) {
            checkCount++;                             // 핵심 연산: 집합 검사(add)
            if (!seen.add(word)) {                    // add가 false = 이미 있던 단어 = 중복!
                if (!duplicates.contains(word)) {
                    duplicates.add(word);
                }
            }
        }

        System.out.println("  [방법 B: HashSet]     검사 횟수 = " + checkCount);
        return duplicates;
    }

    /** 단어 빈도: HashMap으로 각 단어의 등장 횟수를 센다. 순회는 n번. */
    static Map<String, Integer> countFrequencies(String[] words) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            // 처음 보는 단어면 0에서, 이미 본 단어면 지금까지의 횟수에서 1을 더한다
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
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
        Map<String, Integer> freq = countFrequencies(words);
        // HashMap 자체는 순서를 보장하지 않으므로, 첫 등장 순서대로 출력한다
        HashSet<String> printed = new HashSet<>();
        for (String word : words) {
            if (printed.add(word)) {
                System.out.println("  " + word + " : " + freq.get(word) + "회");
            }
        }
        System.out.println();

        System.out.println("n = " + n + "일 때: 완전 탐색은 " + (n * (n - 1) / 2)
                + "번, 해시는 " + n + "번. n이 커질수록 차이는 극적으로 벌어진다.");
    }
}
