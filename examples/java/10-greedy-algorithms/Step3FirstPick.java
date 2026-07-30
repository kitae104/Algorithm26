public class Step3FirstPick {

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

    /** 4강의 삽입 정렬을 재사용해 종료 시각(end) 기준 오름차순으로 정렬한다. */
    static void sortByEndTime(Meeting[] meetings) {
        for (int i = 1; i < meetings.length; i++) {
            Meeting key = meetings[i];
            int j = i - 1;
            // key보다 늦게 끝나는 회의를 한 칸씩 뒤로 민다
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

        System.out.println("[종료 시간 순 정렬 결과]");
        for (Meeting m : meetings) {
            System.out.println(m.name + " (" + m.start + "시 ~ " + m.end + "시)");
        }

        // 그리디의 첫 선택: 가장 일찍 끝나는 회의(정렬 후 첫 번째)를 무조건 선택한다
        Meeting first = meetings[0];
        int lastEnd = first.end;   // 지금까지 선택한 회의 중 마지막 종료 시각
        System.out.println();
        System.out.println("첫 선택: " + first.name + " → 마지막 종료 시각 " + lastEnd + "시");

        // 다음 후보 하나만 판단해 본다: 시작 시각이 lastEnd 이상이면 함께 열 수 있다
        Meeting next = meetings[1];
        if (next.start >= lastEnd) {
            System.out.println("다음 후보 " + next.name + ": 시작 " + next.start
                    + "시 >= 마지막 종료 " + lastEnd + "시 → 선택 가능");
        } else {
            System.out.println("다음 후보 " + next.name + ": 시작 " + next.start
                    + "시 < 마지막 종료 " + lastEnd + "시 → 겹쳐서 탈락");
        }
    }
}
