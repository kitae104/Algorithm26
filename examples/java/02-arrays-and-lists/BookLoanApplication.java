import java.util.ArrayList;

public class BookLoanApplication {

    /** 특정 회원의 대출 기록을 모두 찾아 출력한다 — 조건 검색, O(n) */
    static int printLoansOf(ArrayList<String> loans, String member) {
        int found = 0;
        for (int i = 0; i < loans.size(); i++) {
            String record = loans.get(i);              // 기록 형식: "회원:책제목"
            if (record.startsWith(member + ":")) {
                System.out.println("  " + i + "번 기록: " + record);
                found++;
            }
        }
        if (found == 0) {
            System.out.println("  (대출 기록 없음)");
        }
        return found;
    }

    /** 반납 처리: record와 일치하는 기록 하나를 찾아 삭제한다 — O(n) */
    static boolean returnBook(ArrayList<String> loans, String record) {
        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i).equals(record)) {
                loans.remove(i);   // 삭제하면 뒤 원소가 한 칸씩 앞으로 밀려온다
                return true;       // 하나만 지우고 즉시 종료 (1강의 조기 중단!)
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // [배열 방식] 크기가 5로 고정 — 여섯 번째 대출은 받을 수 없다
        String[] loanArray = new String[5];
        loanArray[0] = "김하늘:자바의 정석";
        loanArray[1] = "이준호:알고리즘 도감";
        loanArray[2] = "김하늘:클린 코드";
        loanArray[3] = "박서연:자바의 정석";
        loanArray[4] = "이준호:데이터베이스 첫걸음";
        System.out.println("[배열] 칸 수 " + loanArray.length
                + " — 가득 차면 더 큰 배열을 만들어 직접 복사해야 한다");

        // [리스트 방식] 필요할 때마다 스스로 자란다
        ArrayList<String> loans = new ArrayList<>();
        loans.add("김하늘:자바의 정석");
        loans.add("이준호:알고리즘 도감");
        loans.add("김하늘:클린 코드");
        loans.add("박서연:자바의 정석");
        loans.add("이준호:데이터베이스 첫걸음");
        System.out.println("[리스트] 현재 대출 " + loans.size() + "건");

        // 추가: 크기 걱정 없이 끝에 붙인다
        loans.add("김하늘:이산수학");
        System.out.println("신규 대출 추가 후: " + loans.size() + "건");

        System.out.println();
        System.out.println("김하늘 님의 대출 기록 검색:");
        int count = printLoansOf(loans, "김하늘");
        System.out.println("→ 총 " + count + "건");

        System.out.println();
        System.out.println("반납 처리: \"김하늘:클린 코드\"");
        boolean returned = returnBook(loans, "김하늘:클린 코드");
        System.out.println("반납 성공 = " + returned + ", 남은 대출 " + loans.size() + "건");

        System.out.println();
        System.out.println("반납 후 김하늘 님의 대출 기록:");
        printLoansOf(loans, "김하늘");
    }
}
