public class Step4SelectionSort {

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
        int compareCount = 0;   // 1강의 연산 카운터 — 비교가 일어날 때마다 +1
        int swapCount = 0;      // 교환이 일어날 때마다 +1

        System.out.println("정렬 전: " + toText(prices));

        // 바깥 반복문: 기준 위치 i를 0부터 끝-1까지 옮긴다 (3강의 중첩 반복문 구조)
        for (int i = 0; i < prices.length - 1; i++) {
            int minIndex = i;   // 남은 구간 [i..끝]의 최솟값 위치

            // 안쪽 반복문: i 오른쪽에서 더 작은 값을 찾는다 (Step3의 한 회차)
            for (int j = i + 1; j < prices.length; j++) {
                compareCount++;
                if (prices[j] < prices[minIndex]) {
                    minIndex = j;
                }
            }

            // 최솟값이 이미 제자리(i)에 있으면 교환하지 않는다
            if (minIndex != i) {
                swap(prices, i, minIndex);
                swapCount++;
            }
        }

        System.out.println("정렬 후: " + toText(prices));
        System.out.println("비교 횟수 = " + compareCount + ", 교환 횟수 = " + swapCount);
    }
}
