public class PermutationComplete {

    static char[] letters = {'a', 'b', 'c'};
    static boolean[] used = new boolean[letters.length];   // 문자 사용(방문) 여부 관리
    static StringBuilder current = new StringBuilder();    // 지금까지 만든 문자열 (공유 상태)
    static int permCount = 0;
    static int comboCount = 0;

    /** 순열: 아직 안 쓴 문자를 하나 골라 붙이고, 재귀가 돌아오면 반드시 되돌린다. */
    static void permutation() {
        if (current.length() == letters.length) {          // 종료 조건: 전부 골랐다
            permCount++;
            System.out.println("  순열 " + permCount + ": " + current);
            return;
        }

        for (int i = 0; i < letters.length; i++) {
            if (used[i]) {
                continue;                                  // 이미 쓴 문자는 건너뛴다
            }
            used[i] = true;                                // (1) 선택
            current.append(letters[i]);
            permutation();                                 // (2) 진행
            current.deleteCharAt(current.length() - 1);    // (3) 취소: 상태 복원
            used[i] = false;
        }
    }

    /** 조합: start 이후의 문자만 고르므로 순서만 다른 중복이 생기지 않는다. */
    static void combination(int start, int k) {
        if (current.length() == k) {                       // 종료 조건: k개를 골랐다
            comboCount++;
            System.out.println("  조합 " + comboCount + ": " + current);
            return;
        }

        for (int i = start; i < letters.length; i++) {
            current.append(letters[i]);                    // 선택
            combination(i + 1, k);                         // 진행: 다음은 i + 1부터만
            current.deleteCharAt(current.length() - 1);    // 취소
        }
    }

    public static void main(String[] args) {
        System.out.println("[1] {a, b, c}의 모든 순열 (3! = 6개)");
        permutation();

        System.out.println();
        System.out.println("[2] {a, b, c}에서 2개를 고르는 조합 (3C2 = 3개)");
        combination(0, 2);
    }
}
