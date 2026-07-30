public class Step3StackPopPeek {

    static char[] stack = new char[8];
    static int top = -1;

    /** 비었는지 검사: pop과 peek 전에 반드시 필요하다 */
    static boolean isEmpty() {
        return top == -1;
    }

    static void push(char value) {
        top++;
        stack[top] = value;
    }

    /** peek: 맨 위 값을 "보기만" 한다. top은 그대로 유지된다. */
    static char peek() {
        if (isEmpty()) {
            System.out.println("경고: 빈 스택은 peek할 수 없습니다.");
            return '?';
        }
        return stack[top];
    }

    /** pop: 맨 위 값을 꺼내고 top을 한 칸 내린다. */
    static char pop() {
        if (isEmpty()) {
            System.out.println("경고: 빈 스택은 pop할 수 없습니다.");
            return '?';
        }
        char value = stack[top];
        top--;
        return value;
    }

    public static void main(String[] args) {
        push('(');
        push('[');
        System.out.println("push 2번 후 개수 = " + (top + 1));

        System.out.println("peek() = " + peek() + " (top은 그대로 " + top + ")");
        System.out.println("pop()  = " + pop() + " (pop 후 top = " + top + ")");
        System.out.println("pop()  = " + pop() + " (pop 후 top = " + top + ")");
        System.out.println("비어 있는가? = " + isEmpty());

        // 비어 있는데 또 pop하면? isEmpty 검사 덕분에 프로그램이 죽지 않는다
        System.out.println("pop()  = " + pop());
    }
}
