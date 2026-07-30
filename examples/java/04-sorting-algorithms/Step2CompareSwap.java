public class Step2CompareSwap {

    /** 배열의 i번 칸과 j번 칸의 값을 맞바꾼다. 임시 변수가 반드시 필요하다. */
    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];   // 1) 한쪽 값을 임시 변수에 보관하고
        arr[i] = arr[j];     // 2) i번 칸에 j번 값을 덮어쓴 뒤
        arr[j] = temp;       // 3) 보관해 둔 값을 j번 칸에 넣는다
    }

    public static void main(String[] args) {
        int[] prices = {26, 15, 38, 12, 21};

        System.out.println("교환 전: prices[0] = " + prices[0]
                + ", prices[1] = " + prices[1]);

        // 비교: 앞의 값이 뒤의 값보다 크면 오름차순에 어긋난다
        if (prices[0] > prices[1]) {
            System.out.println("prices[0] > prices[1] 이므로 두 값을 교환합니다.");
            swap(prices, 0, 1);
        }

        System.out.println("교환 후: prices[0] = " + prices[0]
                + ", prices[1] = " + prices[1]);
    }
}
