import java.util.ArrayList;
import java.util.List;

public class MeetingSchedulerSolution {

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

    /** 1단계: 종료 시각(end) 기준 오름차순 정렬 — 4강 삽입 정렬 재사용 */
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

    /** 2단계: 시작 시각이 마지막 종료 시각 이상이면 선택 가능 (인접 모임 허용) */
    static boolean canSelect(Meeting next, int lastEnd) {
        return next.start >= lastEnd;
    }

    /** 3단계: 정렬된 목록을 검토하며 겹치지 않는 모임을 최대한 선택하고, 과정을 출력한다. */
    static List<Meeting> selectMeetings(Meeting[] sorted) {
        List<Meeting> selected = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;

        for (Meeting m : sorted) {
            if (canSelect(m, lastEnd)) {
                selected.add(m);
                lastEnd = m.end;
                System.out.println("선택: " + m.name + " (" + m.start + "시~" + m.end
                        + "시) → 마지막 종료 " + lastEnd + "시");
            } else {
                System.out.println("탈락: " + m.name + " (" + m.start + "시~" + m.end
                        + "시) — 시작 " + m.start + "시 < 마지막 종료 " + lastEnd + "시");
            }
        }
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

        StringBuilder names = new StringBuilder();
        for (int i = 0; i < selected.size(); i++) {
            if (i > 0) names.append(", ");
            names.append(selected.get(i).name);
        }
        System.out.println("[결과] 최대 " + selected.size() + "개 모임 배정: " + names);
    }
}
