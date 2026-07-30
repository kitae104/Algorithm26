import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DijkstraTrace {

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

    /** 거리 배열 한 줄을 표 형태로 출력한다 */
    static void printRow(String label, String confirmed, int[] dist) {
        StringBuilder row = new StringBuilder();
        row.append(String.format("%-4s | %-4s |", label, confirmed));
        for (int d : dist) {
            row.append(String.format(" %4s", show(d)));
        }
        System.out.println(row);
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

        int start = 0;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, INF);
        dist[start] = 0;

        System.out.println("A 출발 다익스트라 — 회차별 거리 배열의 변화");
        StringBuilder header = new StringBuilder("회차 | 확정 |");
        for (String name : names) {
            header.append(String.format(" %4s", name));
        }
        System.out.println(header);
        System.out.println("-----+------+------------------------------");
        printRow("시작", "-", dist);

        for (int round = 1; round <= n; round++) {
            int u = pickClosest(dist, visited);
            if (u == -1) {
                break;
            }
            visited[u] = true;
            for (Edge e : graph.get(u)) {
                if (dist[u] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[u] + e.weight;   // 완화
                }
            }
            printRow(String.valueOf(round), names[u], dist);
        }

        System.out.println();
        System.out.println("한 번 확정된 정점의 거리는 이후 어떤 회차에서도 변하지 않는다.");
        System.out.println("확정 순서 = 거리가 작은 순서 (A 0, B 3, C 5, D 9, E 10, F 13)");
    }
}
