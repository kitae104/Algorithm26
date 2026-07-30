public class PasswordStarter {

    static char[] candidates = {'1', '3', '7', '9'};   // 후보 문자 4개
    static StringBuilder current = new StringBuilder(); // 지금 만드는 중인 비밀번호
    static int allCount = 0;        // 전체 생성 개수
    static int rulePassCount = 0;   // 가지치기 통과 개수
    static boolean printEach = true; // 하나씩 출력할지 여부 (개수만 셀 때는 false)

    /** [과제 1] 길이 k의 모든 비밀번호를 생성한다. (같은 문자를 여러 번 써도 됨) */
    static void generateAll(int k) {
        // TODO 1: 종료 조건 — current의 길이가 k가 되면 allCount를 1 늘리고,
        //         printEach가 true일 때만 "  " + current를 출력한 뒤 return 하세요.

        // TODO 2: 후보 문자를 하나씩 골라
        //         (1) current.append(...)로 붙이고        [선택]
        //         (2) generateAll(k)을 다시 호출한 뒤      [진행]
        //         (3) current.deleteCharAt(...)으로 되돌리세요. [취소]
    }

    /** [과제 2] 같은 문자가 연속으로 나오는 가지를 잘라내며 생성한다. */
    static void generateNoRepeat(int k) {
        // TODO 3: 종료 조건은 과제 1과 같습니다. (rulePassCount를 늘리세요)

        // TODO 4: 문자를 붙이기 전에 "current의 마지막 문자와 같은가"를 검사해서
        //         같으면 continue로 그 가지를 건너뛰세요. (가지치기)
        //         나머지 선택 -> 진행 -> 취소 구조는 과제 1과 같습니다.
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
        // TODO 5: 가지치기로 걸러진 개수(allCount - rulePassCount)도 출력해 보세요.
    }
}
