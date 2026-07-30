public class Step4HalfDiscard {
    public static void main(String[] args) {
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};
        int target = 3106;

        int low = 0;
        int high = bookNumbers.length - 1;

        System.out.println("탐색 전 범위: [" + low + ", " + high + "] — 남은 도서 "
                + (high - low + 1) + "권");

        // 중간 위치를 계산하고 한 번 비교한다 (3단계와 동일)
        int mid = (low + high) / 2;
        System.out.println("비교: bookNumbers[" + mid + "] = " + bookNumbers[mid]
                + " vs target " + target);

        // 핵심: 비교 결과에 따라 범위의 절반을 버린다
        if (bookNumbers[mid] == target) {
            System.out.println("발견! 인덱스 " + mid);
        } else if (bookNumbers[mid] < target) {
            // mid 자리는 이미 확인했으므로 mid + 1부터 보면 된다
            low = mid + 1;
            System.out.println(bookNumbers[mid] + " < " + target
                    + " → 왼쪽 절반(인덱스 0~" + mid + ")을 통째로 버린다: low = mid + 1 = " + low);
        } else {
            high = mid - 1;
            System.out.println(bookNumbers[mid] + " > " + target
                    + " → 오른쪽 절반(인덱스 " + mid + "~11)을 통째로 버린다: high = mid - 1 = " + high);
        }

        System.out.println("탐색 후 범위: [" + low + ", " + high + "] — 남은 도서 "
                + (high - low + 1) + "권");
        System.out.println("비교 1번으로 후보가 " + bookNumbers.length + "권에서 "
                + (high - low + 1) + "권으로 줄었다!");
    }
}
