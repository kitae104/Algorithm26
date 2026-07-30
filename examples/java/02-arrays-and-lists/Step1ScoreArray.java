public class Step1ScoreArray {
    public static void main(String[] args) {
        // 학생 8명의 점수 (입력 데이터) — 1강에서 쓰던 5명 반에 3명이 더 들어왔다
        int[] scores = {72, 85, 90, 66, 78, 93, 55, 81};

        System.out.println("학생 수: " + scores.length);
        System.out.println("첫 번째 점수 scores[0] = " + scores[0]);
        System.out.println("마지막 점수 scores[" + (scores.length - 1) + "] = "
                + scores[scores.length - 1]);
        System.out.println();

        // 배열 전체를 인덱스 0부터 length - 1까지 순회하며 출력한다
        for (int i = 0; i < scores.length; i++) {
            System.out.println("scores[" + i + "] = " + scores[i]);
        }
    }
}
