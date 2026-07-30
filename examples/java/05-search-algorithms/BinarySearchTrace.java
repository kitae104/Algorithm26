public class BinarySearchTrace {

    /** 이진 탐색의 매 단계 내부 상태(low, mid, high, 비교값, 결정)를 표로 출력한다. */
    static void traceSearch(int[] arr, int target) {
        System.out.println("찾는 도서 번호: " + target);
        System.out.println("단계 | low | mid | high | arr[mid] | 결정");
        System.out.println("-----+-----+-----+------+----------+---------------------------");

        int low = 0;
        int high = arr.length - 1;
        int step = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            step++;

            String decision;
            if (arr[mid] == target) {
                decision = "발견! 인덱스 " + mid + "에서 종료";
            } else if (arr[mid] < target) {
                decision = target + "보다 작음 → low = " + (mid + 1);
            } else {
                decision = target + "보다 큼 → high = " + (mid - 1);
            }
            System.out.printf("%4d | %3d | %3d | %4d | %8d | %s%n",
                    step, low, mid, high, arr[mid], decision);

            if (arr[mid] == target) {
                System.out.println("탐색 성공 — 비교 " + step + "번");
                return;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        System.out.println("범위가 비었다(low > high) → 탐색 실패 — 비교 " + step + "번");
    }

    public static void main(String[] args) {
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};

        System.out.println("== 성공 사례: 4213 찾기 ==");
        traceSearch(bookNumbers, 4213);

        System.out.println();
        System.out.println("== 실패 사례: 2500 찾기 (없는 번호) ==");
        traceSearch(bookNumbers, 2500);
    }
}
