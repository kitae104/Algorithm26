/** 도서 1권의 정보 — Product와 구조가 같다 (고유 번호, 이름, 분류, 수량) */
public class Book {
    int id;          // 등록 번호 (고유한 값 — 검색의 기준)
    String title;    // 제목
    String genre;    // 장르 (분류의 기준)
    int copies;      // 대출 가능 권수 (경고의 기준)

    Book(int id, String title, String genre, int copies) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.copies = copies;
    }

    String summary() {
        return "[" + id + "] " + title + " | " + genre + " | 대출 가능 " + copies + "권";
    }
}
