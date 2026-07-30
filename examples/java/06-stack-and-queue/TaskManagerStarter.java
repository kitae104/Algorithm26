import java.util.ArrayDeque;
import java.util.Deque;

public class TaskManagerStarter {

    /** 문서 내용 (type한 글자들이 이어 붙는다) */
    static StringBuilder document = new StringBuilder();

    /** 실행 취소용 스택: 가장 최근에 입력한 문자열이 맨 위에 온다 (LIFO) */
    static Deque<String> undoStack = new ArrayDeque<>();

    /** 고객 대기열 큐: 먼저 등록한 고객이 먼저 나간다 (FIFO) */
    static Deque<String> waitQueue = new ArrayDeque<>();

    /** 명령 1: text를 문서 끝에 붙이고, 취소할 수 있도록 스택에 기록한다. */
    static void type(String text) {
        // TODO 1: document 끝에 text를 붙이세요. (힌트: document.append(text))
        // TODO 2: undoStack에 text를 push하세요.
        System.out.println("[type] \"" + text + "\" 입력 -> 문서 = \"" + document + "\"");
    }

    /** 명령 2: 가장 최근에 입력한 내용을 취소한다. */
    static void undo() {
        // TODO 3: undoStack이 비어 있으면 "[undo] 취소할 입력이 없습니다"를 출력하고 끝내세요.
        // TODO 4: 비어 있지 않으면 pop한 문자열의 길이만큼 document 끝에서 지우고,
        //         "[undo] \"내용\" 입력 취소 -> 문서 = \"...\"" 형식으로 출력하세요.
        //         (힌트: document.delete(document.length() - last.length(), document.length()))
        System.out.println("[undo] 아직 구현되지 않았습니다");
    }

    /** 명령 3: 고객을 대기열 뒤에 등록한다. */
    static void enqueue(String name) {
        // TODO 5: waitQueue에 name을 offer하고,
        //         "[enqueue] 이름 님 등록 -> 대기 n명" 형식으로 출력하세요.
        System.out.println("[enqueue] 아직 구현되지 않았습니다");
    }

    /** 명령 4: 대기열 맨 앞 고객의 응대를 시작한다. */
    static void serve() {
        // TODO 6: waitQueue가 비어 있으면 "[serve] 대기 중인 고객이 없습니다"를 출력하세요.
        // TODO 7: 비어 있지 않으면 poll한 고객으로
        //         "[serve] 이름 님 응대 시작 -> 남은 대기 n명" 형식으로 출력하세요.
        System.out.println("[serve] 아직 구현되지 않았습니다");
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
