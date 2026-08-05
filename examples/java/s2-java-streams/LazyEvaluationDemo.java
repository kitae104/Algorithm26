import java.util.stream.Stream;

/**
 * 스트림의 두 가지 성질을 실행 결과로 직접 확인한다.
 *   1) 최종 연산이 없으면 아무 일도 일어나지 않는다.
 *   2) 원소는 "한 명씩 파이프라인 끝까지" 지나간다. filter를 전부 돌린 뒤
 *      map으로 넘어가는 것이 아니다.
 */
public class LazyEvaluationDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. 최종 연산이 없을 때 ===");
        Stream.of(1, 2, 3, 4, 5)
              .filter(n -> { System.out.println("  filter " + n); return n % 2 == 0; })
              .map(n -> { System.out.println("  map " + n); return n * 10; });
        System.out.println("  (아무것도 출력되지 않았습니다 — 계획만 쌓였을 뿐입니다)");
        System.out.println();

        System.out.println("=== 2. 최종 연산을 붙였을 때 (toList) ===");
        var all = Stream.of(1, 2, 3, 4, 5)
              .filter(n -> { System.out.println("  filter " + n); return n % 2 == 0; })
              .map(n -> { System.out.println("  map " + n); return n * 10; })
              .toList();
        System.out.println("  결과: " + all);
        System.out.println("  → filter 1, filter 2, map 2, filter 3, ... 순서에 주목하세요.");
        System.out.println("    원소 하나가 끝까지 갔다가 다음 원소가 출발합니다.");
        System.out.println();

        System.out.println("=== 3. findFirst — 찾는 즉시 멈춘다 ===");
        int first = Stream.of(1, 2, 3, 4, 5)
              .filter(n -> { System.out.println("  filter " + n); return n % 2 == 0; })
              .map(n -> { System.out.println("  map " + n); return n * 10; })
              .findFirst().orElse(-1);
        System.out.println("  결과: " + first);
        System.out.println("  → 3, 4, 5는 아예 검사되지 않았습니다.");
        System.out.println("    5강에서 순차 탐색에 return을 넣어 조기 중단했던 것과 같은 절약입니다.");
        System.out.println();

        System.out.println("=== 4. 스트림은 한 번만 쓸 수 있다 ===");
        Stream<Integer> s = Stream.of(1, 2, 3);
        System.out.println("  첫 번째 count(): " + s.count());
        try {
            System.out.println("  두 번째 count(): " + s.count());
        } catch (IllegalStateException e) {
            System.out.println("  두 번째 count(): 예외 발생 — " + e.getMessage());
            System.out.println("  → 스트림은 컬렉션이 아니라 한 번 흘려보내는 통로입니다.");
        }
    }
}
