public class Step1LinearSearch {
    public static void main(String[] args) {
        // 도서관 서가의 도서 번호 목록
        int[] bookNumbers = {1001, 1203, 1450, 2088, 2311, 2754,
                             3106, 3502, 3860, 4213, 4771, 5090};
        int[] targets = {3106, 2500};   // 2500은 없는 번호

        for (int target : targets) {
            System.out.println("도서 번호 " + target + " 찾기 (순차 탐색):");

            int compareCount = 0;   // 비교 횟수 (1강의 카운터 기법)
            int foundIndex = -1;

            // 순차 탐색: 앞에서부터 하나씩 비교한다
            for (int i = 0; i < bookNumbers.length; i++) {
                compareCount++;                 // 핵심 연산: 비교
                if (bookNumbers[i] == target) {
                    foundIndex = i;
                    break;                      // 찾는 즉시 중단 (1강의 조기 중단)
                }
            }

            if (foundIndex >= 0) {
                System.out.println("  → 인덱스 " + foundIndex + "에서 발견, 비교 "
                        + compareCount + "번");
            } else {
                System.out.println("  → 없음, 비교 " + compareCount + "번 (끝까지 확인함)");
            }
        }

        System.out.println();
        System.out.println("도서가 n권이면 최악의 경우 비교도 n번 — 순차 탐색은 O(n)이다.");
    }
}
