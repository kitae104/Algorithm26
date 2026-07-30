public class Step2StackPush {

    static char[] stack = new char[8];
    static int top = -1;

    /** push: top을 한 칸 올리고, 그 자리에 값을 넣는다 */
    static void push(char value) {
        top++;
        stack[top] = value;
        System.out.println("push '" + value + "' -> top = " + top
                + ", 개수 = " + (top + 1));
    }

    public static void main(String[] args) {
        System.out.println("시작: top = " + top + " (비어 있음)");

        push('(');
        push('(');
        push('[');

        // 바닥(0번 칸)부터 꼭대기(top번 칸)까지 순서대로 확인한다
        StringBuilder state = new StringBuilder();
        for (int i = 0; i <= top; i++) {
            if (i > 0) state.append(' ');
            state.append(stack[i]);
        }
        System.out.println("스택 상태(바닥 -> 꼭대기): " + state);
        System.out.println("맨 위(top)의 값 = " + stack[top]);
    }
}
