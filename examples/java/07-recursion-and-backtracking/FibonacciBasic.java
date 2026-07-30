public class FibonacciBasic {

    static long callCount = 0;   // fibo가 호출된 총 횟수

    /** 기본 재귀 피보나치. 정의를 그대로 옮겼지만 같은 계산을 여러 번 반복한다. */
    static long fibo(int n) {
        callCount++;
        if (n <= 1) {                        // 종료 조건: fibo(0) = 0, fibo(1) = 1
            return n;
        }
        return fibo(n - 1) + fibo(n - 2);    // 자기 호출이 두 번!
    }

    public static void main(String[] args) {
        int[] tests = {10, 20, 30};

        System.out.println("n  | fibo(n) | 호출 횟수");
        System.out.println("---+---------+----------");
        for (int n : tests) {
            callCount = 0;
            long value = fibo(n);
            System.out.printf("%-2d | %-7d | %d%n", n, value, callCount);
        }

        System.out.println();
        System.out.println("n이 10 커질 때마다 호출 횟수가 약 120배씩 폭발한다.");
        System.out.println("원인: fibo(20)을 구할 때 fibo(18)을 2번, fibo(17)을 3번... 같은 계산을 반복하기 때문.");
        System.out.println("해결책은 11강 동적 계획법에서 배운다 (계산 결과를 저장해 재사용).");
    }
}
