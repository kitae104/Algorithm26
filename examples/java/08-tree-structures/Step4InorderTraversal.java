public class Step4InorderTraversal {

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

    /** 중위 순회: 왼쪽 서브트리 → 자기 자신 → 오른쪽 서브트리 */
    static void inorder(Node node) {
        if (node == null) {
            return;                    // 종료 조건: 빈 자리에 도착하면 되돌아간다
        }
        inorder(node.left);            // 1. 왼쪽 서브트리를 먼저 전부 방문
        System.out.print(node.value + " ");  // 2. 자기 자신을 출력
        inorder(node.right);           // 3. 오른쪽 서브트리를 방문
    }

    public static void main(String[] args) {
        int[] values = {50, 30, 70, 20, 40, 60};

        Node root = null;
        for (int value : values) {
            root = insert(root, value);
        }

        System.out.println("삽입 순서   : 50 30 70 20 40 60");
        System.out.print("중위 순회 결과: ");
        inorder(root);
        System.out.println();
        System.out.println("→ 중위 순회는 BST의 값을 항상 오름차순으로 출력한다!");
    }
}
