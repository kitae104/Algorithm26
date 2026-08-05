import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 메서드 참조(::)로 줄일 수 있는 경우와 없는 경우를 나란히 놓는다.
 *
 * 판단 기준 하나:
 *   "람다의 매개변수가 메서드 호출에 그대로, 순서대로, 전부 들어가는가?"
 * 하나라도 아니면 람다로 두는 편이 읽기 쉽다.
 */
public class MethodReferenceDemo {

    static String shout(String s) {
        return s.toUpperCase() + "!";
    }

    public static void main(String[] args) {
        List<String> words = Arrays.asList("delta", "alpha", "charlie", "bravo");

        System.out.println("[1) 정적 메서드 — Integer::parseInt]");
        List<String> numberTexts = Arrays.asList("10", "3", "27", "8");
        Function<String, Integer> parseLambda = s -> Integer.parseInt(s);
        Function<String, Integer> parseRef = Integer::parseInt;
        System.out.println("  람다     : " + parseLambda.apply("42"));
        System.out.println("  메서드 참조: " + parseRef.apply("42"));
        int total = 0;
        for (String t : numberTexts) {
            total += parseRef.apply(t);
        }
        System.out.println("  합계: " + total);
        System.out.println();

        System.out.println("[2) 특정 객체의 메서드 — System.out::println]");
        words.forEach(s -> System.out.println("  " + s));      // 앞에 공백을 붙이므로 줄일 수 없다
        words.forEach(System.out::println);                    // 받은 값을 그대로 넘기므로 줄일 수 있다
        System.out.println();

        System.out.println("[3) 임의 객체의 메서드 — String::length]");
        Comparator<String> byLenLambda = (a, b) -> Integer.compare(a.length(), b.length());
        Comparator<String> byLenKey = Comparator.comparingInt(s -> s.length());
        Comparator<String> byLenRef = Comparator.comparingInt(String::length);

        List<String> c1 = new ArrayList<>(words);
        List<String> c2 = new ArrayList<>(words);
        List<String> c3 = new ArrayList<>(words);
        c1.sort(byLenLambda);
        c2.sort(byLenKey);
        c3.sort(byLenRef);
        System.out.println("  세 방식의 결과가 같은가: " + (c1.equals(c2) && c2.equals(c3)));
        System.out.println("  " + c3);
        System.out.println();

        System.out.println("[4) 생성자 — ArrayList::new]");
        Supplier<List<String>> makeLambda = () -> new ArrayList<>();
        Supplier<List<String>> makeRef = ArrayList::new;
        List<String> fresh = makeRef.get();
        fresh.add("새 리스트");
        System.out.println("  " + fresh + " (makeLambda도 동일: " + makeLambda.get() + ")");
        System.out.println();

        System.out.println("[5) 직접 만든 정적 메서드도 참조할 수 있다]");
        Function<String, String> shoutRef = MethodReferenceDemo::shout;
        System.out.println("  " + shoutRef.apply("hello"));
        System.out.println();

        System.out.println("[줄일 수 없는 경우]");
        System.out.println("  s -> System.out.println(\"[\" + s + \"]\")   : 받은 값에 무언가를 더 한다");
        System.out.println("  (a, b) -> Integer.compare(b, a)          : 인자의 순서가 뒤바뀐다");
        System.out.println("  s -> s.length() > 3                      : 호출 결과를 다시 비교한다");
    }
}
