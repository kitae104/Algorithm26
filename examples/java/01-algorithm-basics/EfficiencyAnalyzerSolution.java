/*
 * 1강 최종 문제 — 정답 코드
 * 알고리즘 효율성 분석 프로그램 "AlgoMeter"
 *
 * 같은 문제를 푸는 두 가지 방식의 실행 횟수를 측정해 표로 비교한다.
 *   과제 1: 1부터 n까지의 합       — 반복문(O(n)) vs 공식(O(1))
 *   과제 2: 점수 목록에서 값 찾기  — 전체 확인(항상 n번) vs 조기 중단(최선 1번, 최악 n번)
 */
public class EfficiencyAnalyzerSolution {

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
        for (int i = 1; i <= n; i++) {
            sum = sum + i;   // 핵심 연산: 누적
            count++;
        }
        return new Measurement(sum, count);
    }

    /** 과제 1-B: 가우스 공식. long으로 계산해 오버플로를 막는다. */
    static Measurement sumByFormula(int n) {
        long sum = (long) n * (n + 1) / 2;
        return new Measurement(sum, 1);
    }

    /** 과제 2-A: 배열 전체를 끝까지 확인한다. 비교 횟수는 항상 n이다. */
    static Measurement findCheckAll(int[] data, int target) {
        long count = 0;
        long foundIndex = -1;
        for (int i = 0; i < data.length; i++) {
            count++;                 // 핵심 연산: 비교
            if (data[i] == target) {
                foundIndex = i;      // 기록만 하고 계속 확인한다
            }
        }
        return new Measurement(foundIndex, count);
    }

    /** 과제 2-B: 찾는 즉시 중단한다. 최선 1번, 최악 n번 비교. */
    static Measurement findStopEarly(int[] data, int target) {
        long count = 0;
        for (int i = 0; i < data.length; i++) {
            count++;                 // 핵심 연산: 비교
            if (data[i] == target) {
                return new Measurement(i, count);   // 즉시 반환 = 조기 중단
            }
        }
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

        System.out.println();
        System.out.println("== 과제 2 보너스: 없는 값(100점) 찾기 ==");
        Measurement allMiss = findCheckAll(scores, 100);
        Measurement earlyMiss = findStopEarly(scores, 100);
        System.out.println("전체 확인 방식: 위치 " + allMiss.result + ", 비교 " + allMiss.operationCount + "번");
        System.out.println("조기 중단 방식: 위치 " + earlyMiss.result + ", 비교 " + earlyMiss.operationCount + "번");
        System.out.println("없는 값을 찾을 때(최악의 경우)는 두 방식 모두 n번 비교한다.");
    }
}
