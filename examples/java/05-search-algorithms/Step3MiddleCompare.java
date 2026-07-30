public class Step3MiddleCompare {
    public static void main(String[] args) {
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};
        int target = 3106;    // 찾는 도서 번호

        // 탐색 범위: 처음에는 배열 전체
        int low = 0;
        int high = bookNumbers.length - 1;

        // 핵심 연산 1: 중간 위치 계산 (정수 나눗셈이므로 소수점은 버려진다)
        int mid = (low + high) / 2;

        System.out.println("찾는 도서 번호: " + target);
        System.out.println("탐색 범위: low = " + low + ", high = " + high);
        System.out.println("중간 위치: mid = (" + low + " + " + high + ") / 2 = " + mid);
        System.out.println("중간 위치의 도서 번호: bookNumbers[" + mid + "] = " + bookNumbers[mid]);

        // 핵심 연산 2: 중간 값과 target을 한 번 비교한다
        if (bookNumbers[mid] == target) {
            System.out.println("결과: 발견! 인덱스 " + mid + "에서 탐색 종료");
        } else if (bookNumbers[mid] < target) {
            System.out.println("결과: " + bookNumbers[mid] + " < " + target
                    + " → 찾는 번호는 mid보다 오른쪽에 있다");
        } else {
            System.out.println("결과: " + bookNumbers[mid] + " > " + target
                    + " → 찾는 번호는 mid보다 왼쪽에 있다");
        }
    }
}
