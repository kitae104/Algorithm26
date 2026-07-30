import java.util.ArrayDeque;
import java.util.Queue;

public class BstComplete {

    static class Node {
        int value;
        Node left;
        Node right;

        Node(int value) {
            this.value = value;
        }
    }

    /** 재귀 삽입: 서브트리에 값을 넣고 그 서브트리의 루트를 돌려준다 */
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

    /** 재귀 검색: 있으면 true. 비교 방향을 화면에 남긴다 */
    static boolean search(Node node, int target) {
        if (node == null) {
            return false;                     // 빈 자리까지 내려왔다 → 없다
        }
        System.out.print(node.value + " ");   // 지금 비교하는 노드
        if (target == node.value) {
            return true;
        }
        if (target < node.value) {
            return search(node.left, target);   // 왼쪽 절반만 확인
        }
        return search(node.right, target);      // 오른쪽 절반만 확인
    }

    /** 전위 순회: 자기 자신 → 왼쪽 → 오른쪽 */
    static void preorder(Node node) {
        if (node == null) return;
        System.out.print(node.value + " ");
        preorder(node.left);
        preorder(node.right);
    }

    /** 중위 순회: 왼쪽 → 자기 자신 → 오른쪽 (BST에서는 오름차순!) */
    static void inorder(Node node) {
        if (node == null) return;
        inorder(node.left);
        System.out.print(node.value + " ");
        inorder(node.right);
    }

    /** 후위 순회: 왼쪽 → 오른쪽 → 자기 자신 */
    static void postorder(Node node) {
        if (node == null) return;
        postorder(node.left);
        postorder(node.right);
        System.out.print(node.value + " ");
    }

    /** 레벨 순회: 큐를 사용해 위에서 아래로, 같은 층은 왼쪽에서 오른쪽으로 */
    static void levelOrder(Node root) {
        if (root == null) return;
        Queue<Node> queue = new ArrayDeque<>();   // 6강에서 배운 큐!
        queue.offer(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();             // 앞에서 하나 꺼내고
            System.out.print(node.value + " ");
            if (node.left != null) queue.offer(node.left);    // 자식들을 뒤에 넣는다
            if (node.right != null) queue.offer(node.right);
        }
    }

    public static void main(String[] args) {
        int[] values = {50, 30, 70, 20, 40, 60};

        Node root = null;
        for (int value : values) {
            root = insert(root, value);
        }
        System.out.println("삽입 순서: 50 30 70 20 40 60");
        System.out.println();

        System.out.print("전위 순회 (자신→왼쪽→오른쪽): ");
        preorder(root);
        System.out.println();

        System.out.print("중위 순회 (왼쪽→자신→오른쪽): ");
        inorder(root);
        System.out.println();

        System.out.print("후위 순회 (왼쪽→오른쪽→자신): ");
        postorder(root);
        System.out.println();

        System.out.print("레벨 순회 (위→아래, 큐 사용) : ");
        levelOrder(root);
        System.out.println();
        System.out.println();

        System.out.print("40 검색 — 지나간 노드: ");
        boolean found40 = search(root, 40);
        System.out.println("→ " + (found40 ? "발견" : "없음"));

        System.out.print("65 검색 — 지나간 노드: ");
        boolean found65 = search(root, 65);
        System.out.println("→ " + (found65 ? "발견" : "없음"));
    }
}
