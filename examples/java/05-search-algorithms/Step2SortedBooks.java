public class Step2SortedBooks {
    public static void main(String[] args) {
        // 도서관 서가의 도서 번호 (오름차순으로 정렬되어 있다 — 4강에서 배운 정렬의 결과)
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};

        System.out.println("도서 수: " + bookNumbers.length);

        // 이진 탐색의 전제 조건: 배열이 정말 오름차순인지 확인한다
        boolean sorted = true;
        for (int i = 1; i < bookNumbers.length; i++) {
            if (bookNumbers[i - 1] > bookNumbers[i]) {
                sorted = false;
            }
        }
        System.out.println("오름차순 정렬 여부: " + sorted);

        // 배열의 내용을 인덱스와 함께 출력한다
        for (int i = 0; i < bookNumbers.length; i++) {
            System.out.println("bookNumbers[" + i + "] = " + bookNumbers[i]);
        }
    }
}
