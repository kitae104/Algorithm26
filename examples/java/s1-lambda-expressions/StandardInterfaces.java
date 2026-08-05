import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * java.util.function의 표준 함수형 인터페이스 다섯 가지를 한 화면에서 비교한다.
 * 고르는 기준은 하나 — "입력이 몇 개이고 출력이 있는가".
 */
public class StandardInterfaces {

    public static void main(String[] args) {
        /* 1개 → boolean : 판단 */
        Predicate<Integer> pass = s -> s >= 60;

        /* 1개 → 값 : 변환 */
        Function<Integer, String> toGrade = s -> {
            if (s >= 90) return "A";
            if (s >= 80) return "B";
            if (s >= 70) return "C";
            return "F";
        };

        /* 1개 → 없음 : 소비 */
        Consumer<String> print = msg -> System.out.println("  > " + msg);

        /* 없음 → 값 : 공급 */
        Supplier<String> placeholder = () -> "(성적 없음)";

        /* 2개(같은 타입) → 같은 타입 : 누적 */
        BinaryOperator<Integer> higher = (a, b) -> a >= b ? a : b;

        int[] scores = {88, 72, 95, 54, 79};

        System.out.println("[개별 호출]");
        System.out.println("  pass.test(72)        = " + pass.test(72));
        System.out.println("  toGrade.apply(85)    = " + toGrade.apply(85));
        print.accept("Consumer는 반환값이 없습니다");
        System.out.println("  placeholder.get()    = " + placeholder.get());
        System.out.println("  higher.apply(88, 95) = " + higher.apply(88, 95));

        System.out.println();
        System.out.println("[조합해서 쓰기]");
        int best = scores[0];
        for (int s : scores) {
            String line = s + "점 → " + toGrade.apply(s) + (pass.test(s) ? " (합격)" : " (불합격)");
            print.accept(line);
            best = higher.apply(best, s);
        }
        System.out.println("  최고 점수: " + best);

        System.out.println();
        System.out.println("[Predicate 조합 — and / or / negate]");
        Predicate<Integer> excellent = s -> s >= 90;
        Predicate<Integer> passedButNotExcellent = pass.and(excellent.negate());
        for (int s : scores) {
            if (passedButNotExcellent.test(s)) {
                System.out.println("  " + s + "점: 합격이지만 우수는 아님");
            }
        }

        System.out.println();
        System.out.println("[기본형 전용 인터페이스]");
        /* Predicate<Integer>는 int를 Integer로 포장했다가 다시 꺼낸다(박싱).
           원소가 아주 많을 때는 IntPredicate가 그 비용을 없앤다. */
        IntPredicate passInt = s -> s >= 60;
        int count = 0;
        for (int s : scores) {
            if (passInt.test(s)) count++;   // 박싱 없음
        }
        System.out.println("  IntPredicate로 센 합격자 수: " + count);
    }
}
