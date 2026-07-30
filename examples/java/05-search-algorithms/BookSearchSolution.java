public class BookSearchSolution {

    // 도서 번호(오름차순 정렬됨)와 같은 순서의 제목·재고 데이터
    static int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                                3106, 3502, 3860, 4213, 4771, 5090};
    static String[] titles = {
        "알고리즘 첫걸음", "자바 프로그래밍 입문", "자료구조의 이해",
        "데이터베이스 개론", "운영체제 원리", "컴퓨터 네트워크",
        "소프트웨어 공학", "인공지능 기초", "웹 개발 실무",
        "정보 보안의 기초", "클라우드 컴퓨팅", "모바일 앱 개발"
    };
    static int[] stocks = {3, 5, 2, 0, 4, 1, 6, 2, 0, 3, 7, 1};

    /** 마지막 탐색에서 수행한 비교 횟수 */
    static int compareCount = 0;

    /** 요구사항 1: 순차 탐색 — 앞에서부터 비교하며 target의 위치를 찾는다. 없으면 -1 */
    static int linearSearch(int[] numbers, int target) {
        compareCount = 0;
        for (int i = 0; i < numbers.length; i++) {
            compareCount++;                  // 핵심 연산: 비교
            if (numbers[i] == target) {
                return i;                    // 찾는 즉시 중단 (1강의 조기 중단)
            }
        }
        return -1;
    }

    /** 요구사항 2: 이진 탐색 — 절반씩 버리며 target의 위치를 찾는다. 없으면 -1 */
    static int binarySearch(int[] numbers, int target) {
        compareCount = 0;
        int low = 0;
        int high = numbers.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            compareCount++;                  // 핵심 연산: 비교
            if (numbers[mid] == target) {
                return mid;
            } else if (numbers[mid] < target) {
                low = mid + 1;               // 왼쪽 절반 버리기
            } else {
                high = mid - 1;              // 오른쪽 절반 버리기
            }
        }
        return -1;                           // low > high → 없는 번호
    }

    public static void main(String[] args) {
        int[] queries = {1001, 3106, 5090, 2500};   // 2500은 없는 번호

        System.out.println("== 도서 검색 프로그램 BookFinder ==");
        System.out.println("등록 도서 " + bookNumbers.length + "권");
        System.out.println();

        for (int number : queries) {
            System.out.println("도서 번호 " + number + " 검색:");

            int linearIndex = linearSearch(bookNumbers, number);
            int linearCount = compareCount;
            int binaryIndex = binarySearch(bookNumbers, number);
            int binaryCount = compareCount;

            System.out.println("  순차 탐색: 위치 " + linearIndex + ", 비교 " + linearCount + "번");
            System.out.println("  이진 탐색: 위치 " + binaryIndex + ", 비교 " + binaryCount + "번");

            if (binaryIndex >= 0) {
                String status = stocks[binaryIndex] > 0
                        ? "대출 가능 (재고 " + stocks[binaryIndex] + "권)"
                        : "재고 없음 (대출 불가)";
                System.out.println("  도서: \"" + titles[binaryIndex] + "\" — " + status);
            } else {
                System.out.println("  등록되지 않은 도서 번호입니다.");
            }

            // 두 탐색의 정확성 검증: 결과가 다르면 알고리즘에 오류가 있다
            if (linearIndex != binaryIndex) {
                System.out.println("  경고: 두 탐색의 결과가 다릅니다!");
            }
            System.out.println();
        }

        System.out.println("정렬된 데이터라면, 비교 몇 번으로 십만 권 중 한 권도 찾을 수 있다.");
    }
}
