/**
 * 함수형 인터페이스를 직접 만들어 "기준을 값으로 넘기는" 구조를 확인한다.
 *
 * 2강의 countAtLeast(data, threshold)는 "~점 이상"이라는 기준이 코드에 박혀 있어
 * 새 기준이 필요할 때마다 메서드를 하나씩 더 만들어야 했다.
 * 여기서는 세는 방법(순회)은 하나로 두고, 세는 기준만 호출자가 정한다.
 */
public class RuleDemo {

    /** 추상 메서드가 하나뿐 — 그래서 람다식을 대입할 수 있다. */
    @FunctionalInterface
    interface ScoreRule {
        boolean test(int score);
    }

    /** 순회 코드는 여기 한 번만 있다. 기준은 매개변수로 들어온다. O(n) */
    static int countBy(int[] scores, ScoreRule rule) {
        int count = 0;
        for (int s : scores) {
            if (rule.test(s)) {
                count++;
            }
        }
        return count;
    }

    /** 이름을 붙이면 호출부가 훨씬 잘 읽힌다. */
    static final ScoreRule PASSED = s -> s >= 60;
    static final ScoreRule EXCELLENT = s -> s >= 90;

    public static void main(String[] args) {
        int[] scores = {88, 72, 95, 64, 79, 91, 58, 100};

        System.out.println("점수: " + java.util.Arrays.toString(scores));
        System.out.println();

        // 익명 클래스로 넘기던 방식 — 람다 이전에는 이렇게 써야 했다
        int oldWay = countBy(scores, new ScoreRule() {
            @Override
            public boolean test(int score) {
                return score >= 80;
            }
        });
        System.out.println("80점 이상 (익명 클래스): " + oldWay);

        // 같은 일을 람다식으로
        System.out.println("80점 이상 (람다식)    : " + countBy(scores, s -> s >= 80));
        System.out.println();

        // countBy는 한 줄도 고치지 않았다. 바뀐 것은 넘긴 규칙뿐이다.
        System.out.println("합격(60점 이상) : " + countBy(scores, PASSED));
        System.out.println("우수(90점 이상) : " + countBy(scores, EXCELLENT));
        System.out.println("짝수 점수       : " + countBy(scores, s -> s % 2 == 0));
        System.out.println("70~89점         : " + countBy(scores, s -> s >= 70 && s < 90));

        System.out.println();
        System.out.println("순회 코드는 countBy 하나뿐입니다. 기준이 늘어도 늘지 않습니다.");
    }
}
