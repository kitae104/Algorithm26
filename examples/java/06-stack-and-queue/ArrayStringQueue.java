/** 배열로 직접 구현한 문자열 큐 — "자료구조 = 데이터(배열) + 규칙(FIFO)" */
public class ArrayStringQueue {
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
