public class Step1MeetingData {

    /** 회의 정보를 담는 작은 기록용 클래스 (이름, 시작 시각, 종료 시각) */
    static class Meeting {
        String name;
        int start;   // 시작 시각 (시 단위)
        int end;     // 종료 시각 (시 단위)

        Meeting(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

    public static void main(String[] args) {
        // 오늘 신청된 회의 6건 (입력 데이터)
        Meeting[] meetings = {
            new Meeting("전략 기획", 8, 12),
            new Meeting("디자인 리뷰", 9, 10),
            new Meeting("개발 스탠드업", 10, 11),
            new Meeting("고객 미팅", 11, 13),
            new Meeting("채용 면접", 12, 14),
            new Meeting("팀 회고", 13, 15)
        };

        System.out.println("신청된 회의 수: " + meetings.length);

        // 배열의 내용을 처음부터 끝까지 출력한다
        for (int i = 0; i < meetings.length; i++) {
            Meeting m = meetings[i];
            System.out.println("meetings[" + i + "] = " + m.name
                    + " (" + m.start + "시 ~ " + m.end + "시)");
        }
    }
}
