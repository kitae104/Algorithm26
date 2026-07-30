import java.util.ArrayList;
import java.util.List;

public class MeetingSchedulerStarter {

    /** 모임 정보를 담는 작은 기록용 클래스 (이름, 시작 시각, 종료 시각) */
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

    /** 1단계: 종료 시각(end) 기준 오름차순으로 정렬한다. (4강 삽입 정렬 재사용) */
    static void sortByEndTime(Meeting[] meetings) {
        // TODO 1: 삽입 정렬로 meetings를 end가 작은 순서대로 정렬하세요.
        //         (힌트: 4강의 삽입 정렬에서 비교 기준만 meetings[j].end > key.end로 바꿉니다)
    }

    /** 2단계: 검토 중인 모임을 선택해도 되는지 판정한다. */
    static boolean canSelect(Meeting next, int lastEnd) {
        // TODO 2: 모임의 시작 시각이 마지막 종료 시각(lastEnd) 이상이면 true를 반환하세요.
        //         (종료 시각과 시작 시각이 같은 '인접 모임'은 선택 가능해야 합니다)
        return false;
    }

    /** 3단계: 정렬된 목록을 앞에서부터 검토하며 겹치지 않는 모임을 최대한 선택한다. */
    static List<Meeting> selectMeetings(Meeting[] sorted) {
        List<Meeting> selected = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;
        // TODO 3: sorted를 순서대로 검토하면서
        //         - canSelect가 true이면: selected에 추가하고 lastEnd를 갱신한 뒤 "선택" 출력
        //         - false이면: "탈락" 출력 (탈락 이유도 함께)
        return selected;
    }

    public static void main(String[] args) {
        // 동아리 방 사용 신청 7건
        Meeting[] meetings = {
            new Meeting("스터디", 9, 11),
            new Meeting("밴드 연습", 10, 12),
            new Meeting("코딩 모임", 11, 13),
            new Meeting("영화 감상", 12, 16),
            new Meeting("보드게임", 13, 14),
            new Meeting("발표 준비", 14, 16),
            new Meeting("저녁 모임", 16, 18)
        };

        System.out.println("== 동아리 방 예약 프로그램 ==");
        System.out.println("신청된 모임: " + meetings.length + "건");
        System.out.println();

        System.out.println("[1단계] 종료 시간 순 정렬 결과");
        sortByEndTime(meetings);
        for (Meeting m : meetings) {
            System.out.println(m.name + " (" + m.start + "시~" + m.end + "시)");
        }
        System.out.println();

        System.out.println("[2단계] 그리디 선택 과정");
        List<Meeting> selected = selectMeetings(meetings);
        System.out.println();

        // TODO 4: 선택된 모임의 이름을 쉼표로 이어 붙여 [결과] 줄을 완성하세요.
        System.out.println("[결과] 최대 " + selected.size() + "개 모임 배정");
    }
}
