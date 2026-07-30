public class ClimbStairsComplete {

    /** 방법별 호출/연산 횟수 카운터 */
    static long recursionCalls = 0;
    static long memoCalls = 0;

    /** 방법 1: 순수 재귀 — 점화식 그대로. 느리다(중복 계산). */
    static long waysByRecursion(int n) {
        recursionCalls++;
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        return waysByRecursion(n - 1) + waysByRecursion(n - 2);
    }

    /** 방법 2: 메모이제이션(탑다운) — 계산한 답을 저장한다. */
    static long waysByMemo(int n) {
        long[] memo = new long[n + 1];
        return memoHelper(n, memo);
    }

    static long memoHelper(int n, long[] memo) {
        memoCalls++;
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        if (memo[n] != 0) {
            return memo[n];
        }
        memo[n] = memoHelper(n - 1, memo) + memoHelper(n - 2, memo);
        return memo[n];
    }

    /** 방법 3: 타뷸레이션(바텀업) — 표를 앞에서부터 채운다. */
    static long waysByTable(int n) {
        if (n == 1) {
            return 1;
        }
        long[] dp = new long[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    /** 방법 4: 공간 최적화 — 항상 직전 두 칸만 쓰므로 변수 2개면 충분하다. */
    static long waysByTwoVars(int n) {
        if (n == 1) {
            return 1;
        }
        long prev2 = 1;                     // dp[i - 2] 역할
        long prev1 = 2;                     // dp[i - 1] 역할
        for (int i = 3; i <= n; i++) {
            long current = prev1 + prev2;   // 같은 점화식
            prev2 = prev1;                  // 두 변수를 한 칸씩 밀어 준다
            prev1 = current;
        }
        return prev1;
    }

    public static void main(String[] args) {
        int[] sizes = {10, 20, 30};

        System.out.println("== 계단 오르기: 네 가지 구현 비교 ==");
        System.out.println("n    | 결과      | 재귀 호출    | 메모 호출 | 테이블 덧셈");
        System.out.println("-----+-----------+-------------+----------+------------");

        for (int n : sizes) {
            recursionCalls = 0;
            memoCalls = 0;
            long r1 = waysByRecursion(n);
            long r2 = waysByMemo(n);
            long r3 = waysByTable(n);
            long r4 = waysByTwoVars(n);

            System.out.printf("%-4d | %-9d | %,-11d | %-8d | %d%n",
                    n, r1, recursionCalls, memoCalls, n - 2);

            // 네 방법의 결과가 모두 같은지 정확성을 검증한다 (1강의 원칙)
            if (r1 != r2 || r1 != r3 || r1 != r4) {
                System.out.println("경고: 결과가 서로 다릅니다! 구현에 오류가 있습니다.");
            }
        }

        System.out.println();
        System.out.println("== 큰 입력: n = 50 (순수 재귀는 약 407억 번 호출이라 생략) ==");
        memoCalls = 0;
        System.out.println("메모이제이션 : " + waysByMemo(50) + " (호출 " + memoCalls + "번)");
        System.out.println("타뷸레이션   : " + waysByTable(50) + " (덧셈 48번, 표 51칸)");
        System.out.println("공간 최적화  : " + waysByTwoVars(50) + " (덧셈 48번, 변수 2개)");
        System.out.println();
        System.out.println("시간: O(2^n) → O(n), 공간: 테이블 O(n) → 변수 2개 O(1)");
    }
}
