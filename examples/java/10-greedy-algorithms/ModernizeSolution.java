import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 10강 「람다·스트림 수정 문제」 정답.
 *
 * MeetingRoomComplete.java의 sortByEndTime은 "종료 시각 기준"이 코드에 박혀 있어
 * 다른 전략을 시험해 보려면 메서드를 통째로 복사해야 했다.
 * 기준을 Comparator로 받으면 세 전략을 같은 코드로 비교할 수 있다.
 */
public class ModernizeSolution {

    static class Meeting {
        String name;
        int start;
        int end;

        Meeting(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

    /* ─────────── 이전: 기준이 박힌 정렬 ─────────── */

    static void sortByEndTimeOld(Meeting[] meetings) {
        for (int i = 1; i < meetings.length; i++) {
            Meeting key = meetings[i];
            int j = i - 1;
            while (j >= 0 && meetings[j].end > key.end) {
                meetings[j + 1] = meetings[j];
                j--;
            }
            meetings[j + 1] = key;
        }
    }

    /* ─────────── 이후: 기준을 받는 정렬 ─────────── */

    /** 같은 삽입 정렬이다. 바뀐 것은 "무엇으로 비교하는가"를 밖에서 받는다는 점뿐이다. */
    static void sortBy(Meeting[] meetings, Comparator<Meeting> rule) {
        for (int i = 1; i < meetings.length; i++) {
            Meeting key = meetings[i];
            int j = i - 1;
            while (j >= 0 && rule.compare(meetings[j], key) > 0) {
                meetings[j + 1] = meetings[j];
                j--;
            }
            meetings[j + 1] = key;
        }
    }

    /** 그리디 선택 — 이 코드는 어떤 기준을 쓰든 바뀌지 않는다 */
    static List<Meeting> selectMeetings(Meeting[] sorted) {
        List<Meeting> selected = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;
        for (Meeting m : sorted) {
            if (m.start >= lastEnd) {
                selected.add(m);
                lastEnd = m.end;
            }
        }
        return selected;
    }

    static String namesOf(List<Meeting> meetings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < meetings.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(meetings.get(i).name);
        }
        return sb.toString();
    }

    static Meeting[] sampleMeetings() {
        return new Meeting[] {
            new Meeting("전략 기획", 8, 12),
            new Meeting("디자인 리뷰", 9, 10),
            new Meeting("개발 스탠드업", 10, 11),
            new Meeting("고객 미팅", 11, 13),
            new Meeting("채용 면접", 12, 14),
            new Meeting("팀 회고", 13, 15)
        };
    }

    public static void main(String[] args) {
        /* ─────────── 문제 ① 기준을 밖에서 받기 ─────────── */
        System.out.println("== 문제 ① 이전 방식과 같은 결과인가 ==");

        Meeting[] oldWay = sampleMeetings();
        sortByEndTimeOld(oldWay);
        List<Meeting> oldResult = selectMeetings(oldWay);

        Comparator<Meeting> byEndTime = Comparator.comparingInt(m -> m.end);
        Meeting[] newWay = sampleMeetings();
        sortBy(newWay, byEndTime);
        List<Meeting> newResult = selectMeetings(newWay);

        System.out.println("  이전 " + namesOf(oldResult));
        System.out.println("  이후 " + namesOf(newResult));
        System.out.println("  같은가 " + namesOf(oldResult).equals(namesOf(newResult)));

        /* ─────────── 세 전략을 같은 코드로 비교 ─────────── */
        System.out.println();
        System.out.println("== 기준만 바꿔 세 전략 비교 ==");

        Comparator<Meeting> byStartTime = Comparator.comparingInt(m -> m.start);
        Comparator<Meeting> byDuration = Comparator.comparingInt(m -> m.end - m.start);

        String[] labels = {"종료 시각 순 (정답 전략)", "시작 시각 순", "소요 시간 순"};
        List<Comparator<Meeting>> rules = Arrays.asList(byEndTime, byStartTime, byDuration);

        for (int i = 0; i < rules.size(); i++) {
            Meeting[] copy = sampleMeetings();
            sortBy(copy, rules.get(i));
            List<Meeting> picked = selectMeetings(copy);
            System.out.println("  " + labels[i] + " → " + picked.size() + "개: " + namesOf(picked));
        }
        System.out.println("  기준 하나를 바꿨을 뿐인데 결과가 달라진다 — 그리디의 성패는 기준에 달려 있다.");

        /* ─────────── 문제 ② 동점 처리를 명시하기 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 종료 시각이 같을 때 ==");

        Meeting[] tie = {
            new Meeting("긴 회의", 8, 12),
            new Meeting("짧은 회의", 11, 12),
            new Meeting("오후 회의", 12, 14)
        };

        // 종료 시각이 같으면 늦게 시작한(= 짧은) 회의를 앞에 둔다
        Comparator<Meeting> byEndThenLateStart =
                Comparator.comparingInt((Meeting m) -> m.end)
                          .thenComparing(Comparator.comparingInt((Meeting m) -> m.start).reversed());

        Meeting[] copy = Arrays.copyOf(tie, tie.length);
        sortBy(copy, byEndThenLateStart);
        System.out.println("  정렬 결과: " + namesOf(Arrays.asList(copy)));
        System.out.println("  선택 결과: " + namesOf(selectMeetings(copy)));
        System.out.println("  동점 규칙을 적지 않으면 입력 순서에 따라 결과가 달라진다.");
        System.out.println("  thenComparing은 그 '적지 않은 규칙'을 코드에 드러낸다.");

        System.out.println();
        System.out.println("정렬은 여전히 삽입 정렬 O(n²), 선택은 O(n)이다. 복잡도는 그대로다.");
    }
}
