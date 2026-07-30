import java.util.ArrayDeque;
import java.util.Deque;

public class TaskManagerSolution {

    /** 문서 내용 (type한 글자들이 이어 붙는다) */
    static StringBuilder document = new StringBuilder();

    /** 실행 취소용 스택: 가장 최근에 입력한 문자열이 맨 위에 온다 (LIFO) */
    static Deque<String> undoStack = new ArrayDeque<>();

    /** 고객 대기열 큐: 먼저 등록한 고객이 먼저 나간다 (FIFO) */
    static Deque<String> waitQueue = new ArrayDeque<>();

    /** 명령 1: text를 문서 끝에 붙이고, 취소할 수 있도록 스택에 기록한다. */
    static void type(String text) {
        document.append(text);
        undoStack.push(text);           // 가장 최근 입력이 맨 위로
        System.out.println("[type] \"" + text + "\" 입력 -> 문서 = \"" + document + "\"");
    }

    /** 명령 2: 가장 최근에 입력한 내용을 취소한다. */
    static void undo() {
        if (undoStack.isEmpty()) {      // 빈 스택 검사가 반드시 먼저!
            System.out.println("[undo] 취소할 입력이 없습니다");
            return;
        }
        String last = undoStack.pop();  // 가장 최근 입력부터 취소 (LIFO)
        document.delete(document.length() - last.length(), document.length());
        System.out.println("[undo] \"" + last + "\" 입력 취소 -> 문서 = \"" + document + "\"");
    }

    /** 명령 3: 고객을 대기열 뒤에 등록한다. */
    static void enqueue(String name) {
        waitQueue.offer(name);          // 뒤(rear)에 넣는다
        System.out.println("[enqueue] " + name + " 님 등록 -> 대기 " + waitQueue.size() + "명");
    }

    /** 명령 4: 대기열 맨 앞 고객의 응대를 시작한다. */
    static void serve() {
        if (waitQueue.isEmpty()) {      // 빈 큐 검사가 반드시 먼저!
            System.out.println("[serve] 대기 중인 고객이 없습니다");
            return;
        }
        String name = waitQueue.poll(); // 앞(front)에서 꺼낸다 (FIFO)
        System.out.println("[serve] " + name + " 님 응대 시작 -> 남은 대기 " + waitQueue.size() + "명");
    }

    public static void main(String[] args) {
        String[] commands = {
                "type:안녕",
                "type:하세요",
                "enqueue:김하늘",
                "type:!!",
                "undo",
                "enqueue:이준호",
                "serve",
                "serve",
                "serve",
                "undo",
                "undo",
                "undo"
        };

        for (String command : commands) {
            if (command.startsWith("type:")) {
                type(command.substring(5));
            } else if (command.equals("undo")) {
                undo();
            } else if (command.startsWith("enqueue:")) {
                enqueue(command.substring(8));
            } else if (command.equals("serve")) {
                serve();
            }
        }

        System.out.println();
        System.out.println("== 최종 상태 ==");
        System.out.println("문서 내용 = \"" + document + "\"");
        System.out.println("남은 대기 고객 = " + waitQueue.size() + "명 " + waitQueue);
        System.out.println("취소 가능한 입력 = " + undoStack.size() + "개");
    }
}
