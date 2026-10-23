import java.util.ArrayDeque;
import java.util.Deque;

public class Step3StackPopPeek {

    static Deque<Character> stack = new ArrayDeque<>();

    public static void main(String[] args) {
        stack.push('(');
        stack.push('[');
        System.out.println("push 2번 후 개수 = " + stack.size());

        // peek: 맨 위 값을 "보기만" 한다. 스택은 그대로 유지된다.
        System.out.println("peek() = " + stack.peek() + " (개수는 그대로 " + stack.size() + ")");
        // pop: 맨 위 값을 꺼내고 제거한다.
        System.out.println("pop()  = " + stack.pop() + " (pop 후 개수 = " + stack.size() + ")");
        System.out.println("pop()  = " + stack.pop() + " (pop 후 개수 = " + stack.size() + ")");
        System.out.println("비어 있는가? = " + stack.isEmpty());

        // 비어 있는데 또 pop하면 NoSuchElementException! 꺼내기 전에 isEmpty로 검사한다
        if (stack.isEmpty()) {
            System.out.println("경고: 빈 스택은 pop할 수 없습니다.");
        } else {
            System.out.println("pop()  = " + stack.pop());
        }
    }
}
