public class Step3SumAverage {
    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        int sum = 0;              // 1강의 누적 패턴: sum = sum + ... 을 그대로 사용한다
        int operationCount = 0;   // 1강의 연산 카운터 기법도 그대로 사용한다

        for (int i = 0; i < scores.length; i++) {
            sum = sum + scores[i];   // 핵심 연산: 원소 하나를 누적
            operationCount++;
        }

        double average = (double) sum / scores.length;   // int / int 함정 주의!

        System.out.println("합계 = " + sum);
        System.out.println("평균 = " + average);
        System.out.println("누적 연산 실행 횟수 = " + operationCount
                + " (데이터 " + scores.length + "개 → 연산 " + operationCount + "번, O(n))");
    }
}
