import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraComplete {

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

    /** 다익스트라 결과: 최단 거리 배열 + 경로 복원용 직전 정점 배열 */
    static class Result {
        int[] dist;
        int[] prev;

        Result(int[] dist, int[] prev) {
            this.dist = dist;
            this.prev = prev;
        }
    }

    static void addEdge(List<List<Edge>> graph, int u, int v, int weight) {
        graph.get(u).add(new Edge(v, weight));
        graph.get(v).add(new Edge(u, weight));
    }

    /**
     * 우선순위 큐 버전 다익스트라.
     * 큐 항목은 {거리, 정점 번호} 배열 — 거리가 작은 항목부터 나온다(10강의 PriorityQueue).
     * pickClosest의 O(V) 훑기를 큐의 O(log V) 꺼내기로 바꾼 것이다.
     */
    static Result dijkstra(List<List<Edge>> graph, int start) {
        int n = graph.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(item -> item[0]));
        pq.add(new int[]{0, start});

        while (!pq.isEmpty()) {
            int[] item = pq.poll();      // 미확정 정점 중 거리가 가장 작은 항목
            int d = item[0];
            int u = item[1];
            if (visited[u]) {
                continue;                // 이미 확정된 정점의 낡은 항목은 건너뛴다
            }
            visited[u] = true;           // u까지의 최단 거리 확정

            for (Edge e : graph.get(u)) {
                int newDist = d + e.weight;
                if (newDist < dist[e.to]) {              // 완화 조건
                    dist[e.to] = newDist;
                    prev[e.to] = u;                      // 경로 복원용: 어디서 왔는지 기록
                    pq.add(new int[]{newDist, e.to});    // 갱신된 "새" 거리로 넣는다
                }
            }
        }
        return new Result(dist, prev);
    }

    /** prev 배열을 도착점에서 출발점까지 거슬러 올라가 경로 문자열을 만든다 */
    static String buildPath(int[] prev, String[] names, int end) {
        StringBuilder path = new StringBuilder(names[end]);
        int v = end;
        while (prev[v] != -1) {
            v = prev[v];
            path.insert(0, names[v] + " -> ");
        }
        return path.toString();
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

        int start = 0;
        Result result = dijkstra(graph, start);

        System.out.println("출발 정점: " + names[start]);
        System.out.println("도착 | 최단 거리 | 경로");
        System.out.println("-----+----------+------------------------------");
        for (int v = 0; v < n; v++) {
            System.out.printf("   %s | %8s | %s%n",
                    names[v], show(result.dist[v]), buildPath(result.prev, names, v));
        }
    }
}
