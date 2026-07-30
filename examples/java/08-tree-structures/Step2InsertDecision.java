public class Step2InsertDecision {

    static class Node {
        int value;
        Node left;
        Node right;

        Node(int value) {
            this.value = value;
        }
    }

    /** 한 번의 삽입 판단: 현재 노드와 비교해서 왼쪽 또는 오른쪽에 붙인다 */
    static void insertOneStep(Node current, int newValue) {
        System.out.println("새 값 " + newValue + "을(를) 노드 " + current.value + "과(와) 비교합니다.");

        if (newValue < current.value) {
            System.out.println("  " + newValue + " < " + current.value + " → 왼쪽으로 가야 합니다.");
            if (current.left == null) {
                current.left = new Node(newValue);   // 빈 자리에 새 노드를 연결
                System.out.println("  왼쪽이 비어 있으므로 왼쪽 자식으로 연결했습니다.");
            } else {
                System.out.println("  왼쪽에 이미 " + current.left.value + "이(가) 있습니다. (다음 단계에서 더 내려갑니다)");
            }
        } else {
            System.out.println("  " + newValue + " >= " + current.value + " → 오른쪽으로 가야 합니다.");
            if (current.right == null) {
                current.right = new Node(newValue);
                System.out.println("  오른쪽이 비어 있으므로 오른쪽 자식으로 연결했습니다.");
            } else {
                System.out.println("  오른쪽에 이미 " + current.right.value + "이(가) 있습니다. (다음 단계에서 더 내려갑니다)");
            }
        }
    }

    public static void main(String[] args) {
        Node root = new Node(50);   // 루트만 있는 트리에서 시작

        insertOneStep(root, 30);    // 50과 비교 → 왼쪽에 붙는다
        insertOneStep(root, 70);    // 50과 비교 → 오른쪽에 붙는다
        insertOneStep(root, 20);    // 왼쪽에 이미 30이 있어 한 번의 판단으로는 부족하다!

        System.out.println();
        System.out.println("현재 트리: 루트 " + root.value
                + ", 왼쪽 " + root.left.value + ", 오른쪽 " + root.right.value);
        System.out.println("20은 아직 넣지 못했습니다 → 반복해서 내려가는 구조(재귀)가 필요합니다.");
    }
}
