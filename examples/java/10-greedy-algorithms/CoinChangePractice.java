public class CoinChangePractice {

    /** 한국 동전 (큰 동전부터 — 내림차순 정렬 상태) */
    static int[] COINS = {500, 100, 50, 10};

    /**
     * 그리디 동전 교환: 큰 동전부터 최대한 사용해 총 동전 수를 반환한다.
     * amount는 10원 단위라고 가정한다.
     */
    static int countCoins(int amount) {
        int remaining = amount;
        int totalCount = 0;
        // TODO: COINS를 앞(큰 동전)에서부터 순회하면서
        //       1) 이 동전을 몇 개 쓸 수 있는지 나눗셈(/)으로 구해 totalCount에 더하고
        //       2) 남은 금액을 나머지 연산(%)으로 갱신하세요.
        return totalCount;
    }

    public static void main(String[] args) {
        int[] amounts = {720, 380, 1260};
        for (int amount : amounts) {
            System.out.println(amount + "원 → 동전 " + countCoins(amount) + "개");
        }
    }
}
