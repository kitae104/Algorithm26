import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class BfsShortestPath {

    /** 간선: 도착 정점과 가중치. BFS는 가중치를 무시한다 — 비교 출력에만 사용한다 */
    static class Edge {
        int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static void addEdge(List<List<Edge>> graph, int u, int v, int weight) {
        graph.get(u).add(new Edge(v, weight));
        graph.get(v).add(new Edge(u, weight));
    }

    /** u-v 간선의 가중치를 찾는다 (경로의 이동 시간 합을 계산할 때 사용) */
    static int findWeight(List<List<Edge>> graph, int u, int v) {
        for (Edge e : graph.get(u)) {
            if (e.to == v) {
                return e.weight;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        String[] names = {"A", "B", "C", "D", "E", "F"};
        int n = names.length;

        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        addEdge(graph, 0, 1, 3);  // A-B
        addEdge(graph, 0, 2, 7);  // A-C
        addEdge(graph, 1, 2, 2);  // B-C
        addEdge(graph, 1, 3, 6);  // B-D
        addEdge(graph, 2, 3, 4);  // C-D
        addEdge(graph, 2, 4, 8);  // C-E
        addEdge(graph, 3, 4, 1);  // D-E
        addEdge(graph, 3, 5, 5);  // D-F
        addEdge(graph, 4, 5, 3);  // E-F

        // 9강에서 배운 BFS 최단 거리: 방문 순서대로 "간선 수"가 확정된다
        int start = 0;
        int[] hops = new int[n];      // 최소 간선 수 (-1 = 아직 방문 안 함)
        int[] prev = new int[n];      // 경로 복원용 직전 정점
        Arrays.fill(hops, -1);
        Arrays.fill(prev, -1);

        Queue<Integer> queue = new ArrayDeque<>();
        hops[start] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge e : graph.get(u)) {
                if (hops[e.to] == -1) {          // 처음 도착한 정점만
                    hops[e.to] = hops[u] + 1;    // 간선 수 = 직전 정점 + 1
                    prev[e.to] = u;
                    queue.add(e.to);
                }
            }
        }

        System.out.println("[BFS] A에서 각 정점까지의 최소 간선 수:");
        for (int v = 0; v < n; v++) {
            System.out.println("  A -> " + names[v] + " : 간선 " + hops[v] + "개");
        }

        // BFS가 찾은 A -> C 경로를 복원하고, 그 경로의 "이동 시간 합"을 계산해 본다
        int target = 2;   // C
        StringBuilder path = new StringBuilder(names[target]);
        int timeSum = 0;
        int v = target;
        while (prev[v] != -1) {
            timeSum += findWeight(graph, prev[v], v);
            v = prev[v];
            path.insert(0, names[v] + " -> ");
        }

        System.out.println();
        System.out.println("BFS가 찾은 A -> C 경로: " + path
                + " (간선 " + hops[target] + "개, 이동 시간 합 " + timeSum + "분)");
        System.out.println("하지만 A -> B -> C 로 가면 3 + 2 = 5분이다!");
        System.out.println("BFS는 간선 수가 가장 적은 경로를 찾을 뿐,");
        System.out.println("가중치(이동 시간)의 합이 최소인 경로는 보장하지 못한다.");
    }
}
