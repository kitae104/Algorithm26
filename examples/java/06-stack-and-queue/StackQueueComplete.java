import java.util.ArrayDeque;
import java.util.Deque;

public class StackQueueComplete {

    /** 배열로 직접 구현한 문자 스택 — "자료구조 = 데이터(배열) + 규칙(LIFO)" */
    static class ArrayCharStack {
        private char[] data;
        private int top = -1;

        ArrayCharStack(int capacity) {
            data = new char[capacity];
        }

        boolean isEmpty() {
            return top == -1;
        }

        int size() {
            return top + 1;
        }

        void push(char value) {
            if (top == data.length - 1) {
                throw new IllegalStateException("스택이 가득 찼습니다.");
            }
            data[++top] = value;
        }

        char pop() {
            if (isEmpty()) {
                throw new IllegalStateException("빈 스택에서는 pop할 수 없습니다.");
            }
            return data[top--];
        }

        char peek() {
            if (isEmpty()) {
                throw new IllegalStateException("빈 스택에서는 peek할 수 없습니다.");
            }
            return data[top];
        }
    }

    /** 배열로 직접 구현한 문자열 큐 — "자료구조 = 데이터(배열) + 규칙(FIFO)" */
    static class ArrayStringQueue {
        private String[] data;
        private int front = 0;   // 다음에 꺼낼 자리
        private int rear = 0;    // 다음에 넣을 자리

        ArrayStringQueue(int capacity) {
            data = new String[capacity];
        }

        boolean isEmpty() {
            return front == rear;
        }

        int size() {
            return rear - front;
        }

        void enqueue(String value) {
            if (rear == data.length) {
                throw new IllegalStateException("큐가 가득 찼습니다.");
            }
            data[rear++] = value;
        }

        String dequeue() {
            if (isEmpty()) {
                throw new IllegalStateException("빈 큐에서는 dequeue할 수 없습니다.");
            }
            return data[front++];
        }

        String peek() {
            if (isEmpty()) {
                throw new IllegalStateException("빈 큐에서는 peek할 수 없습니다.");
            }
            return data[front];
        }

        /** 앞(front)부터 뒤(rear 직전)까지의 대기 상태 문자열 */
        String state() {
            if (isEmpty()) {
                return "(비어 있음)";
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = front; i < rear; i++) {
                if (i > front) sb.append(", ");
                sb.append(data[i]);
            }
            return sb.append("]").toString();
        }
    }

    /** 여는 괄호 open과 닫는 괄호 close가 서로 짝인지 확인한다 */
    static boolean isPair(char open, char close) {
        return (open == '(' && close == ')')
                || (open == '[' && close == ']')
                || (open == '{' && close == '}');
    }

    /** 방법 A: 직접 만든 배열 스택으로 괄호 검사 */
    static boolean isValidWithArrayStack(String text) {
        ArrayCharStack stack = new ArrayCharStack(text.length());
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            } else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty() || !isPair(stack.pop(), ch)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /** 방법 B: 표준 라이브러리 ArrayDeque로 괄호 검사 — 실무 권장 방식 */
    static boolean isValidWithDeque(String text) {
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);                       // 스택처럼 사용: 맨 앞에 쌓는다
            } else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty() || !isPair(stack.pop(), ch)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println("== 1) 괄호 유효성 검사: 직접 구현 스택 vs ArrayDeque ==");
        String[] tests = {"()", "{[()]}", "(a+b)*[c]", "(]", "())", "((("};
        for (String t : tests) {
            boolean a = isValidWithArrayStack(t);
            boolean b = isValidWithDeque(t);
            System.out.println(t + " -> " + (a ? "유효" : "무효")
                    + " (두 방식 일치 = " + (a == b) + ")");
        }

        System.out.println();
        System.out.println("== 2) 직접 구현한 배열 큐로 주문 대기열 처리 (FIFO) ==");
        ArrayStringQueue orders = new ArrayStringQueue(8);
        orders.enqueue("김하늘");                       // 뒤(rear)에 넣는다
        orders.enqueue("이준호");
        orders.enqueue("박서연");
        System.out.println("대기열: " + orders.state() + " (대기 " + orders.size() + "명)");
        System.out.println("맨 앞(peek) = " + orders.peek());
        while (!orders.isEmpty()) {
            System.out.println("처리: " + orders.dequeue() + " -> 남은 대기열 " + orders.state());
        }

        System.out.println();
        System.out.println("== 3) 같은 대기열을 표준 라이브러리 ArrayDeque로 (실무 권장) ==");
        Deque<String> queue = new ArrayDeque<>();
        queue.offer("김하늘");                          // 큐처럼 사용: 뒤에 넣는다
        queue.offer("이준호");
        queue.offer("박서연");
        System.out.println("대기열: " + queue);
        while (!queue.isEmpty()) {
            System.out.println("처리: " + queue.poll() + " -> 남은 대기열 " + queue);
        }
        System.out.println("빈 큐에서 poll() = " + queue.poll() + " (null 반환, 예외 없음)");
    }
}
