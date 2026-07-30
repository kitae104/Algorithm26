import java.util.ArrayList;
import java.util.List;

public class Step4GreedySelect {

    /** 회의 정보를 담는 작은 기록용 클래스 */
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

    /** 4강의 삽입 정렬을 재사용해 종료 시각 기준 오름차순으로 정렬한다. */
    static void sortByEndTime(Meeting[] meetings) {
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

    /** 정렬된 회의 목록에서 겹치지 않는 회의를 최대한 많이 선택한다. */
    static List<Meeting> selectMeetings(Meeting[] sorted) {
        List<Meeting> selected = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;   // 아직 아무 회의도 선택하지 않았다

        for (Meeting m : sorted) {
            if (m.start >= lastEnd) {      // 그리디 선택: 겹치지 않으면 무조건 선택
                selected.add(m);
                lastEnd = m.end;           // 마지막 종료 시각을 갱신
            }
        }
        return selected;
    }

    public static void main(String[] args) {
        Meeting[] meetings = {
            new Meeting("전략 기획", 8, 12),
            new Meeting("디자인 리뷰", 9, 10),
            new Meeting("개발 스탠드업", 10, 11),
            new Meeting("고객 미팅", 11, 13),
            new Meeting("채용 면접", 12, 14),
            new Meeting("팀 회고", 13, 15)
        };

        sortByEndTime(meetings);
        List<Meeting> selected = selectMeetings(meetings);

        System.out.println("[선택된 회의]");
        for (Meeting m : selected) {
            System.out.println(m.name + " (" + m.start + "시 ~ " + m.end + "시)");
        }
        System.out.println("최대 " + selected.size() + "개의 회의를 열 수 있습니다.");
    }
}
