public class Step3FirstPass {

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /** 배열 상태를 [26, 15, 38, 12, 21] 형태의 문자열로 만든다. */
    static String toText(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        int[] prices = {26, 15, 38, 12, 21};

        System.out.println("시작 상태: " + toText(prices));

        // 2강에서 배운 "최솟값 찾기" — 단, 값이 아니라 위치(minIndex)를 기억한다
        int minIndex = 0;
        for (int j = 1; j < prices.length; j++) {
            if (prices[j] < prices[minIndex]) {
                minIndex = j;
                System.out.println("  새로운 최솟값 후보: prices[" + j + "] = " + prices[j]);
            }
        }

        System.out.println("최솟값은 prices[" + minIndex + "] = " + prices[minIndex]
                + " → 0번 칸과 교환합니다.");
        swap(prices, 0, minIndex);

        System.out.println("1회차 종료: " + toText(prices));
        System.out.println("0번 칸(" + prices[0] + ")은 이제 확정 — 다시 볼 필요가 없습니다.");
    }
}
