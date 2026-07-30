import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class BracketCheckTrace {

    static boolean isPair(char open, char close) {
        return (open == '(' && close == ')')
                || (open == '[' && close == ']')
                || (open == '{' && close == '}');
    }

    /** 스택 내용을 바닥 -> 꼭대기 순서의 문자열로 만든다 */
    static String stackState(Deque<Character> stack) {
        if (stack.isEmpty()) {
            return "(비어 있음)";
        }
        // ArrayDeque의 push는 앞쪽에 쌓이므로, 바닥부터 보려면 거꾸로 순회한다
        StringBuilder sb = new StringBuilder();
        Iterator<Character> it = stack.descendingIterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(' ');
        }
        return sb.toString();
    }

    /** 한 문자씩 처리하며 (문자, 동작, 스택 상태)를 추적 출력한다 */
    static void trace(String text) {
        System.out.println("입력: \"" + text + "\"");
        Deque<Character> stack = new ArrayDeque<>();
        boolean failed = false;

        for (int i = 0; i < text.length() && !failed; i++) {
            char ch = text.charAt(i);
            String action;

            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
                action = "push '" + ch + "'";
            } else if (stack.isEmpty()) {
                action = "실패! 짝이 없는 닫는 괄호";
                failed = true;
            } else {
                char open = stack.pop();
                if (isPair(open, ch)) {
                    action = "pop '" + open + "' (짝 맞음)";
                } else {
                    action = "실패! '" + open + "'와 짝이 아님";
                    failed = true;
                }
            }
            System.out.println("  " + i + "번째 문자 '" + ch + "' : " + action
                    + " -> 스택: " + stackState(stack));
        }

        if (failed) {
            System.out.println("  검사 중단 -> 무효한 괄호");
        } else if (stack.isEmpty()) {
            System.out.println("  검사 종료: 스택이 비어 있음 -> 유효한 괄호");
        } else {
            System.out.println("  검사 종료: 스택에 " + stackState(stack)
                    + " 남음 -> 무효 (여는 괄호가 남음)");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        trace("([()])");   // 성공 사례
        trace("(()");      // 실패 사례 1: 여는 괄호가 남는다
        trace("())");      // 실패 사례 2: 닫는 괄호의 짝이 없다
    }
}
