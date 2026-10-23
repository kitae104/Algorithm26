import java.util.ArrayDeque;
import java.util.Deque;

public class Step5QueueEnqueue {

    // 큐 = "데이터 보관함" + "규칙(뒤로 넣고 앞에서 꺼낸다, FIFO)"
    static Deque<String> queue = new ArrayDeque<>();   // 대기 이름을 담을 큐

    /** enqueue: offer로 맨 뒤에 세운다 */
    static void enqueue(String name) {
        queue.offer(name);
        System.out.println("enqueue \"" + name + "\" -> 대기 " + queue.size() + "명");
    }

    public static void main(String[] args) {
        System.out.println("시작: 대기 " + queue.size() + "명 (비어 있음)");

        enqueue("김하늘");
        enqueue("이준호");
        enqueue("박서연");

        System.out.println("대기열 상태(앞 -> 뒤): " + queue);
        System.out.println("맨 앞의 값 = " + queue.peek());
    }
}
