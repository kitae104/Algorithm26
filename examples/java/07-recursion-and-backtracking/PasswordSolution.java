public class PasswordSolution {

    static char[] candidates = {'1', '3', '7', '9'};   // 후보 문자 4개
    static StringBuilder current = new StringBuilder(); // 지금 만드는 중인 비밀번호
    static int allCount = 0;        // 전체 생성 개수
    static int rulePassCount = 0;   // 가지치기 통과 개수
    static boolean printEach = true; // 하나씩 출력할지 여부 (개수만 셀 때는 false)

    /** [과제 1] 길이 k의 모든 비밀번호를 생성한다. (같은 문자를 여러 번 써도 됨) */
    static void generateAll(int k) {
        if (current.length() == k) {                       // 종료 조건: k자리 완성
            allCount++;
            if (printEach) {
                System.out.println("  " + current);
            }
            return;
        }

        for (int i = 0; i < candidates.length; i++) {
            current.append(candidates[i]);                 // (1) 선택
            generateAll(k);                                // (2) 진행
            current.deleteCharAt(current.length() - 1);    // (3) 취소: 상태 복원
        }
    }

    /** [과제 2] 같은 문자가 연속으로 나오는 가지를 잘라내며 생성한다. */
    static void generateNoRepeat(int k) {
        if (current.length() == k) {                       // 종료 조건
            rulePassCount++;
            if (printEach) {
                System.out.println("  " + current);
            }
            return;
        }

        for (int i = 0; i < candidates.length; i++) {
            // 가지치기: 직전 문자와 같으면 이 가지는 내려가 보지 않는다
            if (current.length() > 0
                    && current.charAt(current.length() - 1) == candidates[i]) {
                continue;
            }
            current.append(candidates[i]);                 // 선택
            generateNoRepeat(k);                           // 진행
            current.deleteCharAt(current.length() - 1);    // 취소
        }
    }

    public static void main(String[] args) {
        System.out.println("후보 문자: 1, 3, 7, 9");

        System.out.println();
        System.out.println("[1] 길이 2 — 만들 수 있는 모든 비밀번호");
        printEach = true;
        allCount = 0;
        generateAll(2);
        System.out.println("  총 " + allCount + "개 (이론값 4^2 = 16)");

        System.out.println();
        System.out.println("[2] 길이 2 — 같은 문자 연속 금지 (가지치기)");
        rulePassCount = 0;
        generateNoRepeat(2);
        System.out.println("  총 " + rulePassCount + "개 (이론값 4 x 3 = 12)");

        System.out.println();
        System.out.println("[3] 길이 3 — 개수만 비교");
        printEach = false;
        allCount = 0;
        rulePassCount = 0;
        generateAll(3);
        generateNoRepeat(3);
        System.out.println("  전체 생성: " + allCount + "개 (4^3 = 64)");
        System.out.println("  연속 금지: " + rulePassCount + "개 (4 x 3 x 3 = 36)");
        System.out.println("  가지치기로 " + (allCount - rulePassCount)
                + "개는 만들어 보기도 전에 걸러졌다.");
    }
}
