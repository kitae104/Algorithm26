public class InsertPositionPractice {

    /** 정렬된 배열에서 target이 있으면 그 인덱스를,
     *  없으면 "정렬을 유지한 채 끼워 넣을 위치"를 반환한다. */
    static int searchInsertPosition(int[] numbers, int target) {
        int low = 0;
        int high = numbers.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;   // 오버플로 없는 안전한 mid 계산
            if (numbers[mid] == target) {
                return mid;
            } else if (numbers[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        // TODO: 탐색이 실패했을 때 -1 대신 무엇을 반환해야
        //       "끼워 넣을 위치"가 될지 생각해 보세요. (힌트: 반복이 끝난 순간의 low)
        return -1;
    }

    public static void main(String[] args) {
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};
        int[] tests = {3106, 2500, 900, 6000};   // 있는 값 1개 + 없는 값 3개

        for (int target : tests) {
            System.out.println("target " + target + " → 위치 "
                    + searchInsertPosition(bookNumbers, target));
        }
    }
}
