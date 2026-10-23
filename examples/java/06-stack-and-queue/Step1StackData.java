import java.util.ArrayDeque;
import java.util.Deque;

public class Step1StackData {
    public static void main(String[] args) {
        // 스택 = "데이터 보관함" + "규칙(맨 위에서만 넣고 꺼낸다, LIFO)"
        // 자바 표준 라이브러리 ArrayDeque를 스택처럼 사용한다
        Deque<Character> stack = new ArrayDeque<>();   // 괄호 문자를 담을 스택

        System.out.println("스택 내용 = " + stack);
        System.out.println("비어 있는가? = " + stack.isEmpty());
        System.out.println("쌓여 있는 데이터 개수 = " + stack.size());
    }
}
