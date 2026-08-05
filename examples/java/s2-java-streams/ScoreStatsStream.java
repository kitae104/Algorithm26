import java.util.Arrays;
import java.util.IntSummaryStatistics;

/**
 * 2강 ScoreStatsComplete를 스트림으로 다시 쓴다.
 *
 * 반복문 버전과 스트림 버전을 나란히 실행하고 결과가 같은지 프로그램이 확인한다.
 * 1강에서 배운 습관 — 표현을 바꿨으면 결과가 같은지 코드로 검증한다.
 */
public class ScoreStatsStream {

    /* ── 2강의 반복문 버전 ─────────────────────────────────── */

    static int sumLoop(int[] data) {
        int total = 0;
        for (int i = 0; i < data.length; i++) {
            total = total + data[i];
        }
        return total;
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

    static int countAtLeastLoop(int[] data, int threshold) {
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] >= threshold) {
                count++;
            }
        }
        return count;
    }

    /** 배열은 크기가 고정이라 (1) 세고 (2) 만들고 (3) 채우는 세 걸음이 필요했다. */
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
        int[] scores = {88, 72, 95, 64, 79, 91, 58, 100, 83, 67};

        System.out.println("점수: " + Arrays.toString(scores));
        System.out.println();

        /* ── 스트림 버전 ─────────────────────────────────── */
        int sum = Arrays.stream(scores).sum();
        int max = Arrays.stream(scores).max().orElse(0);
        int min = Arrays.stream(scores).min().orElse(0);
        double average = Arrays.stream(scores).average().orElse(0);
        long pass = Arrays.stream(scores).filter(s -> s >= 60).count();
        int[] belowAvg = Arrays.stream(scores).filter(s -> s < average).toArray();

        System.out.println("[스트림 버전]");
        System.out.println("  합계   : " + sum);
        System.out.println("  평균   : " + String.format("%.2f", average));
        System.out.println("  최댓값 : " + max);
        System.out.println("  최솟값 : " + min);
        System.out.println("  60점 이상: " + pass + "명");
        System.out.println("  평균 미만: " + Arrays.toString(belowAvg));
        System.out.println();

        /* ── 결과 검증 ───────────────────────────────────── */
        System.out.println("[반복문 버전과 결과가 같은가]");
        System.out.println("  합계     : " + (sum == sumLoop(scores)));
        System.out.println("  최댓값   : " + (max == maxLoop(scores)));
        System.out.println("  60점 이상: " + (pass == countAtLeastLoop(scores, 60)));
        System.out.println("  평균 미만: " + Arrays.equals(belowAvg, collectBelowLoop(scores, average)));
        System.out.println();

        /* ── 한 번 순회로 통계 다섯 개 ───────────────────── */
        IntSummaryStatistics st = Arrays.stream(scores).summaryStatistics();
        System.out.println("[summaryStatistics — 배열을 한 번만 훑는다]");
        System.out.println("  개수   : " + st.getCount());
        System.out.println("  합계   : " + st.getSum());
        System.out.println("  평균   : " + String.format("%.2f", st.getAverage()));
        System.out.println("  최댓값 : " + st.getMax());
        System.out.println("  최솟값 : " + st.getMin());
        System.out.println();
        System.out.println("  2강 버전은 sum/average/max/min을 각각 순회했습니다 — 배열을 네 번 훑은 셈입니다.");
        System.out.println("  다만 4 x O(n)도 O(n)입니다. 실측 시간은 줄어도 증가 모양은 같습니다(1강).");
        System.out.println();

        /* ── 거르고 · 바꾸고 · 모으기 ─────────────────────── */
        double adjusted = Arrays.stream(scores)
                .filter(s -> s >= 60)     // 거르기
                .map(s -> s + 5)          // 바꾸기 (가산점)
                .average().orElse(0);     // 모으기
        System.out.println("[60점 이상만 5점 가산 후 평균] " + String.format("%.2f", adjusted));
    }
}
