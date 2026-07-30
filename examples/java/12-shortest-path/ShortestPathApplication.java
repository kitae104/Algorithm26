import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ShortestPathApplication {

    static final int INF = Integer.MAX_VALUE;

    /** 간선 정보: 도착 도시 번호와 이동 시간(분) */
    static class Edge {
        int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /** 다익스트라 결과: 최단 시간 배열 + 경로 복원용 직전 도시 배열 */
    static class Result {
        int[] dist;
        int[] prev;

        Result(int[] dist, int[] prev) {
            this.dist = dist;
            this.prev = prev;
        }
    }

    static void addRoad(List<List<Edge>> map, int u, int v, int minutes) {
        map.get(u).add(new Edge(v, minutes));
        map.get(v).add(new Edge(u, minutes));
    }

    /** 우선순위 큐 버전 다익스트라 (DijkstraComplete와 같은 구조) */
    static Result dijkstra(List<List<Edge>> map, int start) {
        int n = map.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(item -> item[0]));
        pq.add(new int[]{0, start});

        while (!pq.isEmpty()) {
            int[] item = pq.poll();
            int d = item[0];
            int u = item[1];
            if (visited[u]) {
                continue;
            }
            visited[u] = true;

            for (Edge e : map.get(u)) {
                int newDist = d + e.weight;
                if (newDist < dist[e.to]) {
                    dist[e.to] = newDist;
                    prev[e.to] = u;
                    pq.add(new int[]{newDist, e.to});
                }
            }
        }
        return new Result(dist, prev);
    }

    static String buildPath(int[] prev, String[] names, int end) {
        StringBuilder path = new StringBuilder(names[end]);
        int v = end;
        while (prev[v] != -1) {
            v = prev[v];
            path.insert(0, names[v] + " -> ");
        }
        return path.toString();
    }

    /** 분 단위 시간을 "n시간 m분" 형태로 바꾼다 */
    static String formatMinutes(int minutes) {
        if (minutes == INF) {
            return "도달 불가";
        }
        if (minutes < 60) {
            return minutes + "분";
        }
        if (minutes % 60 == 0) {
            return (minutes / 60) + "시간";
        }
        return (minutes / 60) + "시간 " + (minutes % 60) + "분";
    }

    public static void main(String[] args) {
        // 도시 8개와 고속버스 노선 11개 (이동 시간: 분)
        String[] cities = {"서울", "인천", "대전", "전주", "광주", "대구", "울산", "부산"};
        int n = cities.length;

        List<List<Edge>> map = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            map.add(new ArrayList<>());
        }
        addRoad(map, 0, 1, 40);   // 서울-인천
        addRoad(map, 0, 2, 65);   // 서울-대전
        addRoad(map, 1, 2, 95);   // 인천-대전
        addRoad(map, 2, 3, 50);   // 대전-전주
        addRoad(map, 2, 5, 70);   // 대전-대구
        addRoad(map, 3, 4, 45);   // 전주-광주
        addRoad(map, 3, 5, 85);   // 전주-대구
        addRoad(map, 4, 7, 140);  // 광주-부산
        addRoad(map, 5, 6, 50);   // 대구-울산
        addRoad(map, 5, 7, 65);   // 대구-부산
        addRoad(map, 6, 7, 30);   // 울산-부산

        int start = 0;   // 서울
        Result result = dijkstra(map, start);

        System.out.println("[고속버스 내비게이션] 출발: " + cities[start]);
        System.out.println();
        System.out.println("도착 | 소요 시간     | 추천 경로");
        System.out.println("-----+--------------+------------------------------");
        for (int v = 0; v < n; v++) {
            if (v == start) {
                continue;
            }
            System.out.printf("%s | %-12s | %s%n",
                    cities[v], formatMinutes(result.dist[v]),
                    buildPath(result.prev, cities, v));
        }

        System.out.println();
        System.out.println("가장 오래 걸리는 도시: " + cities[7] + " ("
                + formatMinutes(result.dist[7]) + ")");
        System.out.println("부산행은 광주 경유(65+50+45+140=300분)보다 대구 경유가 100분 빠르다.");
    }
}
