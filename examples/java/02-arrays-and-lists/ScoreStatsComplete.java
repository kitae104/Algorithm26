public class ScoreStatsComplete {

    /** 합계: 1강의 누적 패턴. 원소가 n개면 누적 연산도 n번 — O(n) */
    static int sum(int[] data) {
        int total = 0;
        for (int i = 0; i < data.length; i++) {
            total = total + data[i];
        }
        return total;
    }

    /** 평균: 합계를 개수로 나눈다. (double) 캐스팅으로 소수점을 지킨다 */
    static double average(int[] data) {
        return (double) sum(data) / data.length;
    }

    /** 최댓값: 첫 원소를 후보로 두고, 더 큰 값을 만나면 후보를 교체한다 — O(n) */
    static int max(int[] data) {
        int candidate = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] > candidate) {
                candidate = data[i];
            }
        }
        return candidate;
    }

    /** 최솟값: 최댓값과 같은 구조에서 부등호 방향만 다르다 — O(n) */
    static int min(int[] data) {
        int candidate = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] < candidate) {
                candidate = data[i];
            }
        }
        return candidate;
    }

    /** 조건 검색: threshold점 이상인 원소의 개수 — O(n) */
    static int countAtLeast(int[] data, int threshold) {
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] >= threshold) {
                count++;
            }
        }
        return count;
    }

    /**
     * 조건 검색 + 배열 복사: limit 미만인 원소만 모아 "새 배열"로 반환한다.
     * 배열은 크기가 고정이므로 (1) 개수를 먼저 세고 (2) 그 크기로 만들어 (3) 채운다.
     */
    static int[] collectBelow(int[] data, double limit) {
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < limit) count++;            // 1차 순회: 개수 세기
        }
        int[] result = new int[count];               // 정확한 크기의 새 배열
        int pos = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < limit) {
                result[pos] = data[i];               // 2차 순회: 값 채우기
                pos++;
            }
        }
        return result;
    }

    /** 빈도 계산: 점수대별 인원을 카운팅 배열로 센다 — O(n) */
    static int[] bandCounts(int[] data) {
        int[] bands = new int[10];
        for (int i = 0; i < data.length; i++) {
            bands[data[i] / 10]++;
        }
        return bands;
    }

    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};
        double avg = average(scores);

        System.out.println("== 성적 통계 리포트 ==");
        System.out.println("학생 수   : " + scores.length + "명");
        System.out.println("합계      : " + sum(scores) + "점");
        System.out.printf("평균      : %.1f점%n", avg);
        System.out.println("최고점    : " + max(scores) + "점");
        System.out.println("최저점    : " + min(scores) + "점");
        System.out.println("80점 이상 : " + countAtLeast(scores, 80) + "명");

        int[] belowAvg = collectBelow(scores, avg);
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < belowAvg.length; i++) {
            if (i > 0) line.append(", ");
            line.append(belowAvg[i]);
        }
        System.out.println("평균 미만 : " + belowAvg.length + "명 [" + line + "]");

        int[] bands = bandCounts(scores);
        System.out.print("점수대 분포: ");
        for (int band = 5; band <= 9; band++) {
            System.out.print(band * 10 + "점대 " + bands[band] + "명");
            if (band < 9) System.out.print(" | ");
        }
        System.out.println();
    }
}
