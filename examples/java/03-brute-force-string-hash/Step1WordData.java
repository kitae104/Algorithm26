public class Step1WordData {
    public static void main(String[] args) {
        // 분석할 문장 (입력 데이터)
        String sentence = "apple banana apple orange banana apple";

        // split(" "): 공백을 기준으로 문장을 잘라 단어 배열을 만든다
        String[] words = sentence.split(" ");

        System.out.println("문장: " + sentence);
        System.out.println("문장 길이(문자 수): " + sentence.length());
        System.out.println("단어 수: " + words.length);

        // 단어 배열을 처음부터 끝까지 출력한다 (2강의 배열 순회와 같은 패턴)
        for (int i = 0; i < words.length; i++) {
            System.out.println("words[" + i + "] = " + words[i]);
        }
    }
}
