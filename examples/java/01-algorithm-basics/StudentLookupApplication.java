/*
 * 1강 실제 데이터 응용 예제: 출석 번호로 학생 찾기
 * 같은 "찾기" 문제를 두 가지 방식으로 해결하고 비교 횟수를 센다.
 *   방법 A: 앞에서부터 한 명씩 확인한다            (최악 n번 비교, O(n))
 *   방법 B: "출석 번호 = 배열 인덱스 + 1" 규칙을 이용해 바로 접근한다 (1번, O(1))
 * 데이터의 "규칙"을 알면 알고리즘이 극적으로 빨라진다는 것을 보여준다.
 */
public class StudentLookupApplication {

    /** 방법 A: 순서대로 확인. 몇 번 비교했는지 함께 출력한다. */
    static String findBySequentialCheck(String[] names, int attendanceNo) {
        int compareCount = 0;
        for (int i = 0; i < names.length; i++) {
            compareCount++;
            // 이 자리의 출석 번호는 i + 1 이다
            if (i + 1 == attendanceNo) {
                System.out.println("  [방법 A] 비교 " + compareCount + "번 만에 발견");
                return names[i];
            }
        }
        System.out.println("  [방법 A] " + compareCount + "번 비교했지만 없음");
        return null;
    }

    /** 방법 B: 번호 규칙(번호 = 인덱스 + 1)을 이용해 한 번에 접근한다. */
    static String findByDirectAccess(String[] names, int attendanceNo) {
        if (attendanceNo < 1 || attendanceNo > names.length) {
            System.out.println("  [방법 B] 접근 1번 — 범위를 벗어난 번호");
            return null;
        }
        System.out.println("  [방법 B] 접근 1번 만에 발견");
        return names[attendanceNo - 1];
    }

    public static void main(String[] args) {
        // 출석 번호 1번부터 8번까지의 학생 이름
        String[] names = {"김하늘", "이준호", "박서연", "최민재",
                          "정다은", "강지훈", "윤소미", "한도윤"};

        int[] searchTargets = {1, 5, 8, 12};

        for (int no : searchTargets) {
            System.out.println("출석 번호 " + no + "번 학생 찾기:");
            String resultA = findBySequentialCheck(names, no);
            String resultB = findByDirectAccess(names, no);
            System.out.println("  결과: " + (resultA == null ? "없음" : resultA)
                    + " / 두 방법의 결과 일치 = "
                    + java.util.Objects.equals(resultA, resultB));
            System.out.println();
        }

        System.out.println("데이터에 규칙(번호 = 인덱스 + 1)이 있으면 O(n) 확인을 O(1) 접근으로 바꿀 수 있다.");
    }
}
