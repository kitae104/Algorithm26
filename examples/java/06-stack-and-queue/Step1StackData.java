public class Step1StackData {
    public static void main(String[] args) {
        // 스택 = "데이터(배열)" + "규칙(맨 위에서만 넣고 꺼낸다, LIFO)"
        // 먼저 데이터를 담을 배열과, 맨 위 칸의 위치를 기억할 변수를 준비한다
        char[] stack = new char[8];   // 괄호 문자를 담을 공간 (용량 8)
        int top = -1;                 // 맨 위 칸의 인덱스. -1이면 "비어 있음"이라는 약속

        System.out.println("스택 용량 = " + stack.length);
        System.out.println("top = " + top);
        System.out.println("비어 있는가? = " + (top == -1));
        System.out.println("쌓여 있는 데이터 개수 = " + (top + 1));
    }
}
