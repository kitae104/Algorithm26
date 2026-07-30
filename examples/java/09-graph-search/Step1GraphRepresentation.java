import java.util.ArrayList;
import java.util.List;

public class Step1GraphRepresentation {
    public static void main(String[] args) {
        // 친구 관계 그래프: 정점 7개(0~6번), 간선은 "쌍의 목록"으로 주어진다
        int vertexCount = 7;
        int[][] edges = { {0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 5} };

        // 표현 1: 인접 리스트 — 정점마다 "이웃 목록"을 저장한다
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int v = 0; v < vertexCount; v++) {
            adjacency.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adjacency.get(edge[0]).add(edge[1]);   // 무방향 그래프는
            adjacency.get(edge[1]).add(edge[0]);   // 양쪽 리스트에 모두 추가한다
        }

        System.out.println("[표현 1] 인접 리스트 (정점 " + vertexCount + "개, 간선 " + edges.length + "개)");
        for (int v = 0; v < vertexCount; v++) {
            System.out.println("  " + v + "번 정점의 이웃: " + adjacency.get(v));
        }

        // 표현 2: 인접 행렬 — matrix[i][j] = 1 이면 i와 j가 연결되어 있다
        int[][] matrix = new int[vertexCount][vertexCount];
        for (int[] edge : edges) {
            matrix[edge[0]][edge[1]] = 1;
            matrix[edge[1]][edge[0]] = 1;
        }

        System.out.println();
        System.out.println("[표현 2] 인접 행렬 (1 = 연결, 0 = 연결 없음)");
        System.out.print("     ");
        for (int j = 0; j < vertexCount; j++) {
            System.out.print(j + " ");
        }
        System.out.println();
        for (int i = 0; i < vertexCount; i++) {
            System.out.print("  " + i + ": ");
            for (int j = 0; j < vertexCount; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
