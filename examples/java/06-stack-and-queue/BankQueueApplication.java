import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class BankQueueApplication {

    /** 은행 고객: 이름, 접수 번호, VIP 여부 */
    static class Customer {
        String name;
        int ticketNo;
        boolean vip;

        Customer(String name, int ticketNo, boolean vip) {
            this.name = name;
            this.ticketNo = ticketNo;
            this.vip = vip;
        }

        @Override
        public String toString() {
            return name + "(" + ticketNo + "번" + (vip ? ", VIP" : "") + ")";
        }
    }

    public static void main(String[] args) {
        Customer[] arrivals = {
                new Customer("김하늘", 1, false),
                new Customer("이준호", 2, false),
                new Customer("박서연", 3, true),
                new Customer("최민재", 4, false),
                new Customer("정다은", 5, true)
        };

        System.out.println("== 방식 A: 일반 큐 — 도착 순서 = 처리 순서 (FIFO) ==");
        Queue<Customer> queue = new ArrayDeque<>();
        for (Customer c : arrivals) {
            queue.offer(c);
            System.out.println("접수: " + c + " -> 대기 " + queue.size() + "명");
        }
        int orderA = 1;
        while (!queue.isEmpty()) {
            System.out.println(orderA + "번째 창구 호출: " + queue.poll());
            orderA++;
        }

        System.out.println();
        System.out.println("== 방식 B: 우선순위 큐 맛보기 — VIP 먼저, 같은 등급은 접수 번호 순 ==");
        PriorityQueue<Customer> priorityQueue = new PriorityQueue<>(
                Comparator.comparing((Customer c) -> !c.vip)   // VIP(false가 앞)부터
                          .thenComparing(c -> c.ticketNo));    // 같은 등급이면 번호 순
        for (Customer c : arrivals) {
            priorityQueue.offer(c);
        }
        int orderB = 1;
        while (!priorityQueue.isEmpty()) {
            System.out.println(orderB + "번째 창구 호출: " + priorityQueue.poll());
            orderB++;
        }

        System.out.println();
        System.out.println("일반 큐는 '들어온 순서', 우선순위 큐는 '중요한 순서'로 꺼낸다. (10강에서 자세히)");
    }
}
