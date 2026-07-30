public class Step2AccessAndUpdate {
    public static void main(String[] args) {
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        // 1) 인덱스로 원소 하나를 읽는다 — 배열의 접근은 위치 계산 한 번, O(1)
        System.out.println("네 번째 학생(인덱스 3)의 점수 = " + scores[3]);

        // 2) 인덱스로 원소 하나를 바꾼다 — 재채점으로 66점이 70점이 되었다
        scores[3] = 70;
        System.out.println("재채점 후 scores[3] = " + scores[3]);

        // 3) 최댓값 알고리즘의 핵심 연산: "후보와 비교 한 번"
        int maxSoFar = scores[0];        // 지금까지의 최댓값 후보
        if (scores[1] > maxSoFar) {      // 비교 1번: 새 값이 후보보다 큰가?
            maxSoFar = scores[1];        // 크면 후보를 교체한다
        }
        System.out.println("scores[0]과 scores[1] 중 큰 값 = " + maxSoFar);
    }
}
