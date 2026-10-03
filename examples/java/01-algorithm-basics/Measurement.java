/** 측정 결과를 담는 작은 기록용 클래스 */
public class Measurement {
    long result;          // 계산 결과 (합계 또는 찾은 위치)
    long operationCount;  // 핵심 연산 실행 횟수

    Measurement(long result, long operationCount) {
        this.result = result;
        this.operationCount = operationCount;
    }
}
