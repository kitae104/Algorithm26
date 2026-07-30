public class Step1CharacterData {
    public static void main(String[] args) {
        // 재귀 연습용 입력 1: 팩토리얼을 구할 수 n
        int n = 4;

        // 백트래킹 연습용 입력 2: 문자열을 만들 후보 문자들
        char[] letters = {'a', 'b', 'c'};

        System.out.println("팩토리얼 대상 n = " + n);
        System.out.println("후보 문자 개수 = " + letters.length);

        // 후보 문자를 처음부터 끝까지 출력한다
        for (int i = 0; i < letters.length; i++) {
            System.out.println("letters[" + i + "] = " + letters[i]);
        }
    }
}
