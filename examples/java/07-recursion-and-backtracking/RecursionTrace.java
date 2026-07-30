public class RecursionTrace {

    static char[] letters = {'a', 'b', 'c'};
    static boolean[] used = new boolean[letters.length];
    static StringBuilder current = new StringBuilder();
    static final int K = 2;   // 만들 순열의 길이

    /** 재귀 깊이만큼 들여쓰기해서 호출·완성·취소를 모두 기록한다. */
    static void perm(int depth) {
        String indent = "  ".repeat(depth);   // 깊이 1당 공백 2칸
        System.out.println(indent + "-> perm(현재=\"" + current + "\")");

        if (current.length() == K) {          // 종료 조건
            System.out.println(indent + "  완성! \"" + current + "\"");
            return;
        }

        for (int i = 0; i < letters.length; i++) {
            if (used[i]) continue;
            used[i] = true;                                // 선택
            current.append(letters[i]);
            perm(depth + 1);                               // 진행
            current.deleteCharAt(current.length() - 1);    // 취소
            used[i] = false;
            System.out.println(indent + "<- 취소 " + letters[i] + " (현재=\"" + current + "\")");
        }
    }

    public static void main(String[] args) {
        System.out.println("{a, b, c}에서 길이 2 순열 만들기 — 호출/반환 추적");
        perm(0);
        System.out.println("추적 끝: 들여쓰기 깊이 = 호출 스택의 깊이");
    }
}
