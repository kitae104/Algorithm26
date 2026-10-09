import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 5강 「람다·스트림 변경」.
 *
 * 도서 번호를 배열 대신 List에 담고, 람다·스트림(순차 탐색)과
 * Collections.binarySearch(이진 탐색)로 같은 번호를 찾아 본다.
 */
public class ModernizeSolution {

    public static void main(String[] args) {
        List<Integer> bookNumbers = List.of(1001, 1203, 1450, 2088, 2311, 2754,
                                            3106, 3502, 3860, 4213, 4771, 5090);
        List<Integer> targets = List.of(1001, 3106, 5090, 2500);   // 2500은 없는 번호

        System.out.println("도서 " + bookNumbers.size() + "권에서 번호 찾기");
        System.out.println();

        // 1) 람다 + 스트림 순차 탐색: 조건에 맞는 첫 인덱스를 findFirst로 찾는다.
        System.out.println("[1] 스트림 순차 탐색");
        targets.forEach(target -> {
            int index = IntStream.range(0, bookNumbers.size())
                    .filter(i -> bookNumbers.get(i).equals(target))
                    .findFirst()                 // 찾는 즉시 멈춘다 (조기 중단)
                    .orElse(-1);
            System.out.println(target + " -> 인덱스 " + index);
        });

        // 2) 스트림 필터: 목록에 실제로 있는 번호만 골라낸다.
        List<Integer> found = targets.stream()
                .filter(bookNumbers::contains)
                .toList();
        System.out.println("목록에 있는 번호: " + found);
        System.out.println();

        // 3) 기본 API 이진 탐색: Collections.binarySearch (정렬된 List 필요)
        //    찾으면 인덱스, 못 찾으면 -(삽입 위치) - 1 을 돌려준다.
        System.out.println("[2] Collections.binarySearch");
        targets.forEach(target -> {
            int result = Collections.binarySearch(bookNumbers, target);
            if (result >= 0) {
                System.out.println(target + " -> 인덱스 " + result);
            } else {
                int insertionPoint = -(result + 1);
                System.out.println(target + " -> 없음 (반환값 " + result
                        + ", 넣을 위치 " + insertionPoint + ")");
            }
        });
    }
}
