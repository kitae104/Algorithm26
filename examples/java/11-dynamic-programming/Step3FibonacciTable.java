public class Step3FibonacciTable {

    /** 타뷸레이션(바텀업): 재귀 없이, 작은 답부터 표를 차례로 채운다 */
    static long fiboByTable(int n) {
        if (n <= 1) {
            return n;
        }
        long[] dp = new long[n + 1];        // dp[0]부터 dp[n]까지 → 크기는 n + 1
        dp[0] = 0;                          // 기저 조건
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];  // 점화식: 이미 채운 두 칸을 더한다
        }
        return dp[n];                       // 표의 마지막 칸이 최종 답
    }

    public static void main(String[] args) {
        // 1) n = 10의 테이블이 채워진 최종 모습을 눈으로 확인한다
        int n = 10;
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        System.out.println("피보나치 DP 테이블 (n = " + n + ")");
        System.out.print("인덱스:");
        for (int i = 0; i <= n; i++) {
            System.out.printf("%6d", i);
        }
        System.out.println();
        System.out.print("dp[i] :");
        for (int i = 0; i <= n; i++) {
            System.out.printf("%6d", dp[i]);
        }
        System.out.println();
        System.out.println();

        // 2) 큰 n에서도 덧셈 n - 1번이면 끝난다
        int[] sizes = {10, 20, 30, 35, 50};
        System.out.println("n    | fiboByTable(n) | 덧셈 횟수 (n - 1)");
        System.out.println("-----+----------------+------------------");
        for (int size : sizes) {
            System.out.printf("%-4d | %-14d | %d%n", size, fiboByTable(size), size - 1);
        }

        System.out.println();
        System.out.println("재귀가 사라졌다: 반복문 한 번으로 끝나므로 호출 스택 걱정도 없다.");
    }
}
