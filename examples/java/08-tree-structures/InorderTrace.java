public class InorderTrace {

    static class Node {
        int value;
        Node left;
        Node right;

        Node(int value) {
            this.value = value;
        }
    }

    static Node insert(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }
        if (value < node.value) {
            node.left = insert(node.left, value);
        } else {
            node.right = insert(node.right, value);
        }
        return node;
    }

    static int visitOrder = 0;                    // 몇 번째로 출력되는지 센다
    static StringBuilder result = new StringBuilder();

    /** 중위 순회를 하면서, 재귀 호출의 시작·출력·복귀를 들여쓰기로 보여 준다 */
    static void inorderTrace(Node node, int depth) {
        String indent = "  ".repeat(depth);       // 깊이만큼 들여쓰기
        if (node == null) {
            System.out.println(indent + "빈 자리(null) → 즉시 되돌아감");
            return;
        }
        System.out.println(indent + "호출: inorder(" + node.value + ")");
        inorderTrace(node.left, depth + 1);       // 1. 왼쪽 먼저

        visitOrder++;
        result.append(node.value).append(" ");
        System.out.println(indent + "출력: " + node.value + "  ← " + visitOrder + "번째 출력");

        inorderTrace(node.right, depth + 1);      // 3. 오른쪽은 마지막
        System.out.println(indent + "복귀: inorder(" + node.value + ") 끝");
    }

    public static void main(String[] args) {
        int[] values = {50, 30, 70, 20};          // 작은 트리로 추적한다

        Node root = null;
        for (int value : values) {
            root = insert(root, value);
        }

        System.out.println("트리: 50의 왼쪽에 30, 오른쪽에 70, 30의 왼쪽에 20");
        System.out.println();
        inorderTrace(root, 0);
        System.out.println();
        System.out.println("중위 순회 최종 출력: " + result.toString().trim());
    }
}
