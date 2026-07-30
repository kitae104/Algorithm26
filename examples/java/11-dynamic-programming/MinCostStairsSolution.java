public class MinCostStairsSolution {

    /**
     * dp[i] = "i번 계단을 밟고 서 있기"까지의 최소 비용.
     * 바닥에서 첫 발은 0번 또는 1번 계단에 바로 올릴 수 있다.
     */
    static long[] buildDpTable(int[] cost) {
        int n = cost.length;
        long[] dp = new long[n];

        dp[0] = cost[0];                    // 기저 조건: 바닥에서 바로 밟는다
        dp[1] = cost[1];

        for (int i = 2; i < n; i++) {
            // 점화식: i-1번에서 1칸 오르거나, i-2번에서 2칸 오르거나 — 싼 쪽을 고른다
            dp[i] = cost[i] + Math.min(dp[i - 1], dp[i - 2]);
        }
        return dp;
    }

    /** 꼭대기(마지막 계단 다음)까지의 최소 비용 */
    static long minTotalCost(int[] cost) {
        long[] dp = buildDpTable(cost);
        int n = cost.length;
        // 꼭대기에는 마지막 계단(n-1)에서 1칸, 또는 그 아래(n-2)에서 2칸 올라 도착한다
        return Math.min(dp[n - 1], dp[n - 2]);
    }

    /** 검증용 완전 탐색(3강): i번 계단을 밟은 뒤 꼭대기까지의 모든 경로를 시도한다. */
    static long bruteForceFrom(int[] cost, int i) {
        if (i >= cost.length) {
            return 0;                       // 이미 꼭대기
        }
        long stepOne = bruteForceFrom(cost, i + 1);
        long stepTwo = bruteForceFrom(cost, i + 2);
        return cost[i] + Math.min(stepOne, stepTwo);
    }

    static long bruteForceMin(int[] cost) {
        return Math.min(bruteForceFrom(cost, 0), bruteForceFrom(cost, 1));
    }

    public static void main(String[] args) {
        int[][] tests = {
                {10, 15, 20},
                {1, 100, 1, 1, 1, 100, 1, 1, 100, 1},
                {10, 15}
        };

        for (int[] cost : tests) {
            long[] dp = buildDpTable(cost);
            long dpAnswer = minTotalCost(cost);
            long bruteAnswer = bruteForceMin(cost);
            System.out.println("계단 비용 " + java.util.Arrays.toString(cost));
            System.out.println("  DP 테이블   = " + java.util.Arrays.toString(dp));
            System.out.println("  DP 최소 비용 = " + dpAnswer
                    + ", 완전 탐색 검증 = " + bruteAnswer
                    + ", 일치 = " + (dpAnswer == bruteAnswer));
            System.out.println();
        }
    }
}
