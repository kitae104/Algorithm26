import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordFrequencySolution {

    /** 1단계: 문장을 공백 기준으로 잘라 단어 배열을 만든다. */
    static String[] splitWords(String post) {
        return post.split(" ");
    }

    /** 2단계: HashMap으로 각 단어의 등장 횟수를 센다. O(n) */
    static Map<String, Integer> countFrequencies(String[] words) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            // 처음 보는 단어면 0에서, 이미 본 단어면 지금까지의 횟수에서 1을 더한다
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
    }

    /** 3단계: 두 번 이상 등장한 단어 목록을 첫 등장 순서대로 만든다. O(n) */
    static List<String> findDuplicates(String[] words) {
        List<String> duplicates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String word : words) {
            if (!seen.add(word)) {                 // add가 false = 이미 본 단어 = 중복
                if (!duplicates.contains(word)) {  // 같은 단어를 두 번 넣지 않는다
                    duplicates.add(word);
                }
            }
        }
        return duplicates;
    }

    /** 4단계: 가장 많이 등장한 단어를 찾는다. (2강의 최댓값 찾기 패턴) O(n) */
    static String findMostFrequent(String[] words, Map<String, Integer> freq) {
        String best = null;
        for (String word : words) {
            if (best == null || freq.get(word) > freq.get(best)) {
                best = word;   // 더 자주 등장한 단어를 만나면 갈아 끼운다
            }
        }
        return best;
    }

    public static void main(String[] args) {
        String post = "자바 공부 시작 자바 문법 공부 자바 프로젝트 시작";
        String[] words = splitWords(post);

        System.out.println("게시글: " + post);
        System.out.println("단어 수: " + words.length);
        System.out.println();

        Map<String, Integer> freq = countFrequencies(words);

        System.out.println("== 단어 빈도 ==");
        Set<String> printed = new HashSet<>();
        for (String word : words) {
            if (printed.add(word)) {   // 첫 등장 순서대로 한 번씩만 출력
                System.out.println("  " + word + " : " + freq.getOrDefault(word, 0) + "회");
            }
        }
        System.out.println();

        System.out.println("== 두 번 이상 등장한 단어 ==");
        System.out.println("  " + findDuplicates(words));
        System.out.println();

        String most = findMostFrequent(words, freq);
        System.out.println("== 가장 많이 등장한 단어 ==");
        System.out.println("  " + most + " (" + (most == null ? 0 : freq.getOrDefault(most, 0)) + "회)");
    }
}
