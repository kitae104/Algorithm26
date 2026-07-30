import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class GraphSearchComplete {

    /** 간선 목록으로 무방향 그래프의 인접 리스트를 만든다 */
    static List<List<Integer>> buildAdjacency(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int v = 0; v < vertexCount; v++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);
            adjacency.get(edge[1]).add(edge[0]);
        }
        return adjacency;
    }

    /** 방법 1: 재귀 DFS — 미방문 이웃을 만나면 즉시 더 깊이 들어간다 */
    static void dfsRecursive(List<List<Integer>> adjacency, int v,
                             boolean[] visited, List<Integer> order) {
        visited[v] = true;
        order.add(v);
        for (int neighbor : adjacency.get(v)) {
            if (!visited[neighbor]) {
                dfsRecursive(adjacency, neighbor, visited, order);
            }
        }
    }

    /** 방법 2: 스택 DFS — 재귀 대신 스택으로 "되돌아갈 곳"을 직접 관리한다 */
    static List<Integer> dfsWithStack(List<List<Integer>> adjacency, int start) {
        boolean[] visited = new boolean[adjacency.size()];
        List<Integer> order = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(start);
        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (visited[v]) {
                continue;   // 스택에 중복으로 들어간 정점은 건너뛴다
            }
            visited[v] = true;
            order.add(v);

            // 재귀 DFS와 같은 방문 순서가 되도록 이웃을 역순으로 push 한다
            List<Integer> neighbors = adjacency.get(v);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                if (!visited[neighbors.get(i)]) {
                    stack.push(neighbors.get(i));
                }
            }
        }
        return order;
    }

    /** 방법 3: 큐 BFS — 가까운 정점부터 물결처럼 넓게 퍼져 나간다 */
    static List<Integer> bfs(List<List<Integer>> adjacency, int start) {
        boolean[] visited = new boolean[adjacency.size()];
        List<Integer> order = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();

        visited[start] = true;   // 큐에 "넣을 때" 방문 표시한다 (중복 삽입 방지)
        queue.offer(start);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);
            for (int neighbor : adjacency.get(v)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
        return order;
    }

    /** 연결 요소 개수: 미방문 정점을 만날 때마다 DFS를 새로 시작한다 */
    static int countComponents(List<List<Integer>> adjacency) {
        boolean[] visited = new boolean[adjacency.size()];
        int count = 0;
        for (int v = 0; v < adjacency.size(); v++) {
            if (!visited[v]) {
                count++;   // 새 무리를 발견했다
                dfsRecursive(adjacency, v, visited, new ArrayList<>());
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int vertexCount = 7;
        int[][] edges = { {0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 5} };
        List<List<Integer>> adjacency = buildAdjacency(vertexCount, edges);

        boolean[] visited = new boolean[vertexCount];
        List<Integer> recursiveOrder = new ArrayList<>();
        dfsRecursive(adjacency, 0, visited, recursiveOrder);

        System.out.println("재귀 DFS 방문 순서: " + recursiveOrder);
        System.out.println("스택 DFS 방문 순서: " + dfsWithStack(adjacency, 0));
        System.out.println("큐 BFS 방문 순서  : " + bfs(adjacency, 0));
        System.out.println("연결 요소 개수    : " + countComponents(adjacency)
                + "개 (0~5번 무리, 홀로 떨어진 6번)");
    }
}
