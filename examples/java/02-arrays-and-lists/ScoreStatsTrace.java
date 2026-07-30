public class ScoreStatsTrace {
    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        int sum = 0;
        int max = scores[0];
        int min = scores[0];

        System.out.println("한 번의 순회로 합계·최댓값·최솟값 동시 갱신 — 실행 추적");
        System.out.println("회차 | i | scores[i] | 누적 합 sum | 현재 max | 현재 min");
        System.out.println("-----+---+-----------+------------+----------+---------");

        for (int i = 0; i < scores.length; i++) {
            sum = sum + scores[i];                  // 누적
            if (scores[i] > max) max = scores[i];   // 최댓값 후보 비교
            if (scores[i] < min) min = scores[i];   // 최솟값 후보 비교
            System.out.printf("%4d | %d | %9d | %10d | %8d | %d%n",
                    i + 1, i, scores[i], sum, max, min);
        }

        System.out.println();
        System.out.println("최종 결과: 합계 " + sum + ", 최고점 " + max + ", 최저점 " + min);
        System.out.println("순회 " + scores.length + "번으로 통계 세 가지를 동시에 얻었다 — O(n)");
    }
}
