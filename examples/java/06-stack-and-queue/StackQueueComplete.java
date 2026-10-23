import java.util.ArrayDeque;
import java.util.Deque;

public class StackQueueComplete {

    /** 여는 괄호 open과 닫는 괄호 close가 서로 짝인지 확인한다 */
    static boolean isPair(char open, char close) {
        return (open == '(' && close == ')')
                || (open == '[' && close == ']')
                || (open == '{' && close == '}');
    }

    /** ArrayDeque를 스택으로 사용해 괄호 검사 */
    static boolean isValid(String text) {
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);                       // 스택처럼 사용: 맨 위에 쌓는다
            } else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty() || !isPair(stack.pop(), ch)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println("== 1) ArrayDeque를 스택으로: 괄호 유효성 검사 ==");
        String[] tests = {"()", "{[()]}", "(a+b)*[c]", "(]", "())", "((("};
        for (String t : tests) {
            System.out.println(t + " -> " + (isValid(t) ? "유효" : "무효"));
        }

        System.out.println();
        System.out.println("== 2) ArrayDeque를 큐로: 주문 대기열 처리 (FIFO) ==");
        Deque<String> orders = new ArrayDeque<>();
        orders.offer("김하늘");                         // 큐처럼 사용: 뒤에 넣는다
        orders.offer("이준호");
        orders.offer("박서연");
        System.out.println("대기열: " + orders + " (대기 " + orders.size() + "명)");
        System.out.println("맨 앞(peek) = " + orders.peek());
        while (!orders.isEmpty()) {
            System.out.println("처리: " + orders.poll() + " -> 남은 대기열 " + orders);
        }
        System.out.println("빈 큐에서 poll() = " + orders.poll() + " (null 반환, 예외 없음)");
    }
}
