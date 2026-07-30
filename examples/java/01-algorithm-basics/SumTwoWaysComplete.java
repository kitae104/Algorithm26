/*
 * 1강 완성 코드: 같은 문제, 두 가지 알고리즘
 * "1부터 n까지의 합"을
 *   방법 A: 반복문으로 하나씩 더하기      (연산 n번, O(n))
 *   방법 B: 가우스 공식 n * (n + 1) / 2  (연산 1번, O(1))
 * 두 방법의 결과가 같은지, 실행 횟수는 얼마나 다른지 비교한다.
 */
public class SumTwoWaysComplete {

    /** 방법 A: 1부터 n까지 반복문으로 더한다. 누적 연산이 n번 실행된다. */
    static long sumByLoop(int n) {
        long sum = 0;
        for (int i = 1; i <= n; i++) {
            sum = sum + i;
        }
        return sum;
    }

    /** 방법 A의 핵심 연산 실행 횟수: 데이터가 n개면 정확히 n번 */
    static long loopOperationCount(int n) {
        return n;
    }

    /** 방법 B: 가우스 공식. n이 아무리 커도 계산은 한 번이다. */
    static long sumByFormula(int n) {
        return (long) n * (n + 1) / 2;
    }

    public static void main(String[] args) {
        int[] sizes = {10, 100, 1000, 10000, 100000};

        System.out.println("n        | 반복문 결과   | 공식 결과     | 반복문 연산 횟수 | 공식 연산 횟수");
        System.out.println("---------+--------------+--------------+-----------------+---------------");

        for (int n : sizes) {
            long loopResult = sumByLoop(n);
            long formulaResult = sumByFormula(n);

            System.out.printf("%-8d | %-12d | %-12d | %-15d | %d%n",
                    n, loopResult, formulaResult, loopOperationCount(n), 1);

            // 두 알고리즘의 "정확성"이 같은지 반드시 확인한다
            if (loopResult != formulaResult) {
                System.out.println("경고: 두 방법의 결과가 다릅니다! 알고리즘에 오류가 있습니다.");
            }
        }

        System.out.println();
        System.out.println("n이 10배 커질 때: 반복문의 일은 10배 늘고, 공식의 일은 그대로 1이다.");
    }
}
