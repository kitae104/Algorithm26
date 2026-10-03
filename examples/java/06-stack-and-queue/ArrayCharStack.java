/** 배열로 직접 구현한 문자 스택 — "자료구조 = 데이터(배열) + 규칙(LIFO)" */
public class ArrayCharStack {
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
