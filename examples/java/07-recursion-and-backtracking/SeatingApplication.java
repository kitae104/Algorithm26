public class SeatingApplication {

    static String[] students = {"민준", "서연", "도윤"};
    static String[] seats = new String[students.length];   // seats[i] = i번 좌석의 학생
    static boolean[] used = new boolean[students.length];  // 학생 배치(방문) 여부
    static int totalCount = 0;    // 제약 없는 전체 배치 수
    static int validCount = 0;    // 규칙을 통과한 배치 수
    static int pruneCount = 0;    // 가지치기로 잘라낸 횟수

    /** 규칙: 민준과 서연은 옆자리에 앉을 수 없다. */
    static boolean isForbiddenPair(String a, String b) {
        return (a.equals("민준") && b.equals("서연"))
                || (a.equals("서연") && b.equals("민준"));
    }

    /** [1] 제약 없이 모든 배치를 나열한다 (순열 백트래킹 그대로). */
    static void arrangeAll(int seatIndex) {
        if (seatIndex == seats.length) {           // 종료 조건: 모든 좌석을 채웠다
            totalCount++;
            System.out.println("  배치 " + totalCount + ": " + String.join(" - ", seats));
            return;
        }

        for (int i = 0; i < students.length; i++) {
            if (used[i]) continue;
            used[i] = true;                        // 선택
            seats[seatIndex] = students[i];
            arrangeAll(seatIndex + 1);             // 진행
            used[i] = false;                       // 취소: 상태 복원
            seats[seatIndex] = null;
        }
    }

    /** [2] 같은 구조에 가지치기 검사를 한 번 추가한다. */
    static void arrangeWithRule(int seatIndex) {
        if (seatIndex == seats.length) {
            validCount++;
            System.out.println("  통과 " + validCount + ": " + String.join(" - ", seats));
            return;
        }

        for (int i = 0; i < students.length; i++) {
            if (used[i]) continue;

            // 가지치기: 바로 왼쪽 좌석과 금지된 짝이면 이 가지는 내려가지 않는다
            if (seatIndex > 0 && isForbiddenPair(seats[seatIndex - 1], students[i])) {
                pruneCount++;
                continue;
            }

            used[i] = true;
            seats[seatIndex] = students[i];
            arrangeWithRule(seatIndex + 1);
            used[i] = false;
            seats[seatIndex] = null;
        }
    }

    public static void main(String[] args) {
        System.out.println("[1] 제약 없는 전체 좌석 배치 (3! = 6가지)");
        arrangeAll(0);

        System.out.println();
        System.out.println("[2] 규칙 적용: 민준-서연 옆자리 금지");
        arrangeWithRule(0);

        System.out.println();
        System.out.println("전체 " + totalCount + "가지 중 " + validCount + "가지만 규칙을 통과");
        System.out.println("가지치기 발동 " + pruneCount + "번 — 어긋나는 가지는 끝까지 만들기 전에 잘랐다.");
    }
}
