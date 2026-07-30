public class Step3RecursiveInsert {

    static class Node {
        int value;
        Node left;
        Node right;

        Node(int value) {
            this.value = value;
        }
    }

    /**
     * 재귀 삽입: "node를 루트로 하는 서브트리에 value를 넣고,
     * 완성된 서브트리의 루트를 돌려준다"
     */
    static Node insert(Node node, int value) {
        if (node == null) {
            return new Node(value);          // 빈 자리 도착 → 새 노드가 그 자리의 루트가 된다
        }
        if (value < node.value) {
            node.left = insert(node.left, value);    // 왼쪽 서브트리에 넣고 결과를 다시 연결
        } else {
            node.right = insert(node.right, value);  // 오른쪽 서브트리에 넣고 결과를 다시 연결
        }
        return node;                          // 자기 자신을 그대로 돌려준다
    }

    /** 트리를 왼쪽으로 90도 눕힌 모양으로 출력한다 (오른쪽 자식이 위에 나온다) */
    static void printSideways(Node node, int depth) {
        if (node == null) {
            return;
        }
        printSideways(node.right, depth + 1);
        System.out.println("    ".repeat(depth) + node.value);
        printSideways(node.left, depth + 1);
    }

    public static void main(String[] args) {
        int[] values = {50, 30, 70, 20, 40, 60};

        Node root = null;                     // 빈 트리에서 시작
        for (int value : values) {
            root = insert(root, value);       // 반환값을 반드시 root에 다시 저장!
            System.out.println(value + " 삽입 완료");
        }

        System.out.println();
        System.out.println("완성된 트리 (왼쪽으로 90도 눕힌 모양, 오른쪽 자식이 위):");
        printSideways(root, 0);
    }
}
