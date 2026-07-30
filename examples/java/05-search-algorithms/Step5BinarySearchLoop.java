public class Step5BinarySearchLoop {
    public static void main(String[] args) {
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};
        int target = 3106;

        int low = 0;
        int high = bookNumbers.length - 1;
        int compareCount = 0;     // 비교 횟수 (1강의 카운터 기법)
        int foundIndex = -1;

        // 범위가 남아 있는 동안(low <= high) 4단계의 "절반 버리기"를 반복한다
        while (low <= high) {
            int mid = (low + high) / 2;
            compareCount++;
            System.out.println("비교 " + compareCount + ": 범위 [" + low + ", " + high
                    + "], mid = " + mid + ", bookNumbers[mid] = " + bookNumbers[mid]);

            if (bookNumbers[mid] == target) {
                foundIndex = mid;
                break;                    // 찾았으므로 즉시 종료
            } else if (bookNumbers[mid] < target) {
                low = mid + 1;            // 왼쪽 절반 버리기
            } else {
                high = mid - 1;           // 오른쪽 절반 버리기
            }
        }

        System.out.println();
        if (foundIndex >= 0) {
            System.out.println("탐색 성공: 도서 번호 " + target + "은(는) 인덱스 "
                    + foundIndex + "에 있다");
        } else {
            System.out.println("탐색 실패: 도서 번호 " + target + "은(는) 없다");
        }
        System.out.println("총 비교 횟수 = " + compareCount
                + " (도서 " + bookNumbers.length + "권)");
    }
}
