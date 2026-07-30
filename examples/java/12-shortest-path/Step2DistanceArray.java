import java.util.Arrays;

public class Step2DistanceArray {

    /** "아직 도달하는 방법을 모른다"를 나타내는 값 */
    static final int INF = Integer.MAX_VALUE;

    /** INF는 ∞ 기호로 바꿔서 보여 준다 */
    static String show(int d) {
        return d == INF ? "∞" : String.valueOf(d);
    }

    public static void main(String[] args) {
        String[] names = {"A", "B", "C", "D", "E", "F"};
        int start = 0;   // 출발 정점 A

        // 거리 배열: 출발 정점만 0, 나머지는 전부 ∞
        int[] dist = new int[names.length];
        Arrays.fill(dist, INF);
        dist[start] = 0;

        System.out.println("출발 정점: " + names[start]);
        for (int v = 0; v < names.length; v++) {
            System.out.println("dist[" + names[v] + "] = " + show(dist[v]));
        }

        System.out.println();
        System.out.println("[주의] Integer.MAX_VALUE에 가중치를 더하면?");
        System.out.println("Integer.MAX_VALUE     = " + Integer.MAX_VALUE);
        System.out.println("Integer.MAX_VALUE + 3 = " + (Integer.MAX_VALUE + 3) + "  <- 오버플로로 음수!");
        System.out.println("그래서 dist[u] + w를 계산하기 전에 dist[u]가 INF인지 반드시 확인해야 한다.");
    }
}
