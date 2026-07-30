/*
 * 1강 구현 4단계: 실행 횟수 세기
 * "이 알고리즘은 얼마나 일을 많이 하는가?"를 숫자로 확인하기 위해
 * 핵심 연산(누적)이 몇 번 실행되는지 카운터로 센다.
 * 데이터가 n개면 누적 연산도 정확히 n번 — 이것이 O(n)의 의미다.
 */
public class Step4CountOperations {
    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78};

        int sum = 0;
        int operationCount = 0;   // 핵심 연산 실행 횟수

        for (int i = 0; i < scores.length; i++) {
            sum = sum + scores[i];
            operationCount++;     // 누적 연산을 할 때마다 1 증가
        }

        System.out.println("점수 합계 = " + sum);
        System.out.println("데이터 개수 n = " + scores.length);
        System.out.println("누적 연산 실행 횟수 = " + operationCount);
    }
}
