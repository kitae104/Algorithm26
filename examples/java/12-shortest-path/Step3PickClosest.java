public class Step3PickClosest {

    /** "아직 도달하는 방법을 모른다"를 나타내는 값 */
    static final int INF = Integer.MAX_VALUE;

    /**
     * 아직 확정되지 않은 정점 중에서 거리가 가장 작은 정점 번호를 찾는다.
     * 전부 확정됐거나 남은 정점이 모두 INF(도달 불가)이면 -1을 반환한다.
     * 2강에서 배운 "최솟값 찾기"에 visited 조건 하나만 얹은 것이다.
     */
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

    /** INF는 ∞ 기호로 바꿔서 보여 준다 */
    static String show(int d) {
        return d == INF ? "∞" : String.valueOf(d);
    }

    public static void main(String[] args) {
        String[] names = {"A", "B", "C", "D", "E", "F"};

        // 출발 정점 A를 확정하고, A의 간선(B까지 3, C까지 7)으로 갱신을 마친 직후의 상태
        int[] dist = {0, 3, 7, INF, INF, INF};
        boolean[] visited = {true, false, false, false, false, false};

        System.out.println("현재 거리 배열:");
        for (int v = 0; v < names.length; v++) {
            System.out.println("  dist[" + names[v] + "] = " + show(dist[v])
                    + (visited[v] ? "  (확정)" : ""));
        }

        int next = pickClosest(dist, visited);
        System.out.println();
        System.out.println("미확정 정점 중 거리가 가장 작은 정점: " + names[next]
                + " (거리 " + dist[next] + ")");
        System.out.println("모든 가중치가 0 이상이므로, 어떤 길로 돌아가도 "
                + names[next] + "까지 " + dist[next] + "보다 짧아질 수 없다 -> 확정!");

        // B를 확정 표시하고 한 번 더 뽑으면? (확정된 정점은 건너뛴다)
        visited[next] = true;
        int afterNext = pickClosest(dist, visited);
        System.out.println();
        System.out.println(names[next] + " 확정 후 다시 뽑으면: " + names[afterNext]
                + " (거리 " + dist[afterNext] + ")");
    }
}
