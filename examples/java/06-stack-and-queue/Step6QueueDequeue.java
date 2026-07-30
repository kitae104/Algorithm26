public class Step6QueueDequeue {

    static String[] queue = new String[8];
    static int front = 0;
    static int rear = 0;

    /** 비었는지 검사: front와 rear가 같으면 빈 큐다 */
    static boolean isEmpty() {
        return front == rear;
    }

    static void enqueue(String name) {
        queue[rear] = name;
        rear++;
    }

    /** peek: 맨 앞 값을 "보기만" 한다. front는 그대로 유지된다. */
    static String peek() {
        if (isEmpty()) {
            System.out.println("경고: 빈 큐는 peek할 수 없습니다.");
            return "?";
        }
        return queue[front];
    }

    /** dequeue: 맨 앞 값을 꺼내고 front를 한 칸 뒤로 옮긴다. */
    static String dequeue() {
        if (isEmpty()) {
            System.out.println("경고: 빈 큐는 dequeue할 수 없습니다.");
            return "?";
        }
        String value = queue[front];
        front++;
        return value;
    }

    public static void main(String[] args) {
        enqueue("김하늘");
        enqueue("이준호");
        enqueue("박서연");
        System.out.println("enqueue 3번 후 대기 인원 = " + (rear - front));

        System.out.println("peek()    = " + peek() + " (front는 그대로 " + front + ")");
        System.out.println("dequeue() = " + dequeue() + " (dequeue 후 front = " + front + ")");
        System.out.println("dequeue() = " + dequeue() + " (dequeue 후 front = " + front + ")");
        System.out.println("dequeue() = " + dequeue() + " (dequeue 후 front = " + front + ")");
        System.out.println("비어 있는가? = " + isEmpty());

        // 비어 있는데 또 dequeue하면? isEmpty 검사 덕분에 프로그램이 죽지 않는다
        System.out.println("dequeue() = " + dequeue());
    }
}
