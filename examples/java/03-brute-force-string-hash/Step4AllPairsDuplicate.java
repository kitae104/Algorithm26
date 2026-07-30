public class Step4AllPairsDuplicate {
    public static void main(String[] args) {
        String[] words = {"apple", "kiwi", "apple", "plum", "kiwi", "grape"};

        int compareCount = 0;   // 비교 연산 실행 횟수

        System.out.println("단어 수 n = " + words.length);
        System.out.println("모든 쌍 (i, j)을 비교하는 완전 탐색으로 중복 찾기:");

        // 이중 반복문: i번째 단어를 그 뒤의 모든 단어 j와 비교한다
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                compareCount++;
                if (words[i].equals(words[j])) {
                    System.out.println("  중복 발견: words[" + i + "]과 words[" + j
                            + "]가 같음 (\"" + words[i] + "\")");
                }
            }
        }

        System.out.println("총 비교 횟수 = " + compareCount
                + " (= n(n-1)/2 = " + words.length + "×" + (words.length - 1) + "÷2)");
    }
}
