public class CoinChangeDpApplication {

    /** "도달 불가능"을 나타내는 큰 값 (더해도 넘치지 않도록 절반으로) */
    static final int IMPOSSIBLE = Integer.MAX_VALUE / 2;

    /** 10강의 그리디: 큰 동전부터 최대한 많이 사용한다. */
    static int greedyCount(int amount, int[] coinsDesc) {
        int remaining = amount;
        int totalCount = 0;
        for (int coin : coinsDesc) {
            totalCount += remaining / coin;
            remaining = remaining % coin;
        }
        return totalCount;
    }

    /** DP: dp[money] = money원을 만드는 최소 동전 개수. 0원부터 표를 채운다. */
    static int[] buildDpTable(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 0;                          // 기저 조건: 0원은 동전 0개
        for (int money = 1; money <= amount; money++) {
            dp[money] = IMPOSSIBLE;
            for (int coin : coins) {
                // 점화식: dp[money] = min(dp[money - coin] + 1)  (모든 동전을 비교)
                if (coin <= money && dp[money - coin] + 1 < dp[money]) {
                    dp[money] = dp[money - coin] + 1;
                }
            }
        }
        return dp;
    }

    /** 완성된 테이블을 역추적해 실제로 어떤 동전을 몇 개 썼는지 알아낸다. */
    static String reconstruct(int amount, int[] coinsDesc, int[] dp) {
        StringBuilder sb = new StringBuilder();
        int money = amount;
        for (int coin : coinsDesc) {
            int used = 0;
            // dp[money - coin]이 정확히 1 작다면, 이 동전은 최적해의 일부다
            while (coin <= money && dp[money - coin] == dp[money] - 1) {
                money -= coin;
                used++;
            }
            if (used > 0) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(coin).append("원 x ").append(used).append("개");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int[] coinsDesc = {100, 60, 1};     // 10강의 반례 동전: {1, 60, 100}
        int[] amounts = {80, 120, 180, 200};

        System.out.println("동전 {1, 60, 100}으로 거슬러 주기 — 그리디(10강) vs DP(11강)");
        System.out.println("금액   | 그리디 개수 | DP 최소 개수 | DP가 고른 조합");
        System.out.println("-------+------------+-------------+---------------------");

        for (int amount : amounts) {
            int greedy = greedyCount(amount, coinsDesc);
            int[] dp = buildDpTable(amount, coinsDesc);
            String combo = reconstruct(amount, coinsDesc, dp);
            System.out.printf("%-6d | %-10d | %-11d | %s%n",
                    amount, greedy, dp[amount], combo);
        }

        System.out.println();
        System.out.println("120원: 그리디는 100원을 먼저 집어 21개, DP는 모든 동전을 비교해 2개(60+60).");
        System.out.println("그리디는 지금의 최선만 보지만, DP는 점화식으로 모든 선택을 비교한다.");
    }
}
