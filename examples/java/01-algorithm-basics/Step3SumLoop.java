/*
 * 1강 구현 3단계: 반복 구조 추가
 * 2단계의 핵심 연산(누적)을 반복문으로 감싸서
 * 배열 전체의 합계를 구하는 알고리즘을 완성한다.
 */
public class Step3SumLoop {
    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78};

        int sum = 0;

        // 핵심 연산을 배열 길이만큼 반복한다
        for (int i = 0; i < scores.length; i++) {
            sum = sum + scores[i];
        }

        System.out.println("점수 합계 = " + sum);
        System.out.println("평균 = " + (double) sum / scores.length);
    }
}
