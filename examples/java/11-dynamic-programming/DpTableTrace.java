public class DpTableTrace {

    public static void main(String[] args) {
        int n = 8;
        long[] dp = new long[n + 1];

        System.out.println("계단 오르기 DP 테이블 채우기 추적 (n = " + n + ")");
        System.out.println();

        dp[1] = 1;
        System.out.println("기저 dp[1] = 1              | " + tableString(dp, 1, n));
        dp[2] = 2;
        System.out.println("기저 dp[2] = 2              | " + tableString(dp, 2, n));

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];   // 점화식: 이미 확정된 두 칸을 참조
            System.out.printf("dp[%d] = dp[%d] + dp[%d] = %2d + %2d = %2d | %s%n",
                    i, i - 1, i - 2, dp[i - 1], dp[i - 2], dp[i], tableString(dp, i, n));
        }

        System.out.println();
        System.out.println("최종 답: dp[" + n + "] = " + dp[n]
                + " — " + n + "칸 계단을 오르는 방법은 " + dp[n] + "가지");
    }

    /** 테이블의 현재 상태를 문자열로 만든다. 아직 안 채운 칸은 - 로 표시. */
    static String tableString(long[] dp, int filledUpTo, int n) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 1; i <= n; i++) {
            if (i > 1) {
                sb.append(", ");
            }
            sb.append(i <= filledUpTo ? String.valueOf(dp[i]) : "-");
        }
        return sb.append("]").toString();
    }
}
