public class Step2FibonacciMemo {

    /** 재귀 호출 횟수 카운터 */
    static long callCount = 0;

    /** memo[k] = 이미 계산한 fibo(k)의 답 (0이면 아직 계산 전) */
    static long[] memo;

    /** 메모이제이션(탑다운): 계산한 답을 저장하고, 두 번째부터는 즉시 꺼내 쓴다 */
    static long fibo(int n) {
        callCount++;
        if (n <= 1) {
            return n;                       // 기저 조건: F(0) = 0, F(1) = 1
        }
        if (memo[n] != 0) {
            return memo[n];                 // 이미 계산한 답 → 다시 계산하지 않는다
        }
        memo[n] = fibo(n - 1) + fibo(n - 2);   // 점화식의 결과를 저장한 뒤
        return memo[n];                        // 반환한다
    }

    /** 순수 재귀였다면 필요한 호출 횟수: 2 x F(n+1) - 1 (Step1의 실측값과 같은 식) */
    static long naiveCallCount(int n) {
        long prev = 0, curr = 1;            // F(0), F(1)
        for (int i = 2; i <= n + 1; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return 2 * curr - 1;                // curr = F(n+1)
    }

    public static void main(String[] args) {
        int[] sizes = {10, 20, 30, 35, 50};

        System.out.println("n    | fibo(n)        | 메모 호출 횟수 | 순수 재귀라면");
        System.out.println("-----+----------------+---------------+------------------");

        for (int n : sizes) {
            callCount = 0;
            memo = new long[n + 1];         // 답을 저장할 공간 (0번~n번 → 크기 n + 1)
            long result = fibo(n);
            System.out.printf("%-4d | %-14d | %-13d | %,d%n",
                    n, result, callCount, naiveCallCount(n));
        }

        System.out.println();
        System.out.println("호출 횟수가 2n - 1로 줄었다: 각 fibo(k)를 딱 한 번만 계산하기 때문이다.");
        System.out.println("n=50이면 약 407억 번이 99번이 된다 — 저장 공간 O(n)과 바꾼 속도다.");
    }
}
