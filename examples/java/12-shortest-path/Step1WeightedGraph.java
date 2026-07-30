import java.util.ArrayList;
import java.util.List;

public class Step1WeightedGraph {

    /** 간선 정보: 도착 정점 번호와 가중치(이동 시간) */
    static class Edge {
        int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /** 무방향 가중치 간선을 양쪽 인접 리스트에 모두 추가한다 */
    static void addEdge(List<List<Edge>> graph, int u, int v, int weight) {
        graph.get(u).add(new Edge(v, weight));
        graph.get(v).add(new Edge(u, weight));
    }

    public static void main(String[] args) {
        String[] names = {"A", "B", "C", "D", "E", "F"};
        int vertexCount = names.length;

        // 정점 수만큼 빈 인접 리스트를 준비한다
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            graph.add(new ArrayList<>());
        }

        // 간선 9개: (정점, 정점, 가중치 = 이동 시간)
        addEdge(graph, 0, 1, 3);  // A-B
        addEdge(graph, 0, 2, 7);  // A-C
        addEdge(graph, 1, 2, 2);  // B-C
        addEdge(graph, 1, 3, 6);  // B-D
        addEdge(graph, 2, 3, 4);  // C-D
        addEdge(graph, 2, 4, 8);  // C-E
        addEdge(graph, 3, 4, 1);  // D-E
        addEdge(graph, 3, 5, 5);  // D-F
        addEdge(graph, 4, 5, 3);  // E-F

        System.out.println("정점 수: " + vertexCount + ", 간선 수: 9");
        for (int u = 0; u < vertexCount; u++) {
            StringBuilder line = new StringBuilder(names[u] + ":");
            for (Edge e : graph.get(u)) {
                line.append(" (").append(names[e.to]).append(", ").append(e.weight).append(")");
            }
            System.out.println(line);
        }
    }
}
