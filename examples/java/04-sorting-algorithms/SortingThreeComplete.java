import java.util.Arrays;

public class SortingThreeComplete {

    /** 한 번의 정렬 결과와 연산 횟수를 담는 기록 클래스 (1강 Measurement 패턴) */
    static class SortResult {
        int[] sorted;      // 정렬된 배열
        long compares;     // 비교 횟수
        long swapsOrMoves; // 교환 횟수(선택·버블) 또는 이동 횟수(삽입)

        SortResult(int[] sorted, long compares, long swapsOrMoves) {
            this.sorted = sorted;
            this.compares = compares;
            this.swapsOrMoves = swapsOrMoves;
        }
    }

    /** 선택 정렬: 남은 구간의 최솟값을 찾아 앞으로 보낸다. */
    static SortResult selectionSort(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length); // 원본 보존
        long compares = 0;
        long swaps = 0;

        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                compares++;
                if (arr[j] < arr[minIndex]) {
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
        return new SortResult(arr, compares, swaps);
    }

    /** 버블 정렬: 이웃끼리 비교·교환하며 큰 값을 뒤로 밀어낸다. 교환이 없으면 조기 종료. */
    static SortResult bubbleSort(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        long compares = 0;
        long swaps = 0;

        for (int i = 0; i < arr.length - 1; i++) {
            boolean swapped = false;
            // 뒤쪽 i개는 이미 확정되었으므로 범위에서 제외한다
            for (int j = 0; j < arr.length - 1 - i; j++) {
                compares++;
                if (arr[j] > arr[j + 1]) {
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
        return new SortResult(arr, compares, swaps);
    }

    /** 삽입 정렬: 왼쪽의 정렬된 영역에 새 값을 알맞은 자리에 끼워 넣는다. */
    static SortResult insertionSort(int[] input) {
        int[] arr = Arrays.copyOf(input, input.length);
        long compares = 0;
        long moves = 0;

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];   // 이번에 끼워 넣을 값
            int j = i - 1;
            while (j >= 0) {
                compares++;
                if (arr[j] > key) {
                    arr[j + 1] = arr[j]; // 한 칸 뒤로 민다 (이동)
                    moves++;
                    j--;
                } else {
                    break; // 더 작은 값을 만나면 그 자리가 key의 자리
                }
            }
            arr[j + 1] = key;
        }
        return new SortResult(arr, compares, moves);
    }

    static void printTable(String title, int[] data) {
        System.out.println("== " + title + ": " + Arrays.toString(data) + " ==");
        System.out.println("알고리즘  | 비교 횟수 | 교환·이동 | 정렬 결과");

        SortResult sel = selectionSort(data);
        SortResult bub = bubbleSort(data);
        SortResult ins = insertionSort(data);

        System.out.printf("선택 정렬 | %-8d | %-8d | %s%n",
                sel.compares, sel.swapsOrMoves, Arrays.toString(sel.sorted));
        System.out.printf("버블 정렬 | %-8d | %-8d | %s%n",
                bub.compares, bub.swapsOrMoves, Arrays.toString(bub.sorted));
        System.out.printf("삽입 정렬 | %-8d | %-8d | %s%n",
                ins.compares, ins.swapsOrMoves, Arrays.toString(ins.sorted));
        System.out.println();
    }

    public static void main(String[] args) {
        int[] random = {26, 15, 38, 12, 21, 30, 8, 19};   // 무작위 데이터
        int[] sorted = {8, 12, 15, 19, 21, 26, 30, 38};   // 이미 정렬된 데이터 (최선)
        int[] reversed = {38, 30, 26, 21, 19, 15, 12, 8}; // 역순 데이터 (최악)

        printTable("무작위 데이터", random);
        printTable("이미 정렬된 데이터", sorted);
        printTable("역순 데이터", reversed);

        System.out.println("관찰: 비교 횟수의 '모양'은 세 정렬 모두 O(n^2)이지만,");
        System.out.println("      이미 정렬된 입력에서 버블(조기 종료)과 삽입은 n-1번 비교로 끝난다.");
    }
}
