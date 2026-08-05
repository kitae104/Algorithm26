import java.util.function.Predicate;

/**
 * 5강 「람다·스트림 수정 문제」 정답.
 *
 * 순차 탐색의 "무엇을 찾는가"를 Predicate로 분리한다.
 * 알고리즘은 그대로 O(n)이며, 이진 탐색은 이 방식으로 일반화되지 않는다.
 */
public class ModernizeSolution {

    /** BookSearchApplication과 같은 구조 */
    static class Book {
        int number;
        String title;
        int stock;

        Book(int number, String title, int stock) {
            this.number = number;
            this.title = title;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return "[" + number + "] " + title + " (재고 " + stock + "권)";
        }
    }

    /* ─────────── 이전: 찾는 것이 바뀔 때마다 메서드가 늘어난다 ─────────── */

    static int findByNumberLoop(Book[] books, int number) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].number == number) return i;
        }
        return -1;
    }

    static int findByTitleLoop(Book[] books, String title) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].title.equals(title)) return i;
        }
        return -1;
    }

    static int findInStockLoop(Book[] books) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].stock > 0) return i;
        }
        return -1;
    }

    /* ─────────── 이후: 순회는 한 번만 쓰고, 조건은 받는다 ─────────── */

    /**
     * 조건에 맞는 첫 도서의 위치. 없으면 -1.
     * 순차 탐색 그대로다 — 찾는 즉시 return하는 조기 중단도 그대로, 복잡도도 O(n) 그대로.
     */
    static int findFirstIndex(Book[] books, Predicate<Book> match) {
        for (int i = 0; i < books.length; i++) {
            if (match.test(books[i])) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Book[] books = {
            new Book(1001, "알고리즘 첫걸음", 3),
            new Book(1203, "자바 프로그래밍 입문", 5),
            new Book(1450, "자료구조의 이해", 2),
            new Book(2088, "데이터베이스 개론", 0),
            new Book(2311, "운영체제 원리", 4),
            new Book(2754, "컴퓨터 네트워크", 1)
        };

        System.out.println("== 문제 ① 조건을 값으로 받는 순차 탐색 ==");

        int byNumberOld = findByNumberLoop(books, 2311);
        int byNumberNew = findFirstIndex(books, b -> b.number == 2311);
        System.out.println("  번호 2311  이전 " + byNumberOld + " | 이후 " + byNumberNew
                + " | 같은가 " + (byNumberOld == byNumberNew));

        int byTitleOld = findByTitleLoop(books, "자료구조의 이해");
        int byTitleNew = findFirstIndex(books, b -> b.title.equals("자료구조의 이해"));
        System.out.println("  제목 검색  이전 " + byTitleOld + " | 이후 " + byTitleNew
                + " | 같은가 " + (byTitleOld == byTitleNew));

        int inStockOld = findInStockLoop(books);
        int inStockNew = findFirstIndex(books, b -> b.stock > 0);
        System.out.println("  재고 있음  이전 " + inStockOld + " | 이후 " + inStockNew
                + " | 같은가 " + (inStockOld == inStockNew));

        // 메서드를 새로 만들지 않고 새 조건을 바로 쓸 수 있다 — 이것이 실제로 얻는 것이다
        int found = findFirstIndex(books, b -> b.stock >= 3 && b.number > 1100);
        System.out.println("  새 조건(재고 3권 이상 + 번호 1100 초과): "
                + (found >= 0 ? books[found].toString() : "없음"));

        System.out.println();
        System.out.println("== 문제 ② 이진 탐색은 왜 이렇게 못 바꾸는가 ==");
        System.out.println("  Predicate가 답할 수 있는 것은 '맞다 / 아니다' 둘뿐이다.");
        System.out.println("  이진 탐색에 필요한 답은 '같다 / 왼쪽으로 / 오른쪽으로' 셋이다.");
        System.out.println("  게다가 그 판정은 배열이 정렬된 기준과 반드시 같아야 한다.");
        System.out.println("  그래서 이진 탐색을 일반화하려면 Predicate가 아니라 Comparator를 받아야 한다.");
        System.out.println();
        System.out.println("  findFirstIndex는 여전히 O(n)이다. 람다를 썼다고 이진 탐색이 되지 않는다.");
        System.out.println("  O(log n)을 만드는 것은 '정렬해 두었다'는 조건이지 문법이 아니다.");
    }
}
