import java.util.ArrayList;
import java.util.List;

public class Step2VisitOne {
    public static void main(String[] args) {
        int vertexCount = 7;
        int[][] edges = { {0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 5} };

        List<List<Integer>> adjacency = new ArrayList<>();
        for (int v = 0; v < vertexCount; v++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }

        // 방문 배열: 정점 번호를 인덱스로 사용한다. 처음에는 모두 false(미방문)
        boolean[] visited = new boolean[vertexCount];

        int start = 0;
        visited[start] = true;   // 핵심 연산: 정점 하나를 "방문했다"고 표시한다

        System.out.println(start + "번 정점을 방문했습니다.");
        System.out.println(start + "번 정점의 이웃 목록: " + adjacency.get(start));
        System.out.println();
        System.out.println("방문 배열 상태 (O = 방문, X = 미방문)");
        for (int v = 0; v < vertexCount; v++) {
            System.out.println("  visited[" + v + "] = " + (visited[v] ? "O" : "X"));
        }
    }
}
