public class Step3FactorialRecursion {

    /** 완성된 재귀 팩토리얼. 호출과 반환의 순서를 함께 출력한다. */
    static long factorial(int n) {
        System.out.println("호출: factorial(" + n + ")");

        if (n <= 1) {                        // 종료 조건
            System.out.println("종료 조건 도달 -> 1을 반환");
            return 1;
        }

        long smaller = factorial(n - 1);     // 자기 호출: 더 작은 문제를 맡긴다
        long result = n * smaller;           // 돌아온 답으로 내 답을 완성한다

        System.out.println("반환: factorial(" + n + ") = " + n + " x " + smaller + " = " + result);
        return result;
    }

    public static void main(String[] args) {
        long answer = factorial(4);
        System.out.println("최종 결과: 4! = " + answer);
    }
}
