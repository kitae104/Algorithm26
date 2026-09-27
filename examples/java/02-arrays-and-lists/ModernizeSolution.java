import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 2강 「람다·스트림 변경」.
 *
 * ScoreStatsComplete.java(실습 코드)를 같은 결과가 나오도록 람다와 스트림으로 다시 쓴 것이다.
 */
public class ModernizeSolution {

    /** 조건 검색: threshold점 이상인 원소의 개수 */
    static long countAtLeast(int[] data, int threshold) {
        return Arrays.stream(data)
                .filter(s -> s >= threshold)
                .count();
    }

    /** 조건 검색 + 배열 복사: limit 미만인 원소만 모아 새 배열로 반환한다. */
    static int[] collectBelow(int[] data, double limit) {
        return Arrays.stream(data)
                .filter(s -> s < limit)
                .toArray();
    }

    /** 빈도 계산: 점수대별 인원을 카운팅 배열로 센다. */
    static int[] bandCounts(int[] data) {
        int[] bands = new int[10];
        Arrays.stream(data).forEach(s -> bands[s / 10]++);
        return bands;
    }

    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        // 개수·합계·평균·최댓값·최솟값을 한 번의 순회로 구한다
        IntSummaryStatistics stat = Arrays.stream(scores).summaryStatistics();
        double avg = stat.getAverage();

        System.out.println("== 성적 통계 리포트 ==");
        System.out.println("학생 수   : " + stat.getCount() + "명");
        System.out.println("합계      : " + stat.getSum() + "점");
        System.out.printf("평균      : %.1f점%n", avg);
        System.out.println("최고점    : " + stat.getMax() + "점");
        System.out.println("최저점    : " + stat.getMin() + "점");
        System.out.println("80점 이상 : " + countAtLeast(scores, 80) + "명");

        int[] belowAvg = collectBelow(scores, avg);
        String line = Arrays.stream(belowAvg)
                .boxed()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        System.out.println("평균 미만 : " + belowAvg.length + "명 [" + line + "]");

        int[] bands = bandCounts(scores);
        String distribution = IntStream.rangeClosed(5, 9)
                .boxed()
                .map(band -> band * 10 + "점대 " + bands[band] + "명")
                .collect(Collectors.joining(" | "));
        System.out.println("점수대 분포: " + distribution);
    }
}
