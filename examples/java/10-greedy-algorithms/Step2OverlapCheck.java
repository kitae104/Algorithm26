public class Step2OverlapCheck {

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

    /**
     * 두 회의가 시간상 겹치면 true를 반환한다.
     * 한 회의가 끝나는 시각에 다른 회의가 바로 시작하는 것(예: 10시 종료, 10시 시작)은
     * 겹침이 아니다 — 종료 시각은 회의실을 비워 주는 시각이기 때문이다.
     */
    static boolean isOverlap(Meeting a, Meeting b) {
        return a.start < b.end && b.start < a.end;
    }

    public static void main(String[] args) {
        Meeting design = new Meeting("디자인 리뷰", 9, 10);
        Meeting standup = new Meeting("개발 스탠드업", 10, 11);
        Meeting plan = new Meeting("전략 기획", 8, 12);
        Meeting client = new Meeting("고객 미팅", 11, 13);
        Meeting interview = new Meeting("채용 면접", 12, 14);

        // 경계 확인: 10시에 끝나는 회의와 10시에 시작하는 회의는 겹치지 않는다
        System.out.println("디자인 리뷰(9~10) vs 개발 스탠드업(10~11) 겹침? "
                + isOverlap(design, standup));

        // 8~12시 회의는 그 사이의 회의들과 모두 겹친다
        System.out.println("전략 기획(8~12) vs 디자인 리뷰(9~10) 겹침? "
                + isOverlap(plan, design));

        // 일부 구간(12~13시)만 겹쳐도 겹침이다
        System.out.println("고객 미팅(11~13) vs 채용 면접(12~14) 겹침? "
                + isOverlap(client, interview));
    }
}
