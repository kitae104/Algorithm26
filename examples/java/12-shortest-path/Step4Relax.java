import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Step4Relax {

    static final int INF = Integer.MAX_VALUE;

    /** 간선 정보: 도착 정점 번호와 가중치(이동 시간) */
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

    /** 미확정 정점 중 거리가 가장 작은 정점 번호. 없으면 -1 (Step3과 동일) */
    static int pickClosest(int[] dist, boolean[] visited) {
        int minVertex = -1;
        int minDist = INF;
        for (int v = 0; v < dist.length; v++) {
            if (!visited[v] && dist[v] < minDist) {
                minDist = dist[v];
                minVertex = v;
            }
        }
        return minVertex;
    }

    static String show(int d) {
        return d == INF ? "∞" : String.valueOf(d);
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

        int start = 0;                    // 출발 정점 A
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, INF);
        dist[start] = 0;

        // 정점 수만큼 반복: 뽑기(그리디) -> 확정 -> 완화(거리 갱신)
        for (int round = 1; round <= n; round++) {
            int u = pickClosest(dist, visited);
            if (u == -1) {
                break;                    // 더 도달할 수 있는 정점이 없다
            }
            visited[u] = true;            // u까지의 최단 거리 확정
            System.out.println(round + "회차 확정: " + names[u] + " (거리 " + dist[u] + ")");

            // u를 거쳐 가면 더 짧아지는 이웃의 거리를 갱신한다 = 완화(relaxation)
            // (pickClosest가 INF인 정점은 뽑지 않으므로 dist[u] + weight는 넘치지 않는다)
            for (Edge e : graph.get(u)) {
                if (dist[u] + e.weight < dist[e.to]) {
                    System.out.println("    완화: dist[" + names[e.to] + "] "
                            + show(dist[e.to]) + " -> " + (dist[u] + e.weight)
                            + "  (" + names[u] + " 경유)");
                    dist[e.to] = dist[u] + e.weight;
                }
            }
        }

        System.out.println();
        System.out.println("A에서 각 정점까지의 최단 거리:");
        for (int v = 0; v < n; v++) {
            System.out.println("  A -> " + names[v] + " : " + show(dist[v]));
        }
    }
}
