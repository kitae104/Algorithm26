import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 같은 설계를 다른 도메인에 재활용하기 — 학교 도서실 도서 관리.
 * "고유 번호가 있는 데이터 + 검색이 잦음 + 분류별 집계"라는 요구사항 패턴이 같으므로,
 * 상품 관리에서 내린 결정(정렬 → 이진 탐색, HashMap 집계)을 그대로 다시 쓴다.
 * 바뀐 것은 데이터 클래스(Product → Book)뿐이다.
 * 이번에는 정렬·탐색도 직접 구현 대신 JDK 표준 라이브러리(List.sort, Collections.binarySearch)를 그대로 쓴다.
 */
public class ProductManagerApplication {

    /** 도서 1권의 정보 — Product와 구조가 같다 (고유 번호, 이름, 분류, 수량) */
    static class Book {
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

    /** 기증받은 순서 그대로의 도서 6권 */
    static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(5023, "이펙티브 자바", "IT", 2));
        books.add(new Book(1101, "데미안", "소설", 1));
        books.add(new Book(3402, "코스모스", "과학", 4));
        books.add(new Book(2205, "1984", "소설", 0));
        books.add(new Book(4310, "클린 코드", "IT", 3));
        books.add(new Book(1508, "어린 왕자", "소설", 5));
        return books;
    }

    public static void main(String[] args) {
        List<Book> books = loadBooks();
        Comparator<Book> byId = Comparator.comparingInt(b -> b.id);

        // 결정 1: 검색이 잦다 → 등록 번호 순 정렬 + 이진 탐색 (이번엔 라이브러리로)
        books.sort(byId);
        System.out.println("== 등록 번호 순 정렬 ==");
        for (Book b : books) {
            System.out.println("  " + b.summary());
        }

        System.out.println();
        System.out.println("== 도서 검색 (이진 탐색 + 예외 처리) ==");
        String[] requests = {"2205", "9999", "일구팔사"};
        for (String request : requests) {
            System.out.println("검색 요청 \"" + request + "\"");
            try {
                int id = Integer.parseInt(request.trim());
                Book probe = new Book(id, "", "", 0);   // 비교에만 쓰는 탐색용 키 객체
                int index = Collections.binarySearch(books, probe, byId);
                if (index >= 0) {
                    System.out.println("  → " + books.get(index).summary());
                } else {
                    System.out.println("  → 번호 " + id + " 도서 없음");
                }
            } catch (NumberFormatException e) {
                System.out.println("  → 오류: \"" + request + "\"은(는) 숫자가 아닙니다.");
            }
        }

        // 결정 2: 장르별 통계가 필요하다 → HashMap 집계 (3강)
        System.out.println();
        System.out.println("== 장르별 보유 현황 ==");
        Map<String, Integer> countByGenre = new HashMap<>();
        List<String> genreOrder = new ArrayList<>();
        for (Book b : books) {
            if (!countByGenre.containsKey(b.genre)) {
                genreOrder.add(b.genre);
                countByGenre.put(b.genre, 0);
            }
            countByGenre.put(b.genre, countByGenre.get(b.genre) + 1);
        }
        for (String genre : genreOrder) {
            System.out.println("  " + genre + " : " + countByGenre.get(genre) + "종");
        }

        // 결정 3: 대출 가능 권수가 부족한 책 → 조건 검색 (2강)
        System.out.println();
        System.out.println("== 대출 불가 임박 (1권 이하) ==");
        for (Book b : books) {
            if (b.copies <= 1) {
                System.out.println("  " + b.summary());
            }
        }

        System.out.println();
        System.out.println("도메인이 상품 → 도서로 바뀌어도, 요구사항 패턴이 같으면 같은 설계를 재사용할 수 있다.");
    }
}
