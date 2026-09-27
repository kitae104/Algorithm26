import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 10강 「람다·스트림 변경」.
 *
 * MeetingRoomComplete.java(실습 코드)를 같은 결과가 나오도록 람다와 스트림으로 다시 쓴 것이다.
 */
public class ModernizeSolution {

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

    /** 그리디의 전처리: 종료 시각 기준 오름차순으로 정렬한 새 목록을 만든다. */
    static List<Meeting> sortByEndTime(Meeting[] meetings) {
        return Arrays.stream(meetings)
                .sorted(Comparator.comparingInt(m -> m.end))
                .toList();
    }

    /** 그리디 선택: 일찍 끝나는 순서로 검토하며 겹치지 않으면 무조건 선택 */
    static List<Meeting> selectMeetings(List<Meeting> sorted) {
        List<Meeting> selected = new ArrayList<>();
        int lastEnd = Integer.MIN_VALUE;

        // lastEnd가 선택할 때마다 바뀌므로, 선택 단계는 반복문 그대로 둔다
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
        int[] counts = new int[coins.length];
        int remaining = amount;

        // remaining이 동전마다 바뀌므로, 개수 계산은 반복문 그대로 둔다
        for (int i = 0; i < coins.length; i++) {
            counts[i] = remaining / coins[i];   // 이 동전을 최대 몇 개 쓸 수 있는가
            remaining = remaining % coins[i];   // 남은 금액
        }

        // 1개 이상 쓴 동전만 골라 출력한다
        IntStream.range(0, coins.length)
                .filter(i -> counts[i] > 0)
                .forEach(i -> System.out.println("  " + coins[i] + "원 x " + counts[i] + "개"));

        return Arrays.stream(counts).sum();
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
        List<Meeting> sorted = sortByEndTime(meetings);
        List<Meeting> selected = selectMeetings(sorted);
        selected.forEach(m ->
                System.out.println("  선택: " + m.name + " (" + m.start + "시 ~ " + m.end + "시)"));
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
