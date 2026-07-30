public class Step2CharCompare {
    public static void main(String[] args) {
        String word = "banana";

        // charAt(i): 문자열의 i번째 문자를 하나 꺼낸다 (인덱스는 0부터 시작)
        char first = word.charAt(0);
        char last = word.charAt(word.length() - 1);

        System.out.println("단어: " + word);
        System.out.println("첫 문자 charAt(0) = " + first);
        System.out.println("마지막 문자 charAt(" + (word.length() - 1) + ") = " + last);

        // 문자(char)끼리는 == 로 비교한다
        System.out.println("첫 문자와 마지막 문자가 같은가? " + (first == last));

        // 문자열(String)끼리는 반드시 equals로 비교한다
        String a = "apple";
        String b = "app" + "le";        // 내용이 같은 문자열
        String c = new String("apple"); // 내용은 같지만 새로 만든 객체

        System.out.println("a.equals(b) = " + a.equals(b));
        System.out.println("a.equals(c) = " + a.equals(c));
        System.out.println("(주의) a == c 는 " + (a == c) + " (내용이 같아도 다른 객체라서 false!)");
    }
}
