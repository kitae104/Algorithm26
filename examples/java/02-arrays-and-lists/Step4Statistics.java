public class Step4Statistics {
    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        // 1) 합계·최댓값·최솟값을 "한 번의 순회"로 동시에 구한다
        int sum = 0;
        int max = scores[0];   // 첫 원소를 최댓값 후보로 시작
        int min = scores[0];   // 첫 원소를 최솟값 후보로 시작
        for (int i = 0; i < scores.length; i++) {
            sum = sum + scores[i];
            if (scores[i] > max) max = scores[i];   // 후보 비교 (최댓값)
            if (scores[i] < min) min = scores[i];   // 후보 비교 (최솟값)
        }
        System.out.println("합계 = " + sum + ", 최고점 = " + max + ", 최저점 = " + min);

        // 2) 조건 검색: 80점 이상인 점수를 모두 찾는다
        System.out.print("80점 이상: ");
        int passCount = 0;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] >= 80) {
                System.out.print("scores[" + i + "]=" + scores[i] + " ");
                passCount++;
            }
        }
        System.out.println("→ " + passCount + "명");

        // 3) 빈도 계산: 점수대(50점대~90점대)별 학생 수를 카운팅 배열로 센다
        int[] bandCount = new int[10];        // bandCount[7] = 70점대 학생 수
        for (int i = 0; i < scores.length; i++) {
            bandCount[scores[i] / 10]++;      // 예: 72 / 10 = 7 → 70점대 칸을 1 증가
        }
        for (int band = 5; band <= 9; band++) {
            System.out.println(band * 10 + "점대: " + bandCount[band] + "명");
        }
    }
}
