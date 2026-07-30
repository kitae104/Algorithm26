public class GreedyFailApplication {

    /** 그리디 동전 교환: 큰 동전부터 최대한 사용하고, 과정을 출력한다. */
    static int greedyChange(int amount, int[] coinsDesc) {
        int remaining = amount;
        int totalCount = 0;

        for (int coin : coinsDesc) {
            int count = remaining / coin;
            if (count > 0) {
                System.out.println("  [그리디] " + coin + "원 x " + count
                        + "개 → 남은 금액 " + (remaining % coin) + "원");
            }
            totalCount += count;
            remaining = remaining % coin;
        }
        return totalCount;
    }

    /**
     * 완전 탐색(3강): 동전 {10, 7, 1}의 모든 개수 조합을 확인해 최소 동전 수를 찾는다.
     * 남는 금액은 1원으로 채우면 되므로 10원과 7원의 개수만 모두 시도한다.
     */
    static int bestChangeFor14(int amount) {
        int best = Integer.MAX_VALUE;
        int bestTen = 0, bestSeven = 0, bestOne = 0;

        for (int ten = 0; ten * 10 <= amount; ten++) {
            for (int seven = 0; ten * 10 + seven * 7 <= amount; seven++) {
                int one = amount - ten * 10 - seven * 7;   // 나머지는 전부 1원
                int total = ten + seven + one;
                if (total < best) {
                    best = total;
                    bestTen = ten;
                    bestSeven = seven;
                    bestOne = one;
                }
            }
        }
        System.out.println("  [완전 탐색] 10원 " + bestTen + "개, 7원 " + bestSeven
                + "개, 1원 " + bestOne + "개");
        return best;
    }

    public static void main(String[] args) {
        System.out.println("== 실험 1: 동전 {1, 7, 10}으로 14원 거슬러 주기 ==");
        int[] strangeCoins = {10, 7, 1};
        int greedyResult = greedyChange(14, strangeCoins);
        System.out.println("그리디 결과: " + greedyResult + "개");
        int bestResult = bestChangeFor14(14);
        System.out.println("최적 결과: " + bestResult + "개");

        if (greedyResult != bestResult) {
            System.out.println("→ 그리디(" + greedyResult + "개)는 최적(" + bestResult
                    + "개)이 아니다! 지금의 최선(10원)이 미래의 더 좋은 선택(7원 2개)을 막았다.");
        }

        System.out.println();
        System.out.println("== 실험 2: 동전 {10, 50, 100, 500}으로 1260원 거슬러 주기 ==");
        int[] koreanCoins = {500, 100, 50, 10};
        int koreanResult = greedyChange(1260, koreanCoins);
        System.out.println("그리디 결과: " + koreanResult + "개");
        System.out.println("→ 한국 동전은 작은 동전이 큰 동전의 약수(배수 관계)라서");
        System.out.println("  큰 동전을 포기해도 이득이 없다. 이런 구조에서는 그리디가 항상 최적이다.");
    }
}
