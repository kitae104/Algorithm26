public class SelectionSortTrace {

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

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
        int compareCount = 0;
        int swapCount = 0;

        System.out.println("선택 정렬 실행 추적 — 시작 상태: " + toText(prices));
        System.out.println();

        for (int i = 0; i < prices.length - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < prices.length; j++) {
                compareCount++;
                if (prices[j] < prices[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                System.out.println("i=" + i + " 회차: 최솟값 " + prices[minIndex]
                        + "(위치 " + minIndex + ")과 " + i + "번 칸의 "
                        + prices[i] + "을(를) 교환");
                swap(prices, i, minIndex);
                swapCount++;
            } else {
                System.out.println("i=" + i + " 회차: 최솟값이 이미 " + i
                        + "번 칸에 있음 — 교환 없음");
            }

            System.out.println("       배열 상태: " + toText(prices)
                    + " | 비교 누적 " + compareCount
                    + " | 교환 누적 " + swapCount);
        }

        System.out.println();
        System.out.println("정렬 완료: " + toText(prices)
                + " (비교 " + compareCount + "번, 교환 " + swapCount + "번)");
    }
}
