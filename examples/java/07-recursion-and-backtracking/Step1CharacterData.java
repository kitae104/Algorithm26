public class Step1CharacterData {
    public static void main(String[] args) {
        // 재귀 연습용 입력 1: 하노이 탑 — 원반 개수와 기둥 이름
        int n = 3;
        char from = 'A', via = 'B', to = 'C';

        // 백트래킹 연습용 입력 2: 순서를 정할 후보 문자들 (순열의 재료)
        char[] letters = {'a', 'b', 'c'};

        System.out.println("원반 " + n + "개를 기둥 " + from + "에서 " + to + "로 옮긴다 (보조 기둥 " + via + ")");
        System.out.println("후보 문자 개수 = " + letters.length);

        // 후보 문자를 처음부터 끝까지 출력한다
        for (int i = 0; i < letters.length; i++) {
            System.out.println("letters[" + i + "] = " + letters[i]);
        }
    }
}
