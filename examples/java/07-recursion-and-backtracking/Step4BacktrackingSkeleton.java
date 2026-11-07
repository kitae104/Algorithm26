public class Step4BacktrackingSkeleton {

    static char[] letters = {'a', 'b', 'c'};
    static boolean[] used = new boolean[letters.length];   // 이미 쓴 문자 표시
    static StringBuilder current = new StringBuilder();    // 지금까지의 선택 (공유 상태)
    static int permCount = 0;

    /** 깊이(depth)번째 자리에 놓을 문자를 결정한다. 선택 -> 진행 -> 취소 구조. */
    static void permutation() {
        if (current.length() == letters.length) {          // 종료 조건: 모든 자리를 채웠다
            permCount++;
            System.out.println("순열 " + permCount + ": " + current);
            return;
        }

        for (int i = 0; i < letters.length; i++) {
            if (used[i]) continue;                         // 이미 쓴 문자는 건너뛴다

            used[i] = true;                                // (1) 선택: 문자를 붙이고 사용 표시
            current.append(letters[i]);
            permutation();                                 // (2) 진행: 다음 자리를 정하러 내려간다
            current.deleteCharAt(current.length() - 1);    // (3) 취소: 상태를 원래대로 복원한다!
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        System.out.println("{a, b, c}의 모든 순열:");
        permutation();
        System.out.println("총 " + permCount + "개 (3 x 2 x 1 = 6)");
    }
}
