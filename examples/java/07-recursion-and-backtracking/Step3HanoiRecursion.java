public class Step3HanoiRecursion {

    static int moveCount = 0;   // 지금까지 옮긴 횟수

    /** 완성된 하노이 탑. 호출 깊이를 들여쓰기로, 원반 이동을 한 줄로 출력한다. */
    static void hanoi(int n, char from, char to, char via, int depth) {
        String indent = "  ".repeat(depth);   // 깊이 = 호출 스택에 쌓인 프레임 수
        System.out.println(indent + "호출: hanoi(" + n + ", " + from + " -> " + to + ")");

        if (n == 1) {                         // 종료 조건: 원반 1개는 바로 옮긴다
            moveCount++;
            System.out.println(indent + "  이동 " + moveCount + ": 원반 1 (" + from + " -> " + to + ")");
            return;
        }

        hanoi(n - 1, from, via, to, depth + 1);   // 1) 위의 n-1개를 보조 기둥 via로 치운다
        moveCount++;
        System.out.println(indent + "  이동 " + moveCount + ": 원반 " + n + " (" + from + " -> " + to + ")");   // 2) 가장 큰 원반을 옮긴다
        hanoi(n - 1, via, to, from, depth + 1);   // 3) 치워 둔 n-1개를 목적지 위로 옮긴다
    }

    public static void main(String[] args) {
        int n = 3;
        System.out.println("원반 " + n + "개: A -> C (보조 B)");
        hanoi(n, 'A', 'C', 'B', 0);
        System.out.println("총 이동 " + moveCount + "번 (2^" + n + " - 1 = " + ((1 << n) - 1) + ")");
    }
}
