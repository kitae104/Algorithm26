public class Step2BaseCaseOnly {

    /** 종료 조건(base case)만 있는 팩토리얼 뼈대. 아직 자기 호출은 없다. */
    static long factorial(int n) {
        if (n <= 1) {      // 종료 조건: 더 이상 쪼갤 수 없는 가장 작은 문제
            return 1;      // 1! = 1 (0! 도 1로 처리)
        }
        // 다음 단계에서 여기에 자기 호출을 추가한다
        return -1;         // "아직 계산하지 못함"을 나타내는 임시 값
    }

    public static void main(String[] args) {
        System.out.println("factorial(0) = " + factorial(0));
        System.out.println("factorial(1) = " + factorial(1));
        System.out.println("factorial(4) = " + factorial(4) + "  <- 아직 -1: 자기 호출이 없어서");
    }
}
