import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class Step2StackPush {

    static Deque<Character> stack = new ArrayDeque<>();

    /** push: 맨 위에 값을 올린다 */
    static void push(char value) {
        stack.push(value);
        System.out.println("push '" + value + "' -> 개수 = " + stack.size());
    }

    public static void main(String[] args) {
        System.out.println("시작: 개수 = " + stack.size() + " (비어 있음)");

        push('(');
        push('(');
        push('[');

        // ArrayDeque의 push는 앞쪽에 쌓이므로, 바닥부터 보려면 거꾸로 순회한다
        StringBuilder state = new StringBuilder();
        Iterator<Character> it = stack.descendingIterator();
        while (it.hasNext()) {
            state.append(it.next());
            if (it.hasNext()) state.append(' ');
        }
        System.out.println("스택 상태(바닥 -> 꼭대기): " + state);
        System.out.println("맨 위의 값 = " + stack.peek());
    }
}
