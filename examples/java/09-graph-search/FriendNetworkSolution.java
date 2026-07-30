import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class FriendNetworkSolution {

    /** 요구 1: 친구 쌍 목록으로 인접 리스트를 만든다 */
    static List<List<Integer>> buildAdjacency(int userCount, int[][] friendships) {
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int v = 0; v < userCount; v++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] pair : friendships) {
            adjacency.get(pair[0]).add(pair[1]);   // 친구 관계는 서로 대칭이므로
            adjacency.get(pair[1]).add(pair[0]);   // 양쪽 리스트에 모두 추가한다
        }
        return adjacency;
    }

    /** 요구 2: v에서 DFS로 도달 가능한 사용자를 visitOrder에 방문 순서대로 담는다 */
    static void dfs(List<List<Integer>> adjacency, int v,
                    boolean[] visited, List<Integer> visitOrder) {
        visited[v] = true;       // 방문 표시가 없으면 친구 관계의 순환에서 무한 재귀!
        visitOrder.add(v);
        for (int neighbor : adjacency.get(v)) {
            if (!visited[neighbor]) {
                dfs(adjacency, neighbor, visited, visitOrder);
            }
        }
    }

    /** 요구 3: BFS로 start에서 각 사용자까지의 최소 다리 수를 구한다 (연결 없으면 -1) */
    static int[] bfsDistances(List<List<Integer>> adjacency, int start) {
        int[] distance = new int[adjacency.size()];
        Arrays.fill(distance, -1);
        Deque<Integer> queue = new ArrayDeque<>();

        distance[start] = 0;     // 거리 기록 = 방문 표시 (큐에 넣을 때 기록한다)
        queue.offer(start);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int neighbor : adjacency.get(v)) {
                if (distance[neighbor] == -1) {
                    distance[neighbor] = distance[v] + 1;
                    queue.offer(neighbor);
                }
            }
        }
        return distance;
    }

    /** 요구 4: 전체 친구 그룹(연결 요소) 수를 센다 */
    static int countGroups(List<List<Integer>> adjacency) {
        boolean[] visited = new boolean[adjacency.size()];
        int groupCount = 0;
        for (int v = 0; v < adjacency.size(); v++) {
            if (!visited[v]) {
                groupCount++;    // 새 그룹 발견
                dfs(adjacency, v, visited, new ArrayList<>());
            }
        }
        return groupCount;
    }

    public static void main(String[] args) {
        // SNS 친구 네트워크: 사용자 8명과 친구 관계(쌍)
        String[] users = {"민준", "서연", "지호", "하은", "도윤", "예린", "시우", "지아"};
        int[][] friendships = { {0, 1}, {0, 2}, {1, 3}, {2, 3}, {4, 5}, {5, 6} };

        List<List<Integer>> adjacency = buildAdjacency(users.length, friendships);

        System.out.println("[1] 인접 리스트");
        for (int v = 0; v < users.length; v++) {
            System.out.println("  " + v + " " + users[v] + ": " + adjacency.get(v));
        }

        System.out.println();
        System.out.println("[2] " + users[0] + "에서 DFS로 도달 가능한 친구망");
        boolean[] visited = new boolean[users.length];
        List<Integer> reachable = new ArrayList<>();
        dfs(adjacency, 0, visited, reachable);
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < reachable.size(); i++) {
            if (i > 0) {
                names.append(" → ");
            }
            names.append(users[reachable.get(i)]);
        }
        System.out.println("  방문 순서: " + names + " (" + reachable.size() + "명)");

        System.out.println();
        System.out.println("[3] " + users[0] + " 기준 \"몇 다리 건너 아는 사이\" (BFS 최소 거리)");
        int[] distance = bfsDistances(adjacency, 0);
        for (int v = 1; v < users.length; v++) {
            String text = (distance[v] == -1) ? "연결 없음" : distance[v] + "다리";
            System.out.println("  " + users[v] + ": " + text);
        }

        System.out.println();
        System.out.println("[4] 전체 친구 그룹 수: " + countGroups(adjacency) + "개");
    }
}
