public class BookSearchApplication {

    /** 도서 정보: 번호, 제목, 재고 */
    static class Book {
        int number;
        String title;
        int stock;

        Book(int number, String title, int stock) {
            this.number = number;
            this.title = title;
            this.stock = stock;
        }
    }

    /** 마지막 탐색의 비교 횟수 */
    static int compareCount = 0;

    /** 도서 번호로 이진 탐색한다. 배열은 번호 오름차순 정렬이 전제다. */
    static int binarySearchByNumber(Book[] books, int targetNumber) {
        compareCount = 0;
        int low = 0;
        int high = books.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            compareCount++;
            if (books[mid].number == targetNumber) {
                return mid;
            } else if (books[mid].number < targetNumber) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    /** int 배열에서 순차 탐색의 비교 횟수만 센다 (대규모 비교 실험용). */
    static int linearComparisons(int[] arr, int target) {
        int comparisons = 0;
        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            if (arr[i] == target) {
                return comparisons;
            }
        }
        return comparisons;
    }

    /** int 배열에서 이진 탐색의 비교 횟수만 센다 (대규모 비교 실험용). */
    static int binaryComparisons(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;
        int comparisons = 0;
        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            if (arr[mid] == target) {
                return comparisons;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return comparisons;
    }

    public static void main(String[] args) {
        // 도서 번호 오름차순으로 정렬된 도서 목록 (4강 정렬의 결과라고 가정)
        Book[] books = {
            new Book(1001, "알고리즘 첫걸음", 3),
            new Book(1203, "자바 프로그래밍 입문", 5),
            new Book(1450, "자료구조의 이해", 2),
            new Book(2088, "데이터베이스 개론", 0),
            new Book(2311, "운영체제 원리", 4),
            new Book(2754, "컴퓨터 네트워크", 1),
            new Book(3106, "소프트웨어 공학", 6),
            new Book(3502, "인공지능 기초", 2),
            new Book(3860, "웹 개발 실무", 0),
            new Book(4213, "정보 보안의 기초", 3)
        };

        int[] queries = {3106, 2088, 2500};

        System.out.println("== 도서 번호로 재고 확인 (도서 " + books.length + "권) ==");
        for (int number : queries) {
            int index = binarySearchByNumber(books, number);
            System.out.println("도서 번호 " + number + " 검색 (비교 " + compareCount + "번):");
            if (index >= 0) {
                Book found = books[index];
                String status = found.stock > 0
                        ? "대출 가능 (재고 " + found.stock + "권)"
                        : "재고 없음 (대출 불가)";
                System.out.println("  → \"" + found.title + "\" — " + status);
            } else {
                System.out.println("  → 등록되지 않은 도서 번호입니다.");
            }
        }

        // 대규모 실험: 도서 100,000권이라면?
        System.out.println();
        System.out.println("== 대규모 실험: 도서 100,000권에서 번호 찾기 ==");
        int n = 100000;
        int[] codes = new int[n];
        for (int i = 0; i < n; i++) {
            codes[i] = 10001 + i * 2;    // 정렬된 도서 번호 (모두 홀수)
        }

        int middleTarget = codes[50000];     // 앞에서 50,001번째 도서
        System.out.println("있는 번호 " + middleTarget + " (인덱스 50000):");
        System.out.println("  순차 탐색 비교 = " + linearComparisons(codes, middleTarget) + "번");
        System.out.println("  이진 탐색 비교 = " + binaryComparisons(codes, middleTarget) + "번");

        int missingTarget = 10000;           // 짝수이므로 반드시 없는 번호
        System.out.println("없는 번호 " + missingTarget + ":");
        System.out.println("  순차 탐색 비교 = " + linearComparisons(codes, missingTarget) + "번");
        System.out.println("  이진 탐색 비교 = " + binaryComparisons(codes, missingTarget) + "번");

        System.out.println();
        System.out.println("데이터가 십만 개여도 이진 탐색은 20번을 넘지 않는다 — O(log n)의 힘이다.");
    }
}
