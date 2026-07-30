public class Step1FibonacciNaive {

    /** 재귀 호출 횟수 카운터 (1강의 연산 카운터를 재귀에 적용) */
    static long callCount = 0;

    /** 순수 재귀 피보나치: 점화식 F(n) = F(n-1) + F(n-2)를 그대로 옮긴 코드 */
    static long fibo(int n) {
        callCount++;                        // 호출될 때마다 1 증가
        if (n <= 1) {
            return n;                       // 기저 조건: F(0) = 0, F(1) = 1
        }
        return fibo(n - 1) + fibo(n - 2);   // 점화식(전이): 두 작은 문제의 답을 더한다
    }

    public static void main(String[] args) {
        int[] sizes = {10, 20, 30, 35};

        System.out.println("n    | fibo(n)    | 재귀 호출 횟수");
        System.out.println("-----+------------+---------------");

        for (int n : sizes) {
            callCount = 0;                  // 매번 카운터를 초기화한다
            long result = fibo(n);
            System.out.printf("%-4d | %-10d | %,d%n", n, result, callCount);
        }

        System.out.println();
        System.out.println("n이 5 커질 때마다 호출 횟수가 약 11배씩 늘어난다.");
        System.out.println("같은 fibo(k)를 몇 번이고 다시 계산하기 때문이다 — 중복 부분 문제!");
    }
}
