public class SearchAlgorithmsComplete {

    /** 탐색 결과: 찾은 위치와 비교 횟수를 함께 담는 기록용 클래스 */
    static class SearchResult {
        int index;        // 찾은 위치 (없으면 -1)
        int comparisons;  // 비교 횟수

        SearchResult(int index, int comparisons) {
            this.index = index;
            this.comparisons = comparisons;
        }
    }

    /** 순차 탐색: 앞에서부터 하나씩 비교. 1강의 findStopEarly와 같은 구조다. */
    static SearchResult linearSearch(int[] arr, int target) {
        int comparisons = 0;
        for (int i = 0; i < arr.length; i++) {
            comparisons++;                       // 핵심 연산: 비교
            if (arr[i] == target) {
                return new SearchResult(i, comparisons);   // 찾는 즉시 중단
            }
        }
        return new SearchResult(-1, comparisons);
    }

    /** 반복문 기반 이진 탐색: 정렬된 배열에서 절반씩 버리며 찾는다. */
    static SearchResult binarySearchLoop(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;
        int comparisons = 0;
        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;                       // 핵심 연산: 비교
            if (arr[mid] == target) {
                return new SearchResult(mid, comparisons);
            } else if (arr[mid] < target) {
                low = mid + 1;                   // 왼쪽 절반 버리기
            } else {
                high = mid - 1;                  // 오른쪽 절반 버리기
            }
        }
        return new SearchResult(-1, comparisons);
    }

    /** 재귀 기반 이진 탐색: 같은 알고리즘을 "자기 자신 호출"로 표현한다. (7강에서 심화) */
    static SearchResult binarySearchRecursive(int[] arr, int target,
                                              int low, int high, int comparisons) {
        if (low > high) {
            return new SearchResult(-1, comparisons);      // 범위가 비면 실패
        }
        int mid = (low + high) / 2;
        comparisons++;
        if (arr[mid] == target) {
            return new SearchResult(mid, comparisons);
        } else if (arr[mid] < target) {
            return binarySearchRecursive(arr, target, mid + 1, high, comparisons);
        } else {
            return binarySearchRecursive(arr, target, low, mid - 1, comparisons);
        }
    }

    /** 중복이 있을 때 첫 번째 위치를 찾는다 (lower bound의 기초). */
    static SearchResult firstOccurrence(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;
        int comparisons = 0;
        int answer = -1;
        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            if (arr[mid] == target) {
                answer = mid;        // 일단 기록하고,
                high = mid - 1;      // 더 왼쪽에도 있는지 계속 확인한다
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return new SearchResult(answer, comparisons);
    }

    public static void main(String[] args) {
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};
        int[] targets = {1001, 3106, 5090, 2500};   // 2500은 없는 번호

        System.out.println("도서 " + bookNumbers.length + "권에서 번호 찾기 — 순차 vs 이진(반복) vs 이진(재귀)");
        System.out.println("찾는 번호 | 순차 위치 | 순차 비교 | 이진 위치 | 이진 비교 | 재귀 비교");
        System.out.println("---------+----------+----------+----------+----------+----------");

        for (int target : targets) {
            SearchResult lin = linearSearch(bookNumbers, target);
            SearchResult bin = binarySearchLoop(bookNumbers, target);
            SearchResult rec = binarySearchRecursive(bookNumbers, target,
                    0, bookNumbers.length - 1, 0);

            System.out.printf("%-9d| %-9d| %-9d| %-9d| %-9d| %d%n",
                    target, lin.index, lin.comparisons, bin.index, bin.comparisons, rec.comparisons);

            // 세 방법의 정확성이 같은지 반드시 확인한다
            if (lin.index != bin.index || bin.index != rec.index) {
                System.out.println("경고: 세 방법의 결과가 다릅니다! 알고리즘에 오류가 있습니다.");
            }
        }

        System.out.println();
        int[] withDuplicates = {1001, 2311, 2311, 2311, 2754, 3106, 3106, 3860};
        System.out.println("중복이 있는 배열: [1001, 2311, 2311, 2311, 2754, 3106, 3106, 3860]");
        SearchResult any = binarySearchLoop(withDuplicates, 2311);
        SearchResult first = firstOccurrence(withDuplicates, 2311);
        System.out.println("2311을 일반 이진 탐색으로: 인덱스 " + any.index
                + " (비교 " + any.comparisons + "번) — 중복 중 '어느 하나'에서 멈춘다");
        System.out.println("2311을 firstOccurrence로 : 인덱스 " + first.index
                + " (비교 " + first.comparisons + "번) — 항상 '첫 번째' 위치를 보장한다");
    }
}
