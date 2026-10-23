import java.util.ArrayDeque;
import java.util.Deque;

public class Step4BracketCheckLoop {

    /** 여는 괄호 open과 닫는 괄호 close가 서로 짝인지 확인한다 */
    static boolean isPair(char open, char close) {
        return (open == '(' && close == ')')
                || (open == '[' && close == ']')
                || (open == '{' && close == '}');
    }

    /** 괄호 문자열이 유효한지 검사한다 */
    static boolean isValid(String text) {
        Deque<Character> stack = new ArrayDeque<>();   // 검사할 때마다 새 스택으로 시작

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);                       // 여는 괄호: 스택에 쌓는다
            } else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty()) {
                    return false;                     // 짝이 없는 닫는 괄호
                }
                if (!isPair(stack.pop(), ch)) {
                    return false;                     // 종류가 다른 괄호끼리 만남
                }
            }
        }
        // 루프가 끝난 뒤 스택이 비어 있어야 진짜 유효 (여는 괄호가 남으면 무효)
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        String[] tests = {"()", "([])", "{[()]}", "(]", "())", "((("};
        for (String t : tests) {
            System.out.println(t + " -> " + (isValid(t) ? "유효" : "무효"));
        }
    }
}
