import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeAnalysisApplication {

    /** 단어 빈도를 세면서, 처음 만난 단어를 등장 순서대로 order 목록에 기록한다. */
    static Map<String, Integer> countFrequencies(String[] words, List<String> order) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            if (!freq.containsKey(word)) {
                order.add(word);   // 처음 보는 단어의 등장 순서를 기억한다
            }
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }
        return freq;
    }

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

    /** 회문 판별 (PalindromeTrace와 같은 알고리즘, 추적 출력만 없음) */
    static boolean isPalindrome(String word) {
        int left = 0;
        int right = word.length() - 1;
        while (left < right) {
            if (word.charAt(left) != word.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public static void main(String[] args) {
        String notice = "시험 일정 안내 시험 범위 안내 시험 일정 준비 안내 제출 마감 안내";
        String[] words = notice.split(" ");

        System.out.println("공지사항: " + notice);
        System.out.println("단어 수: " + words.length);
        System.out.println();

        List<String> order = new ArrayList<>();
        Map<String, Integer> freq = countFrequencies(words, order);

        System.out.println("== 단어 빈도 (첫 등장 순서) ==");
        for (String word : order) {
            System.out.println("  " + word + " : " + freq.get(word) + "회");
        }

        // entrySet 순회로 전체 등장 횟수를 다시 세어 검증한다
        int total = 0;
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            total += entry.getValue();
        }
        System.out.println("  (검증) entrySet 순회로 합산한 전체 단어 수 = " + total);
        System.out.println();

        System.out.println("== 빈도 상위 3개 단어 ==");
        List<String> top3 = topWords(freq, order, 3);
        for (int rank = 0; rank < top3.size(); rank++) {
            String word = top3.get(rank);
            System.out.println("  " + (rank + 1) + "위: " + word + " (" + freq.get(word) + "회)");
        }
        System.out.println();

        System.out.println("== 회문 아이디 검출 ==");
        String[] memberIds = {"ana", "minsu", "level", "kiwi", "noon"};
        for (String id : memberIds) {
            if (isPalindrome(id)) {
                System.out.println("  " + id + " → 회문 아이디입니다!");
            } else {
                System.out.println("  " + id + " → 회문이 아닙니다.");
            }
        }
    }
}
