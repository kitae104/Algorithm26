public class Step4ClimbStairs {

    /** 재귀 호출 횟수 카운터 */
    static long callCount = 0;

    /** 순수 재귀: ways(n) = ways(n-1) + ways(n-2) — 마지막 걸음이 1칸이냐 2칸이냐로 나눈다 */
    static long waysByRecursion(int n) {
        callCount++;
        if (n == 1) {
            return 1;                       // 기저 조건: 1칸은 [1] 한 가지
        }
        if (n == 2) {
            return 2;                       // 기저 조건: 2칸은 [1+1], [2] 두 가지
        }
        return waysByRecursion(n - 1) + waysByRecursion(n - 2);   // 점화식
    }

    /** 바텀업 테이블: 같은 점화식을 반복문으로 채운다 */
    static long waysByTable(int n) {
        if (n == 1) {
            return 1;
        }
        long[] dp = new long[n + 1];        // dp[1]부터 dp[n]까지 사용
        dp[1] = 1;                          // 기저 조건
        dp[2] = 2;
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];  // 점화식(전이)
        }
        return dp[n];
    }

    public static void main(String[] args) {
        int[] sizes = {4, 10, 20, 30};

        System.out.println("계단 오르기: n칸을 1칸/2칸 걸음으로 오르는 방법의 수");
        System.out.println("n    | 재귀 결과  | 테이블 결과 | 재귀 호출 횟수 | 테이블 덧셈 횟수");
        System.out.println("-----+-----------+------------+---------------+-----------------");

        for (int n : sizes) {
            callCount = 0;
            long byRecursion = waysByRecursion(n);
            long byTable = waysByTable(n);
            System.out.printf("%-4d | %-9d | %-10d | %,-13d | %d%n",
                    n, byRecursion, byTable, callCount, Math.max(n - 2, 0));

            if (byRecursion != byTable) {
                System.out.println("경고: 두 방법의 결과가 다릅니다! 점화식 구현에 오류가 있습니다.");
            }
        }

        System.out.println();
        System.out.println("피보나치와 다른 문제인데 점화식 모양은 같다 — 기저 조건만 1, 2로 다르다.");
    }
}
