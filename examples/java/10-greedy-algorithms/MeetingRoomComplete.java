import java.util.ArrayList;
import java.util.List;

public class MeetingRoomComplete {

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

    /** 그리디의 전처리: 4강의 삽입 정렬로 종료 시각 기준 오름차순 정렬 */
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

    /** 그리디 선택: 일찍 끝나는 순서로 검토하며 겹치지 않으면 무조건 선택 */
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

    /** 그리디 동전 교환: 큰 동전부터 최대한 사용한다. coins는 내림차순 정렬 상태여야 한다. */
    static int greedyCoinChange(int amount, int[] coins) {
        int totalCount = 0;
        int remaining = amount;

        for (int coin : coins) {
            int count = remaining / coin;   // 이 동전을 최대 몇 개 쓸 수 있는가
            if (count > 0) {
                System.out.println("  " + coin + "원 x " + count + "개");
            }
            totalCount += count;
            remaining = remaining % coin;   // 남은 금액
        }
        return totalCount;
    }

    public static void main(String[] args) {
        // 문제 1: 회의실 배정
        Meeting[] meetings = {
            new Meeting("전략 기획", 8, 12),
            new Meeting("디자인 리뷰", 9, 10),
            new Meeting("개발 스탠드업", 10, 11),
            new Meeting("고객 미팅", 11, 13),
            new Meeting("채용 면접", 12, 14),
            new Meeting("팀 회고", 13, 15)
        };

        System.out.println("== 문제 1: 회의실 배정 (신청 " + meetings.length + "건) ==");
        sortByEndTime(meetings);
        List<Meeting> selected = selectMeetings(meetings);
        for (Meeting m : selected) {
            System.out.println("  선택: " + m.name + " (" + m.start + "시 ~ " + m.end + "시)");
        }
        System.out.println("최대 " + selected.size() + "개의 회의를 열 수 있습니다.");

        // 문제 2: 동전 교환 (한국 동전은 서로 배수 관계라 그리디가 항상 최적)
        System.out.println();
        int amount = 1260;
        int[] coins = {500, 100, 50, 10};   // 큰 동전부터 (내림차순)

        System.out.println("== 문제 2: " + amount + "원 거슬러 주기 ==");
        int coinCount = greedyCoinChange(amount, coins);
        System.out.println("총 동전 수: " + coinCount + "개");
    }
}
