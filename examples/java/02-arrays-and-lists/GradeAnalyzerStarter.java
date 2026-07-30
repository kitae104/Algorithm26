import java.util.ArrayList;

public class GradeAnalyzerStarter {

    /** 학생 한 명의 데이터: 이름 + 점수 */
    static class Student {
        String name;
        int score;

        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    /** 1) 전체 점수 합계 */
    static int totalScore(Student[] students) {
        int sum = 0;
        // TODO 1: 1강의 누적 패턴(sum = sum + ...)으로 모든 학생의 score를 sum에 더하세요.
        return sum;
    }

    /** 2) 평균 점수 */
    static double average(Student[] students) {
        // TODO 2: totalScore를 이용해 평균을 계산하세요.
        //         (int / int 함정 주의 — (double) 캐스팅이 필요합니다)
        return 0.0;
    }

    /** 3) 최고점 학생 */
    static Student findTop(Student[] students) {
        Student top = students[0];
        // TODO 3: 후보 비교 패턴으로 최고점 학생을 찾으세요.
        //         (점수 비교는 students[i].score > top.score)
        return top;
    }

    /** 4) 최저점 학생 */
    static Student findBottom(Student[] students) {
        Student bottom = students[0];
        // TODO 4: findTop과 같은 구조에서 부등호 방향만 바꾸면 됩니다.
        return bottom;
    }

    /** 5) threshold점 이상인 학생 명단 */
    static ArrayList<Student> filterAtLeast(Student[] students, int threshold) {
        ArrayList<Student> result = new ArrayList<>();
        // TODO 5: 조건 검색 패턴 — 조건에 맞는 학생만 result.add(...)로 모으세요.
        return result;
    }

    /** 6) limit점 미만인 학생 명단 (평균 미만 학생 찾기에 사용) */
    static ArrayList<Student> filterBelow(Student[] students, double limit) {
        ArrayList<Student> result = new ArrayList<>();
        // TODO 6: filterAtLeast와 같은 구조로, score < limit 조건으로 모으세요.
        return result;
    }

    /** 명단을 "이름(점수점)" 형태로 한 줄에 출력하는 보조 메서드 (완성되어 있음) */
    static void printStudents(String title, ArrayList<Student> list) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) line.append(", ");
            Student s = list.get(i);
            line.append(s.name).append("(").append(s.score).append("점)");
        }
        System.out.println(title + " (" + list.size() + "명): " + line);
    }

    public static void main(String[] args) {
        Student[] students = {
            new Student("김하늘", 72), new Student("이준호", 85),
            new Student("박서연", 90), new Student("최민재", 66),
            new Student("정다은", 78), new Student("강지훈", 93),
            new Student("윤소미", 55), new Student("한도윤", 81)
        };

        System.out.println("== 성적 분석 리포트 ==");
        System.out.println("학생 수: " + students.length + "명");

        int sum = totalScore(students);
        double avg = average(students);
        System.out.println("합계: " + sum + "점");
        System.out.printf("평균: %.1f점%n", avg);

        Student top = findTop(students);
        Student bottom = findBottom(students);
        System.out.println("최고점: " + top.name + " " + top.score + "점");
        System.out.println("최저점: " + bottom.name + " " + bottom.score + "점");

        printStudents("80점 이상", filterAtLeast(students, 80));
        printStudents("평균 미만", filterBelow(students, avg));
    }
}
