public class MeetingRoomTrace {

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

        System.out.println("[회의실 배정 — 실행 추적] 종료 시간 순으로 검토");
        int lastEnd = -1;          // 아직 선택한 회의 없음 (-1은 '없음' 표시용)
        int selectedCount = 0;

        for (int i = 0; i < meetings.length; i++) {
            Meeting m = meetings[i];
            String lastEndText = (lastEnd < 0) ? "없음" : lastEnd + "시";

            if (lastEnd < 0 || m.start >= lastEnd) {
                selectedCount++;
                lastEnd = m.end;
                System.out.println("검토 " + (i + 1) + ": " + m.name
                        + " (" + m.start + "시~" + m.end + "시) | 마지막 종료: " + lastEndText
                        + " | 결정: 선택 → 마지막 종료 " + lastEnd + "시로 갱신");
            } else {
                System.out.println("검토 " + (i + 1) + ": " + m.name
                        + " (" + m.start + "시~" + m.end + "시) | 마지막 종료: " + lastEndText
                        + " | 결정: 탈락 (시작 " + m.start + "시 < " + lastEnd + "시)");
            }
        }
        System.out.println("선택된 회의 수: " + selectedCount + "개");

        // 동전 교환 추적: 매 단계 "가장 큰 동전"이라는 그리디 선택을 확인한다
        System.out.println();
        System.out.println("[동전 교환 — 실행 추적] 목표 금액 1260원");
        int remaining = 1260;
        int totalCoins = 0;
        int[] coins = {500, 100, 50, 10};

        for (int coin : coins) {
            int count = remaining / coin;
            int before = remaining;
            remaining = remaining % coin;
            totalCoins += count;
            System.out.println(coin + "원 동전: " + before + "원에서 " + count
                    + "개 사용 → 남은 금액 " + remaining + "원");
        }
        System.out.println("총 동전 수: " + totalCoins + "개");
    }
}
