import java.util.ArrayList;

public class GradeAnalyzerSolution {

    /** 학생 한 명의 데이터: 이름 + 점수 */
    static class Student {
        String name;
        int score;

        Student(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    /** 1) 전체 점수 합계 — 1강의 누적 패턴, O(n) */
    static int totalScore(Student[] students) {
        int sum = 0;
        for (int i = 0; i < students.length; i++) {
            sum = sum + students[i].score;
        }
        return sum;
    }

    /** 2) 평균 점수 — (double) 캐스팅으로 소수점을 지킨다 */
    static double average(Student[] students) {
        return (double) totalScore(students) / students.length;
    }

    /** 3) 최고점 학생 — 후보 비교 패턴, O(n) */
    static Student findTop(Student[] students) {
        Student top = students[0];
        for (int i = 1; i < students.length; i++) {
            if (students[i].score > top.score) {
                top = students[i];
            }
        }
        return top;
    }

    /** 4) 최저점 학생 — 부등호 방향만 반대, O(n) */
    static Student findBottom(Student[] students) {
        Student bottom = students[0];
        for (int i = 1; i < students.length; i++) {
            if (students[i].score < bottom.score) {
                bottom = students[i];
            }
        }
        return bottom;
    }

    /** 5) threshold점 이상인 학생 명단 — 조건 검색 + ArrayList에 수집, O(n) */
    static ArrayList<Student> filterAtLeast(Student[] students, int threshold) {
        ArrayList<Student> result = new ArrayList<>();
        for (int i = 0; i < students.length; i++) {
            if (students[i].score >= threshold) {
                result.add(students[i]);   // 몇 명이 될지 몰라도 리스트는 스스로 자란다
            }
        }
        return result;
    }

    /** 6) limit점 미만인 학생 명단 — 같은 구조, 조건만 다름, O(n) */
    static ArrayList<Student> filterBelow(Student[] students, double limit) {
        ArrayList<Student> result = new ArrayList<>();
        for (int i = 0; i < students.length; i++) {
            if (students[i].score < limit) {
                result.add(students[i]);
            }
        }
        return result;
    }

    /** 명단을 "이름(점수점)" 형태로 한 줄에 출력하는 보조 메서드 */
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
