/*
 * 1강 구현 1단계: 입력 데이터 준비
 * 처리할 데이터(학생 점수)를 배열에 담고 그대로 출력해 본다.
 * 아직 알고리즘은 없다 — "입력"이 무엇인지부터 눈으로 확인한다.
 */
public class Step1ScoreData {
    public static void main(String[] args) {
        // 학생 5명의 점수 (입력 데이터)
        int[] scores = {72, 85, 90, 66, 78};

        System.out.println("학생 수: " + scores.length);

        // 배열의 내용을 처음부터 끝까지 출력한다
        for (int i = 0; i < scores.length; i++) {
            System.out.println("scores[" + i + "] = " + scores[i]);
        }
    }
}
