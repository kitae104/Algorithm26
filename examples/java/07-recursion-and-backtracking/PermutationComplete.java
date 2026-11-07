public class PermutationComplete {

    static char[] letters = {'a', 'b', 'c'};
    static boolean[] used = new boolean[letters.length];   // 문자 사용(방문) 여부 관리
    static StringBuilder current = new StringBuilder();    // 지금 트리의 어느 노드인지 = 지금까지의 선택
    static int nodeCount = 0;    // 방문한 상태 공간 트리의 노드 수
    static int permCount = 0;    // 완성된 순열(리프) 수

    /** 상태 공간 트리를 깊이 우선으로 훑으며 순열을 만든다. depth = 트리의 깊이 = 호출 스택의 깊이. */
    static void permutation(int depth) {
        nodeCount++;                                       // 이 호출 하나가 트리의 노드 하나
        String indent = "  ".repeat(depth);                // 깊이만큼 들여써서 트리 모양을 만든다

        if (current.length() == letters.length) {          // 종료 조건: 리프(잎) 노드 = 순열 완성
            permCount++;
            System.out.println(indent + current + "   <- 순열 " + permCount);
            return;
        }
        System.out.println(indent + (depth == 0 ? "(시작)" : current.toString()));

        for (int i = 0; i < letters.length; i++) {
            if (used[i]) {
                continue;                                  // 이미 쓴 문자는 가지를 만들지 않는다
            }
            used[i] = true;                                // (1) 선택: 자식 노드로 내려갈 준비
            current.append(letters[i]);
            permutation(depth + 1);                        // (2) 진행: 자식 노드 탐색
            current.deleteCharAt(current.length() - 1);    // (3) 취소: 부모 노드의 상태로 복원
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        System.out.println("{a, b, c}의 상태 공간 트리 (들여쓰기 = 깊이)");
        permutation(0);

        System.out.println();
        System.out.println("방문한 노드(= 재귀 호출 횟수): " + nodeCount + "개");
        System.out.println("그중 리프(순열 완성): " + permCount + "개");
    }
}
