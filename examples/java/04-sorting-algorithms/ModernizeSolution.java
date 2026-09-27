import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * 4강 「람다·스트림 변경」.
 *
 * SortingThreeComplete.java(실습 코드)를 같은 결과가 나오도록 람다와 스트림으로 다시 쓴 것이다.
 * 배열의 칸을 직접 바꾸는 정렬 알고리즘의 핵심(교환·이동)은 반복문으로 둔다.
 */
public class ModernizeSolution {

    /** 오름차순 기준: 세 정렬이 모두 이 람다로 두 값의 순서를 판단한다. */
    static final Comparator<Integer> ASC = (a, b) -> Integer.compare(a, b);

    /** 한 번의 정렬 결과와 연산 횟수를 담는 기록 클래스 (정렬 이름 포함) */
    static class SortResult {
        String name;       // 정렬 이름
        int[] sorted;      // 정렬된 배열
        long compares;     // 비교 횟수
        long swapsOrMoves; // 교환 횟수(선택·버블) 또는 이동 횟수(삽입)

        SortResult(String name, int[] sorted, long compares, long swapsOrMoves) {
            this.name = name;
            this.sorted = sorted;
            this.compares = compares;
            this.swapsOrMoves = swapsOrMoves;
        }
    }

    /** 선택 정렬: 남은 구간의 최솟값을 찾아 앞으로 보낸다. */
    static SortResult selectionSort(int[] input, Comparator<Integer> order) {
        int[] arr = Arrays.stream(input).toArray(); // 원본 보존 (스트림으로 복사)
        long compares = 0;
        long swaps = 0;

        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                compares++;
                if (order.compare(arr[j], arr[minIndex]) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
                swaps++;
            }
        }
        return new SortResult("선택 정렬", arr, compares, swaps);
    }

    /** 버블 정렬: 이웃끼리 비교·교환하며 큰 값을 뒤로 밀어낸다. 교환이 없으면 조기 종료. */
    static SortResult bubbleSort(int[] input, Comparator<Integer> order) {
        int[] arr = Arrays.stream(input).toArray();
        long compares = 0;
        long swaps = 0;

        for (int i = 0; i < arr.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                compares++;
                if (order.compare(arr[j], arr[j + 1]) > 0) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swaps++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // 한 바퀴 동안 교환이 없었다 = 이미 정렬 완료
            }
        }
        return new SortResult("버블 정렬", arr, compares, swaps);
    }

    /** 삽입 정렬: 왼쪽의 정렬된 영역에 새 값을 알맞은 자리에 끼워 넣는다. */
    static SortResult insertionSort(int[] input, Comparator<Integer> order) {
        int[] arr = Arrays.stream(input).toArray();
        long compares = 0;
        long moves = 0;

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0) {
                compares++;
                if (order.compare(arr[j], key) > 0) {
                    arr[j + 1] = arr[j]; // 한 칸 뒤로 민다 (이동)
                    moves++;
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
        }
        return new SortResult("삽입 정렬", arr, compares, moves);
    }

    static void printTable(String title, int[] data) {
        System.out.println("== " + title + ": " + Arrays.toString(data) + " ==");
        System.out.println("알고리즘  | 비교 횟수 | 교환·이동 | 정렬 결과");

        // 세 정렬의 결과를 한 줄로 늘어놓고, 같은 형식으로 한 줄씩 출력한다
        Stream.of(selectionSort(data, ASC), bubbleSort(data, ASC), insertionSort(data, ASC))
                .forEach(r -> System.out.printf("%s | %-8d | %-8d | %s%n",
                        r.name, r.compares, r.swapsOrMoves, Arrays.toString(r.sorted)));
        System.out.println();
    }

    public static void main(String[] args) {
        int[] random = {26, 15, 38, 12, 21, 30, 8, 19};              // 무작위 데이터
        int[] sorted = Arrays.stream(random).sorted().toArray();     // 이미 정렬된 데이터 (최선)
        int[] reversed = Arrays.stream(random).boxed()               // 역순 데이터 (최악)
                .sorted(ASC.reversed())
                .mapToInt(x -> x)
                .toArray();

        printTable("무작위 데이터", random);
        printTable("이미 정렬된 데이터", sorted);
        printTable("역순 데이터", reversed);

        System.out.println("관찰: 비교 횟수의 '모양'은 세 정렬 모두 O(n^2)이지만,");
        System.out.println("      이미 정렬된 입력에서 버블(조기 종료)과 삽입은 n-1번 비교로 끝난다.");
    }
}
