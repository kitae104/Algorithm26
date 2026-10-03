/** 탐색 결과: 찾은 위치와 비교 횟수를 함께 담는 기록용 클래스 */
public class SearchResult {
    int index;        // 찾은 위치 (없으면 -1)
    int comparisons;  // 비교 횟수

    SearchResult(int index, int comparisons) {
        this.index = index;
        this.comparisons = comparisons;
    }
}
