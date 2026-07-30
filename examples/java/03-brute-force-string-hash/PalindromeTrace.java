public class PalindromeTrace {

    /** 회문 판별: 양 끝(left, right)에서 가운데로 좁혀 가며 문자 쌍을 비교한다. */
    static boolean isPalindrome(String word) {
        int left = 0;
        int right = word.length() - 1;

        System.out.println("[추적] \"" + word + "\" 회문 판별");
        System.out.println("left | right | charAt(left) | charAt(right) | 일치");
        System.out.println("-----+-------+--------------+---------------+-----");

        while (left < right) {
            char leftChar = word.charAt(left);
            char rightChar = word.charAt(right);
            boolean same = (leftChar == rightChar);   // 문자(char) 비교는 ==

            System.out.printf("%4d | %5d | %12c | %13c | %s%n",
                    left, right, leftChar, rightChar, same ? "O" : "X");

            if (!same) {
                return false;   // 한 쌍이라도 다르면 그 즉시 회문이 아니다
            }
            left++;             // 왼쪽 포인터는 오른쪽으로
            right--;            // 오른쪽 포인터는 왼쪽으로
        }
        return true;            // 포인터가 만나거나 엇갈리면 회문이다
    }

    public static void main(String[] args) {
        String[] tests = {"level", "banana", "noon", "기러기"};

        for (String word : tests) {
            boolean result = isPalindrome(word);
            System.out.println("=> \"" + word + "\" 은(는) 회문인가? " + result);
            System.out.println();
        }
    }
}
