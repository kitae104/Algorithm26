import java.util.ArrayDeque;
import java.util.Deque;

public class Step6QueueDequeue {

    static Deque<String> queue = new ArrayDeque<>();

    static void enqueue(String name) {
        queue.offer(name);
    }

    public static void main(String[] args) {
        enqueue("김하늘");
        enqueue("이준호");
        enqueue("박서연");
        System.out.println("enqueue 3번 후 대기 인원 = " + queue.size());

        // peek: 맨 앞 값을 "보기만" 한다. 큐는 그대로 유지된다.
        System.out.println("peek() = " + queue.peek() + " (대기 인원은 그대로 " + queue.size() + ")");
        // poll: 맨 앞 값을 꺼내고 제거한다.
        System.out.println("poll() = " + queue.poll() + " (poll 후 대기 인원 = " + queue.size() + ")");
        System.out.println("poll() = " + queue.poll() + " (poll 후 대기 인원 = " + queue.size() + ")");
        System.out.println("poll() = " + queue.poll() + " (poll 후 대기 인원 = " + queue.size() + ")");
        System.out.println("비어 있는가? = " + queue.isEmpty());

        // 비어 있는데 또 poll하면? 예외 없이 null을 돌려준다
        System.out.println("poll() = " + queue.poll() + " (빈 큐에서는 null)");
    }
}
