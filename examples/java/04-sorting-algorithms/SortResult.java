/** 한 번의 정렬 결과와 연산 횟수를 담는 기록 클래스 (1강 Measurement 패턴) */
public class SortResult {
    int[] sorted;      // 정렬된 배열
    long compares;     // 비교 횟수
    long swapsOrMoves; // 교환 횟수(선택·버블) 또는 이동 횟수(삽입)

    SortResult(int[] sorted, long compares, long swapsOrMoves) {
        this.sorted = sorted;
        this.compares = compares;
        this.swapsOrMoves = swapsOrMoves;
    }
}
