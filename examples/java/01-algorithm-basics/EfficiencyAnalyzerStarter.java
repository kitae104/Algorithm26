/*
 * 1강 최종 문제 — 학생용 시작 코드
 * 알고리즘 효율성 분석 프로그램 "AlgoMeter"
 *
 * 같은 문제를 푸는 두 가지 방식의 "실행 횟수"를 측정해 표로 비교한다.
 *   과제 1: 1부터 n까지의 합       — 반복문 방식 vs 공식 방식
 *   과제 2: 점수 목록에서 값 찾기  — 끝까지 모두 확인 vs 찾으면 즉시 중단
 *
 * TODO 부분을 완성하세요. 이 파일은 TODO를 채우지 않아도 컴파일됩니다.
 */
public class EfficiencyAnalyzerStarter {

    /** 측정 결과를 담는 작은 기록용 클래스 */
    static class Measurement {
        long result;          // 계산 결과 (합계 또는 찾은 위치)
        long operationCount;  // 핵심 연산 실행 횟수

        Measurement(long result, long operationCount) {
            this.result = result;
            this.operationCount = operationCount;
        }
    }

    /** 과제 1-A: 반복문으로 1부터 n까지 더하고, 덧셈 횟수를 센다. */
    static Measurement sumByLoop(int n) {
        long sum = 0;
        long count = 0;
        // TODO 1: 1부터 n까지 반복하면서 sum에 누적하고, 누적할 때마다 count를 1 늘리세요.
        return new Measurement(sum, count);
    }

    /** 과제 1-B: 가우스 공식으로 합을 구한다. 연산 횟수는 1로 기록한다. */
    static Measurement sumByFormula(int n) {
        // TODO 2: n * (n + 1) / 2 공식을 사용하세요. (int 곱셈 오버플로에 주의 — long으로 계산)
        return new Measurement(0, 1);
    }

    /** 과제 2-A: 배열 전체를 끝까지 확인하며 target의 위치를 찾는다. (중간에 멈추지 않음) */
    static Measurement findCheckAll(int[] data, int target) {
        long count = 0;
        long foundIndex = -1;
        // TODO 3: 모든 원소를 비교(count 증가)하고, target과 같으면 foundIndex를 기록하세요.
        //         찾은 뒤에도 끝까지 계속 확인합니다.
        return new Measurement(foundIndex, count);
    }

    /** 과제 2-B: 찾는 즉시 반복을 중단한다. */
    static Measurement findStopEarly(int[] data, int target) {
        long count = 0;
        // TODO 4: 비교(count 증가) 후 target을 찾으면 그 자리에서 바로 결과를 반환하세요.
        return new Measurement(-1, count);
    }

    public static void main(String[] args) {
        System.out.println("== 과제 1: 1부터 n까지의 합 ==");
        System.out.println("n      | 반복문 결과 | 공식 결과   | 반복 연산 | 공식 연산");
        int[] sizes = {10, 100, 1000, 10000};
        for (int n : sizes) {
            Measurement loop = sumByLoop(n);
            Measurement formula = sumByFormula(n);
            System.out.printf("%-6d | %-10d | %-10d | %-8d | %d%n",
                    n, loop.result, formula.result, loop.operationCount, formula.operationCount);
        }

        System.out.println();
        System.out.println("== 과제 2: 점수 목록에서 78점 찾기 ==");
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81, 60, 77};
        Measurement all = findCheckAll(scores, 78);
        Measurement early = findStopEarly(scores, 78);
        System.out.println("전체 확인 방식: 위치 " + all.result + ", 비교 " + all.operationCount + "번");
        System.out.println("조기 중단 방식: 위치 " + early.result + ", 비교 " + early.operationCount + "번");

        // TODO 5: 없는 값(예: 100)을 찾을 때 두 방식의 비교 횟수가 어떻게 되는지도 출력해 보세요.
    }
}
