import java.util.Arrays;
import java.util.IntSummaryStatistics;

/**
 * 2강 「람다·스트림 수정 문제」 정답.
 *
 * ScoreStatsComplete.java의 반복문 집계를 스트림으로 다시 쓴 것이다.
 * 반복문 버전을 그대로 남겨 두고, 두 결과가 같은지 실행 결과로 확인한다.
 * 복잡도는 어느 쪽도 O(n)이다 — 문법이 바뀐 것이지 알고리즘이 바뀐 것이 아니다.
 */
public class ModernizeSolution {

    /* ─────────── 이전: ScoreStatsComplete와 같은 반복문 코드 (비교 기준) ─────────── */

    static int sumLoop(int[] data) {
        int total = 0;
        for (int i = 0; i < data.length; i++) {
            total = total + data[i];
        }
        return total;
    }

    static double averageLoop(int[] data) {
        return (double) sumLoop(data) / data.length;
    }

    static int maxLoop(int[] data) {
        int candidate = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] > candidate) {
                candidate = data[i];
            }
        }
        return candidate;
    }

    static int minLoop(int[] data) {
        int candidate = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] < candidate) {
                candidate = data[i];
            }
        }
        return candidate;
    }

    static int countAtLeastLoop(int[] data, int threshold) {
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] >= threshold) {
                count++;
            }
        }
        return count;
    }

    /** 배열은 크기가 고정이라 (1) 개수를 세고 (2) 크기를 정해 (3) 채운다 — 두 번 순회한다. */
    static int[] collectBelowLoop(int[] data, double limit) {
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < limit) count++;
        }
        int[] result = new int[count];
        int pos = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < limit) {
                result[pos] = data[i];
                pos++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        /* ─────────── 문제 ① 집계 네 번을 한 번으로 ─────────── */
        // 이전에는 sum, average, max, min이 각자 배열을 훑어 모두 네 번 순회했다.
        // summaryStatistics()는 한 번 훑으면서 다섯 값을 한꺼번에 모은다.
        IntSummaryStatistics stat = Arrays.stream(scores).summaryStatistics();

        System.out.println("== 문제 ① 집계 ==");
        System.out.println("  합계   반복문 " + sumLoop(scores) + " | 스트림 " + stat.getSum()
                + " | 같은가 " + (sumLoop(scores) == stat.getSum()));
        System.out.println("  평균   반복문 " + averageLoop(scores) + " | 스트림 " + stat.getAverage()
                + " | 같은가 " + (Math.abs(averageLoop(scores) - stat.getAverage()) < 1e-9));
        System.out.println("  최고점 반복문 " + maxLoop(scores) + " | 스트림 " + stat.getMax()
                + " | 같은가 " + (maxLoop(scores) == stat.getMax()));
        System.out.println("  최저점 반복문 " + minLoop(scores) + " | 스트림 " + stat.getMin()
                + " | 같은가 " + (minLoop(scores) == stat.getMin()));

        // 하나만 필요하다면 summaryStatistics까지 갈 것 없이 이렇게 쓴다.
        // 빈 배열이면 값이 없으므로 Optional이 나온다 — orElse로 없을 때를 반드시 적게 만든다.
        System.out.println("  (최저점만 필요할 때) "
                + Arrays.stream(scores).min().orElse(0));

        /* ─────────── 문제 ② 조건 세기와 조건 수집 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 조건 검색 ==");

        long countStream = Arrays.stream(scores).filter(s -> s >= 80).count();
        System.out.println("  80점 이상 반복문 " + countAtLeastLoop(scores, 80)
                + " | 스트림 " + countStream
                + " | 같은가 " + (countAtLeastLoop(scores, 80) == countStream));

        // average를 람다 안에서 쓰려면 한 번 대입한 뒤 바꾸지 않아야 한다(effectively final).
        double average = stat.getAverage();
        int[] belowLoop = collectBelowLoop(scores, average);
        int[] belowStream = Arrays.stream(scores).filter(s -> s < average).toArray();
        System.out.println("  평균 미만 반복문 " + Arrays.toString(belowLoop));
        System.out.println("  평균 미만 스트림 " + Arrays.toString(belowStream));
        System.out.println("  같은가 " + Arrays.equals(belowLoop, belowStream));

        System.out.println();
        System.out.println("두 방식 모두 배열을 훑는 횟수만 다를 뿐 O(n)이다.");
        System.out.println("스트림은 표현을 바꾼 것이지 복잡도를 바꾼 것이 아니다.");
    }
}
