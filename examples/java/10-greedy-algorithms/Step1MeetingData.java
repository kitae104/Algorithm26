public class Step1MeetingData {

    // Meeting 클래스는 같은 폴더의 Meeting.java에 정의되어 있다.

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
