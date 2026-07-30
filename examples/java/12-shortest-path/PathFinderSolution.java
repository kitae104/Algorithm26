import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 최종 프로그램 "동네 길찾기 PathFinder" — 정답 코드.
 * 배열 버전 다익스트라 + prev 경로 복원 + 도달 불가 처리.
 */
public class PathFinderSolution {

    static final int INF = Integer.MAX_VALUE;

    /** 간선 정보: 도착 장소 번호와 걷는 시간(분) */
    static class Edge {
        int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /** 다익스트라 결과: 최단 시간 배열 + 경로 복원용 직전 장소 배열 */
    static class Result {
        int[] dist;
        int[] prev;

        Result(int[] dist, int[] prev) {
            this.dist = dist;
            this.prev = prev;
        }
    }

    static void addPath(List<List<Edge>> map, int u, int v, int minutes) {
        map.get(u).add(new Edge(v, minutes));
        map.get(v).add(new Edge(u, minutes));
    }

    /** 미확정 장소 중 거리가 가장 작은 장소 번호를 반환한다. 없으면 -1 */
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

    /** 배열 버전 다익스트라: start에서 모든 장소까지의 최단 시간과 직전 장소를 계산한다 */
    static Result dijkstra(List<List<Edge>> map, int start) {
        int n = map.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        for (int round = 0; round < n; round++) {
            int u = pickClosest(dist, visited);
            if (u == -1) {
                break;                        // 남은 장소는 모두 도달 불가
            }
            visited[u] = true;                // u까지의 최단 시간 확정

            for (Edge e : map.get(u)) {
                if (dist[u] + e.weight < dist[e.to]) {   // 완화 조건
                    dist[e.to] = dist[u] + e.weight;
                    prev[e.to] = u;                      // 경로 기록
                }
            }
        }
        return new Result(dist, prev);
    }

    /** prev 배열을 거슬러 올라가 "집 -> 카페 -> ..." 형태의 경로 문자열을 만든다 */
    static String buildPath(int[] prev, String[] names, int end) {
        StringBuilder path = new StringBuilder(names[end]);
        int v = end;
        while (prev[v] != -1) {
            v = prev[v];
            path.insert(0, names[v] + " -> ");
        }
        return path.toString();
    }

    public static void main(String[] args) {
        String[] names = {"집", "카페", "학교", "도서관", "공원", "병원", "체육관"};
        int n = names.length;

        List<List<Edge>> map = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            map.add(new ArrayList<>());
        }
        addPath(map, 0, 1, 4);   // 집-카페
        addPath(map, 0, 2, 10);  // 집-학교
        addPath(map, 1, 2, 3);   // 카페-학교
        addPath(map, 1, 3, 8);   // 카페-도서관
        addPath(map, 2, 3, 2);   // 학교-도서관
        addPath(map, 2, 4, 7);   // 학교-공원
        addPath(map, 3, 5, 5);   // 도서관-병원
        addPath(map, 4, 5, 1);   // 공원-병원
        // 체육관(6)은 진입로 공사 중이라 연결된 길이 아직 없다

        int start = 0;   // 집
        Result result = dijkstra(map, start);

        int[] targets = {5, 6, 0};   // 병원, 체육관, 집
        for (int end : targets) {
            if (result.dist[end] == INF) {
                System.out.println(names[start] + " -> " + names[end] + " : 도달 불가");
            } else {
                System.out.println(names[start] + " -> " + names[end] + " : "
                        + result.dist[end] + "분, 경로 "
                        + buildPath(result.prev, names, end));
            }
        }
    }
}
