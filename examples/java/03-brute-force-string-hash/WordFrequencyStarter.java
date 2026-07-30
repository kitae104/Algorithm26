import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordFrequencyStarter {

    /** 1단계: 문장을 공백 기준으로 잘라 단어 배열을 만든다. (완성되어 있음) */
    static String[] splitWords(String post) {
        return post.split(" ");
    }

    /** 2단계: HashMap으로 각 단어의 등장 횟수를 센다. */
    static Map<String, Integer> countFrequencies(String[] words) {
        Map<String, Integer> freq = new HashMap<>();
        // TODO 1: words를 순회하며 freq.getOrDefault(단어, 0) + 1 로 빈도를 누적하세요.
        return freq;
    }

    /** 3단계: 두 번 이상 등장한 단어 목록을 첫 등장 순서대로 만든다. */
    static List<String> findDuplicates(String[] words) {
        List<String> duplicates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        // TODO 2: words를 순회하며 seen.add(단어)가 false인 단어(= 이미 본 단어)를
        //         duplicates에 추가하세요. (같은 단어를 두 번 넣지 않도록 주의!)
        return duplicates;
    }

    /** 4단계: 가장 많이 등장한 단어를 찾는다. (2강의 최댓값 찾기 패턴) */
    static String findMostFrequent(String[] words, Map<String, Integer> freq) {
        String best = null;
        // TODO 3: words를 순회하며 freq에서 빈도를 꺼내,
        //         지금까지의 best보다 크면 best를 갈아 끼우세요.
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
