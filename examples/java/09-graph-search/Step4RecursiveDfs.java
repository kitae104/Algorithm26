import java.util.ArrayList;
import java.util.List;

public class Step4RecursiveDfs {

    static List<List<Integer>> adjacency = new ArrayList<>();
    static boolean[] visited;
    static List<Integer> visitOrder = new ArrayList<>();

    /** 재귀 DFS: 정점 v를 방문하고, 미방문 이웃마다 자기 자신을 다시 호출한다 */
    static void dfs(int v) {
        visited[v] = true;       // 1) 방문 표시 (순환 그래프에서 무한 재귀를 막는다)
        visitOrder.add(v);       // 2) 방문 순서 기록
        System.out.println("dfs(" + v + ") 호출 — " + v + "번 방문");

        for (int neighbor : adjacency.get(v)) {
            if (!visited[neighbor]) {   // 3) 미방문 이웃으로만 더 깊이 들어간다
                dfs(neighbor);
            }
        }
    }

    public static void main(String[] args) {
        int vertexCount = 7;
        int[][] edges = { {0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 5} };
        for (int v = 0; v < vertexCount; v++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }
        visited = new boolean[vertexCount];

        dfs(0);

        System.out.println();
        StringBuilder order = new StringBuilder();
        for (int i = 0; i < visitOrder.size(); i++) {
            if (i > 0) order.append(" → ");
            order.append(visitOrder.get(i));
        }
        System.out.println("DFS 방문 순서: " + order);
        System.out.println("방문한 정점 수: " + visitOrder.size() + " / 전체 " + vertexCount
                + "개 (6번은 간선이 없어 도달하지 못함)");
    }
}
