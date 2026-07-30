import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class SubwayRouteApplication {

    /** BFS로 start에서 모든 역까지의 최소 이동 정거장 수와 "직전 역"을 계산한다 */
    static int[][] bfsDistanceAndPrevious(List<List<Integer>> adjacency, int start) {
        int stationCount = adjacency.size();
        int[] distance = new int[stationCount];
        int[] previous = new int[stationCount];
        Arrays.fill(distance, -1);   // -1 = 아직 도달하지 못함 (방문 배열 역할도 겸한다)
        Arrays.fill(previous, -1);

        Deque<Integer> queue = new ArrayDeque<>();
        distance[start] = 0;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int neighbor : adjacency.get(v)) {
                if (distance[neighbor] == -1) {
                    distance[neighbor] = distance[v] + 1;   // 한 정거장 더 간 거리
                    previous[neighbor] = v;                 // 어디서 왔는지 기록
                    queue.offer(neighbor);
                }
            }
        }
        return new int[][] { distance, previous };
    }

    /** previous 배열을 목적지에서 출발지까지 거슬러 올라가 경로를 복원한다 */
    static List<Integer> buildPath(int[] previous, int goal) {
        List<Integer> path = new ArrayList<>();
        for (int v = goal; v != -1; v = previous[v]) {
            path.add(0, v);   // 앞쪽에 끼워 넣어 출발지 → 목적지 순서로 만든다
        }
        return path;
    }

    public static void main(String[] args) {
        // 지하철 노선 소그래프: 역 이름 배열 + 역 사이 연결(간선)
        String[] stations = {"시청", "강변", "공원", "대학", "터미널", "시장", "병원"};
        int[][] links = { {0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {2, 5}, {5, 6}, {4, 6} };

        List<List<Integer>> adjacency = new ArrayList<>();
        for (int v = 0; v < stations.length; v++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] link : links) {
            adjacency.get(link[0]).add(link[1]);
            adjacency.get(link[1]).add(link[0]);
        }

        int start = 0;   // 시청역에서 출발
        int[][] result = bfsDistanceAndPrevious(adjacency, start);
        int[] distance = result[0];
        int[] previous = result[1];

        System.out.println("[" + stations[start] + "역에서 각 역까지의 최소 이동 정거장 수]");
        for (int v = 0; v < stations.length; v++) {
            System.out.println("  " + stations[v] + " : " + distance[v] + "정거장");
        }

        int goal = 6;   // 병원역까지 가는 최소 경로
        List<Integer> path = buildPath(previous, goal);
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                route.append(" → ");
            }
            route.append(stations[path.get(i)]);
        }
        System.out.println();
        System.out.println("[" + stations[start] + " → " + stations[goal] + " 최소 이동 경로]");
        System.out.println("  " + route + "  (" + distance[goal] + "번 이동)");
    }
}
