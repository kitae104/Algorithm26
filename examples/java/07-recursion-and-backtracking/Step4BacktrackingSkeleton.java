import java.util.ArrayList;
import java.util.List;

public class Step4BacktrackingSkeleton {

    static int[] numbers = {1, 2, 3};
    static List<Integer> chosen = new ArrayList<>();   // 지금까지의 선택
    static int subsetCount = 0;

    /** index번째 원소를 "넣을지 / 뺄지" 결정한다. 선택 -> 진행 -> 취소 구조. */
    static void subsets(int index) {
        if (index == numbers.length) {          // 종료 조건: 모든 원소를 결정했다
            subsetCount++;
            System.out.println("부분집합 " + subsetCount + ": " + chosen);
            return;
        }

        chosen.add(numbers[index]);             // (1) 선택: 이 원소를 넣는다
        subsets(index + 1);                     // (2) 진행: 다음 원소를 결정하러 간다
        chosen.remove(chosen.size() - 1);       // (3) 취소: 상태를 원래대로 복원한다!

        subsets(index + 1);                     // 이 원소를 넣지 않는 경우도 진행한다
    }

    public static void main(String[] args) {
        System.out.println("{1, 2, 3}의 모든 부분집합:");
        subsets(0);
        System.out.println("총 " + subsetCount + "개 (2 x 2 x 2 = 8)");
    }
}
