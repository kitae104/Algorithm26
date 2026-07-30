/*
 * 1강 구현 2단계: 핵심 연산 하나 구현
 * 합계 알고리즘의 핵심 연산은 "누적 변수에 값 하나를 더하는 것"이다.
 * 반복문 없이 딱 한 번만 수행해 본다.
 */
public class Step2AddOnce {
    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78};

        int sum = 0;              // 누적 변수: 지금까지의 합을 기억한다

        sum = sum + scores[0];    // 핵심 연산: 값 하나를 누적한다

        System.out.println("첫 번째 점수를 더한 뒤 sum = " + sum);
    }
}
