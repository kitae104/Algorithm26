public class Step5QueueEnqueue {

    static String[] queue = new String[8];   // 대기 이름을 담을 공간 (용량 8)
    static int front = 0;                    // 다음에 꺼낼 자리 (맨 앞)
    static int rear = 0;                     // 다음에 넣을 자리 (맨 뒤)

    /** enqueue: rear 자리에 값을 넣고, rear를 한 칸 뒤로 옮긴다 */
    static void enqueue(String name) {
        queue[rear] = name;
        rear++;
        System.out.println("enqueue \"" + name + "\" -> front = " + front
                + ", rear = " + rear + ", 대기 " + (rear - front) + "명");
    }

    public static void main(String[] args) {
        System.out.println("시작: front = " + front + ", rear = " + rear + " (비어 있음)");

        enqueue("김하늘");
        enqueue("이준호");
        enqueue("박서연");

        // 맨 앞(front)부터 맨 뒤(rear 직전)까지 순서대로 확인한다
        StringBuilder state = new StringBuilder();
        for (int i = front; i < rear; i++) {
            if (i > front) state.append(' ');
            state.append(queue[i]);
        }
        System.out.println("대기열 상태(앞 -> 뒤): " + state);
        System.out.println("맨 앞(front)의 값 = " + queue[front]);
    }
}
