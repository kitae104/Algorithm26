public class Step1PriceData {
    public static void main(String[] args) {
        // 상품 5개의 가격 (단위: 천 원) — 오늘 정렬할 입력 데이터
        int[] prices = {26, 15, 38, 12, 21};

        System.out.println("상품 개수: " + prices.length);

        // 정렬 전 상태를 [26, 15, 38, 12, 21] 형태로 출력한다
        System.out.print("정렬 전 가격: [");
        for (int i = 0; i < prices.length; i++) {
            System.out.print(prices[i]);
            if (i < prices.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
