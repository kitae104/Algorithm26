public class DescendingSortPractice {

    /** 버블 정렬을 내림차순으로: 이웃한 두 값 중 "작은 값"을 뒤로 보낸다. */
    static void bubbleSortDescending(int[] arr) {
        // TODO 1: 바깥 반복문 — i를 0부터 arr.length - 2까지 옮기세요.
        // TODO 2: 안쪽 반복문 — j를 0부터 arr.length - 2 - i까지 옮기세요.
        // TODO 3: arr[j] < arr[j + 1] 이면(작은 값이 앞에 있으면)
        //         임시 변수를 사용해 두 값을 교환하세요.
    }

    static String toText(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        int[] prices = {26, 15, 38, 12, 21};

        System.out.println("정렬 전        : " + toText(prices));
        bubbleSortDescending(prices);
        System.out.println("내림차순 정렬 후: " + toText(prices));
        System.out.println("기대 결과      : [38, 26, 21, 15, 12]");
    }
}
