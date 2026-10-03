import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 4강 「람다·스트림 변경」.
 *
 * 정렬 알고리즘을 직접 구현하지 않고, 람다와 스트림만으로 정렬하는 방법을 모았다.
 * 오름차순은 sorted() 한 줄이고, 역순(내림차순)은 비교 기준을 뒤집어 만든다.
 */
public class ModernizeSolution {

    public static void main(String[] args) {
        int[] prices = {26, 15, 38, 12, 21, 30, 8, 19};
        System.out.println("원본 배열      : " + Arrays.toString(prices));

        // 1) 오름차순: sorted()는 기준을 주지 않으면 작은 값부터 늘어놓는다
        int[] asc = Arrays.stream(prices)
                .sorted()
                .toArray();
        System.out.println("오름차순       : " + Arrays.toString(asc));

        // 2) 역순(내림차순): 람다에서 a와 b의 자리를 바꿔 비교한다
        //    int 스트림은 비교 기준을 받지 못하므로 boxed()로 Integer 스트림으로 바꾼 뒤 정렬한다
        int[] desc = Arrays.stream(prices)
                .boxed()
                .sorted((a, b) -> Integer.compare(b, a))
                .mapToInt(x -> x)
                .toArray();
        System.out.println("역순 (람다)    : " + Arrays.toString(desc));

        // 3) 역순(내림차순): 오름차순 기준을 만들어 두고 reversed()로 뒤집는다
        Comparator<Integer> ascOrder = (a, b) -> Integer.compare(a, b);
        int[] descByReversed = Arrays.stream(prices)
                .boxed()
                .sorted(ascOrder.reversed())
                .mapToInt(x -> x)
                .toArray();
        System.out.println("역순 (reversed): " + Arrays.toString(descByReversed));

        // 4) 큰 값 3개만: 역순으로 정렬한 뒤 앞에서 3개만 남긴다
        int[] top3 = Arrays.stream(prices)
                .boxed()
                .sorted(ascOrder.reversed())
                .limit(3)
                .mapToInt(x -> x)
                .toArray();
        System.out.println("큰 값 3개      : " + Arrays.toString(top3));

        // 스트림 정렬은 새 배열을 만들 뿐, 원본 배열은 바꾸지 않는다
        System.out.println("정렬 후 원본   : " + Arrays.toString(prices));
        System.out.println();

        List<String> names = List.of("보조 배터리", "이어폰", "스마트폰 거치대", "충전기", "블루투스 스피커");
        System.out.println("원본 목록      : " + names);

        // 5) 문자열 오름차순(사전순)
        List<String> byName = names.stream()
                .sorted()
                .toList();
        System.out.println("이름순         : " + byName);

        // 6) 문자열 역순: compareTo의 방향을 뒤집는다
        List<String> byNameDesc = names.stream()
                .sorted((a, b) -> b.compareTo(a))
                .toList();
        System.out.println("이름 역순      : " + byNameDesc);

        // 7) 기준 바꾸기: 글자 수가 짧은 것부터
        List<String> byLength = names.stream()
                .sorted(Comparator.comparingInt(String::length))
                .toList();
        System.out.println("짧은 이름부터  : " + byLength);

        // 8) 기준 바꾸기 + 역순: 글자 수가 긴 것부터
        List<String> byLengthDesc = names.stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList();
        System.out.println("긴 이름부터    : " + byLengthDesc);
    }
}
