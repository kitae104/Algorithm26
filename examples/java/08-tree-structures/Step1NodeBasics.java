public class Step1NodeBasics {

    // Node 클래스는 같은 폴더의 Node.java에 정의되어 있다.

    public static void main(String[] args) {
        // 노드 3개를 만들어 손으로 직접 연결한다
        Node root = new Node(50);        // 루트 노드
        Node leftChild = new Node(30);   // 왼쪽 자식이 될 노드
        Node rightChild = new Node(70);  // 오른쪽 자식이 될 노드

        root.left = leftChild;    // 50의 왼쪽에 30을 연결
        root.right = rightChild;  // 50의 오른쪽에 70을 연결

        System.out.println("루트 노드의 값: " + root.value);
        System.out.println("루트의 왼쪽 자식: " + root.left.value);
        System.out.println("루트의 오른쪽 자식: " + root.right.value);

        // 자식이 없는 노드(리프 노드)인지 확인한다
        boolean isLeaf = (leftChild.left == null && leftChild.right == null);
        System.out.println("30은 리프 노드인가? " + isLeaf);
        System.out.println("50은 리프 노드인가? " + (root.left == null && root.right == null));
    }
}
