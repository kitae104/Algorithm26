/** 간선: 도착 정점과 가중치. BFS는 가중치를 무시한다 — 비교 출력에만 사용한다 */
public class Edge {
    int to;
    int weight;

    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}
