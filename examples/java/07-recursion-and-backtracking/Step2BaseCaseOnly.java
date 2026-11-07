public class Step2BaseCaseOnly {

    static int moveCount = 0;   // 지금까지 옮긴 횟수

    /** 종료 조건(base case)만 있는 하노이 탑 뼈대. 아직 자기 호출은 없다. */
    static void hanoi(int n, char from, char to, char via) {
        if (n == 1) {                  // 종료 조건: 원반이 1개면 바로 옮기면 끝
            moveCount++;
            System.out.println("이동 " + moveCount + ": 원반 1 (" + from + " -> " + to + ")");
            return;
        }
        // 다음 단계에서 여기에 자기 호출(원반 n-1개 옮기기)을 추가한다
        System.out.println("원반 " + n + "개는 아직 옮기지 못함 (자기 호출이 없어서)");
    }

    public static void main(String[] args) {
        System.out.println("[원반 1개]");
        hanoi(1, 'A', 'C', 'B');

        System.out.println();
        System.out.println("[원반 3개]");
        hanoi(3, 'A', 'C', 'B');
    }
}
