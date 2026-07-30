import java.util.ArrayList;
import java.util.List;

public class Step3VisitNeighbor {
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

        boolean[] visited = new boolean[vertexCount];
        List<Integer> visitOrder = new ArrayList<>();

        // 출발 정점 0번을 방문한다
        int current = 0;
        visited[current] = true;
        visitOrder.add(current);
        System.out.println(current + "번 방문. 이웃 목록: " + adjacency.get(current));

        // current의 이웃 중 "아직 방문하지 않은 첫 번째 이웃"으로 딱 한 걸음 이동한다
        for (int neighbor : adjacency.get(current)) {
            if (!visited[neighbor]) {
                System.out.println("  → 미방문 이웃 " + neighbor + "번 발견, 이동합니다.");
                current = neighbor;
                visited[current] = true;
                visitOrder.add(current);
                break;   // 이번 단계에서는 한 걸음만
            }
        }

        System.out.println(current + "번 방문. 이웃 목록: " + adjacency.get(current));
        System.out.println();
        System.out.println("지금까지의 방문 순서: " + visitOrder);
        System.out.println("이 \"한 걸음\"을 이웃의 이웃으로 계속 반복하는 것이 곧 DFS입니다.");
    }
}
