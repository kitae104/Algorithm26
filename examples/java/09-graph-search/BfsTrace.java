import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BfsTrace {

    /** 방문 배열을 "OXXXXXX" 형태의 문자열로 만든다 (O = 방문, X = 미방문) */
    static String visitedText(boolean[] visited) {
        StringBuilder sb = new StringBuilder();
        for (boolean v : visited) {
            sb.append(v ? "O" : "X");
        }
        return sb.toString();
    }

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

        int start = 0;
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> queue = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        visited[start] = true;
        queue.offer(start);
        System.out.println("[BFS 실행 추적] 시작 정점: " + start + " (방문 배열은 0~6번 순서)");
        System.out.println("준비   | 큐에 0 넣고 방문 표시      | 큐 " + queue
                + " | 방문 " + visitedText(visited));

        int stepNo = 0;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);

            List<Integer> added = new ArrayList<>();
            for (int neighbor : adjacency.get(v)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;   // 큐에 넣는 순간 방문 표시!
                    queue.offer(neighbor);
                    added.add(neighbor);
                }
            }
            stepNo++;
            System.out.printf("%d단계  | 꺼냄 %d, 새로 넣음 %-7s | 큐 %-6s | 방문 %s%n",
                    stepNo, v, added, queue, visitedText(visited));
        }

        System.out.println();
        System.out.println("큐가 비어 탐색 종료. BFS 방문 순서: " + order);
        System.out.println("시작 정점에서 가까운 순서(0다리 → 1다리 → 2다리 → 3다리)로 방문했습니다.");
    }
}
