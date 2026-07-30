public class Step3CountOneWord {
    public static void main(String[] args) {
        String sentence = "apple banana apple orange banana apple";
        String[] words = sentence.split(" ");
        String target = "apple";

        int count = 0;          // target이 등장한 횟수
        int compareCount = 0;   // 비교 연산 실행 횟수 (1강의 카운터 기법)

        // 단어 배열을 한 번 순회하며 target과 비교한다 (단일 루프)
        for (int i = 0; i < words.length; i++) {
            compareCount++;
            if (words[i].equals(target)) {  // 문자열 비교는 반드시 equals!
                count++;
            }
        }

        System.out.println("단어 \"" + target + "\"의 등장 횟수 = " + count);
        System.out.println("비교 연산 횟수 = " + compareCount + " (단어 수 n = " + words.length + ")");
    }
}
