import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 스트림을 쓸 때 실제로 자주 나는 문제들을 실행 결과로 확인한다.
 * 마지막 항목은 "스트림을 쓰지 말아야 할 곳"이다 — 이쪽이 더 중요하다.
 */
public class StreamPitfalls {

    public static void main(String[] args) {
        List<Integer> scores = List.of(88, 72, 95, 64, 79, 91, 58);

        System.out.println("=== ① 최종 연산을 빼먹으면 조용히 아무 일도 하지 않는다 ===");
        scores.stream().filter(s -> s >= 60).map(s -> s + 5);
        System.out.println("  위 줄은 오류도 나지 않고 결과도 없습니다.");
        System.out.println("  스트림을 썼는데 변화가 없다면 최종 연산부터 확인하세요.");
        System.out.println();

        System.out.println("=== ② toList()가 돌려주는 리스트는 수정할 수 없다 ===");
        List<Integer> fixed = scores.stream().filter(s -> s >= 80).toList();
        System.out.println("  결과: " + fixed);
        try {
            fixed.add(100);
        } catch (UnsupportedOperationException e) {
            System.out.println("  add 시도 → UnsupportedOperationException");
            System.out.println("  나중에 수정해야 한다면 collect(Collectors.toCollection(ArrayList::new))를 쓰세요.");
        }
        System.out.println();

        System.out.println("=== ③ forEach로 바깥 리스트를 채우지 않는다 ===");
        List<Integer> badWay = new ArrayList<>();
        scores.stream().filter(s -> s >= 80).forEach(badWay::add);   // 되긴 하지만
        List<Integer> goodWay = scores.stream().filter(s -> s >= 80).toList();
        System.out.println("  forEach로 채운 결과 : " + badWay);
        System.out.println("  collect로 모은 결과 : " + goodWay);
        System.out.println("  결과는 같지만, forEach 방식은 병렬로 바꾸는 순간 깨집니다.");
        System.out.println("  '무엇을 하는지'가 코드에 드러나는 이점도 잃습니다.");
        System.out.println();

        System.out.println("=== ④ Stream<Integer>에는 sum()이 없다 ===");
        int sumWrongWay = scores.stream().reduce(0, (a, b) -> a + b);      // 되지만 박싱이 붙는다
        int sumRightWay = scores.stream().mapToInt(Integer::intValue).sum();
        System.out.println("  reduce로 더한 합계   : " + sumWrongWay);
        System.out.println("  mapToInt 후 sum()    : " + sumRightWay);
        System.out.println("  기본형 배열이라면 처음부터 Arrays.stream(int[])이 IntStream을 줍니다.");
        System.out.println();

        System.out.println("=== ⑤ 인덱스가 필요한 알고리즘은 반복문이 낫다 ===");
        int[] arr = {5, 2, 9, 1, 7};
        int[] byLoop = arr.clone();
        insertionSort(byLoop);
        System.out.println("  삽입 정렬(반복문): " + Arrays.toString(byLoop));

        /* 굳이 스트림으로 흉내 내면 이렇게 된다 — 짧지도 명확하지도 않다.
           게다가 이건 정렬 알고리즘을 구현한 것이 아니라 이미 있는 sorted()를 부른 것뿐이다. */
        int[] byStream = IntStream.of(arr).sorted().toArray();
        System.out.println("  sorted()로 정렬  : " + Arrays.toString(byStream));
        System.out.println("  두 결과가 같은가 : " + Arrays.equals(byLoop, byStream));
        System.out.println();
        System.out.println("  주의: sorted()는 '정렬 알고리즘을 구현한 것'이 아니라 '이미 있는 정렬을 부른 것'입니다.");
        System.out.println("  4강에서 배우는 것은 그 안에서 무슨 일이 일어나는가입니다.");
        System.out.println("  arr[j + 1] = arr[j]처럼 인덱스를 직접 옮기는 코드는 스트림으로 옮길 수 없습니다.");
        System.out.println();
        System.out.println("  → 스트림이 잘 맞는 곳: 집계 · 변환 · 필터링");
        System.out.println("  → 반복문이 잘 맞는 곳: 인덱스 조작 · 조기 break · 여러 변수 동시 갱신");
    }

    /** 4강의 삽입 정렬 — 인덱스를 직접 다루므로 스트림으로 바꿀 수 없다. */
    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }
}
