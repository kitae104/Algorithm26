import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collectors — "어떻게 모을 것인가"를 정하는 도구 모음.
 * toSet / toMap / groupingBy / counting / partitioningBy / joining을 한 화면에서 본다.
 */
public class CollectorsDemo {

    record Student(String name, String dept, int score) { }

    static String grade(int score) {
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        return "F";
    }

    public static void main(String[] args) {
        List<Student> students = List.of(
            new Student("김인하", "컴퓨터시스템", 92),
            new Student("이공전", "컴퓨터시스템", 78),
            new Student("박알고", "전기전자", 85),
            new Student("최시스", "컴퓨터시스템", 64),
            new Student("정통신", "전기전자", 95),
            new Student("한자료", "기계", 88),
            new Student("오탐색", "전기전자", 71)
        );

        System.out.println("학생 " + students.size() + "명");
        System.out.println();

        System.out.println("[toList / toSet]");
        List<String> names = students.stream().map(Student::name).toList();
        Set<String> depts = students.stream().map(Student::dept).collect(Collectors.toSet());
        System.out.println("  이름 목록: " + names);
        System.out.println("  학과 집합: " + depts + "  (중복 제거됨)");
        System.out.println();

        System.out.println("[toMap — 이름 → 점수]");
        Map<String, Integer> scoreByName = students.stream()
                .collect(Collectors.toMap(Student::name, Student::score));
        System.out.println("  " + scoreByName);
        System.out.println("  주의: 키가 겹치면 IllegalStateException이 납니다.");
        System.out.println("        겹칠 수 있으면 (a, b) -> a 같은 병합 규칙을 세 번째 인자로 줍니다.");
        System.out.println();

        System.out.println("[groupingBy — 학과별로 묶기]");
        Map<String, List<String>> byDept = students.stream()
                .collect(Collectors.groupingBy(Student::dept,
                        Collectors.mapping(Student::name, Collectors.toList())));
        byDept.forEach((dept, list) -> System.out.println("  " + dept + ": " + list));
        System.out.println();

        System.out.println("[groupingBy + counting — 등급별 인원]");
        Map<String, Long> byGrade = students.stream()
                .collect(Collectors.groupingBy(s -> grade(s.score()), Collectors.counting()));
        System.out.println("  " + byGrade);
        System.out.println("  → 3강의 단어 빈도표와 완전히 같은 구조입니다.");
        System.out.println();

        System.out.println("[groupingBy + averagingInt — 학과별 평균]");
        Map<String, Double> avgByDept = students.stream()
                .collect(Collectors.groupingBy(Student::dept, Collectors.averagingInt(Student::score)));
        avgByDept.forEach((dept, avg) -> System.out.println("  " + dept + ": " + String.format("%.1f", avg)));
        System.out.println();

        System.out.println("[partitioningBy — 참/거짓 두 덩어리]");
        Map<Boolean, List<String>> passFail = students.stream()
                .collect(Collectors.partitioningBy(s -> s.score() >= 80,
                        Collectors.mapping(Student::name, Collectors.toList())));
        System.out.println("  80점 이상: " + passFail.get(true));
        System.out.println("  80점 미만: " + passFail.get(false));
        System.out.println("  → groupingBy와 달리 키가 항상 true/false 둘뿐입니다.");
        System.out.println();

        System.out.println("[joining — 문자열로 잇기]");
        String line = students.stream()
                .filter(s -> s.score() >= 85)
                .map(Student::name)
                .collect(Collectors.joining(", ", "우수: [", "]"));
        System.out.println("  " + line);
    }
}
