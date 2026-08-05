import java.util.function.Predicate;

/**
 * 5강 BookSearch의 "조건마다 메서드를 하나씩 만들던 코드"를
 * Predicate 하나를 받는 메서드로 합친다.
 *
 * 중요: 합쳐도 알고리즘은 그대로 순차 탐색 O(n)이다.
 * 람다식은 표현을 바꿀 뿐 복잡도를 바꾸지 않는다 — 비교 횟수로 직접 확인한다.
 */
public class SearchRewrite {

    static class Book {
        final String title;
        final String author;
        final int price;

        Book(String title, String author, int price) {
            this.title = title;
            this.author = author;
            this.price = price;
        }

        @Override
        public String toString() {
            return title + " / " + author + " / " + price + "원";
        }
    }

    /* ── 이전: 조건이 늘 때마다 거의 같은 메서드가 하나씩 늘어난다 ── */

    static int findByTitle(Book[] books, String title) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].title.equals(title)) {
                return i;
            }
        }
        return -1;
    }

    static int findByAuthor(Book[] books, String author) {
        for (int i = 0; i < books.length; i++) {
            if (books[i].author.equals(author)) {
                return i;
            }
        }
        return -1;
    }

    /* ── 이후: 순회는 한 번만 쓰고 조건은 값으로 받는다 ───────────── */

    static int comparisons = 0;   // 실제 일의 양을 세는 카운터 (1강의 습관)

    static int findFirst(Book[] books, Predicate<Book> match) {
        for (int i = 0; i < books.length; i++) {
            comparisons++;
            if (match.test(books[i])) {
                return i;         // 찾는 즉시 중단 — 5강의 조기 중단 그대로
            }
        }
        return -1;
    }

    static void report(String label, Book[] books, int index) {
        if (index >= 0) {
            System.out.println("  " + label + " → [" + index + "] " + books[index]);
        } else {
            System.out.println("  " + label + " → 없음");
        }
    }

    public static void main(String[] args) {
        Book[] books = {
            new Book("자료구조", "김인하", 28000),
            new Book("알고리즘 입문", "이공전", 32000),
            new Book("자바의 기초", "박알고", 19000),
            new Book("운영체제", "최시스", 35000),
            new Book("네트워크 개론", "정통신", 17000)
        };

        System.out.println("[이전 — 조건마다 메서드]");
        report("제목이 '운영체제'", books, findByTitle(books, "운영체제"));
        report("저자가 '박알고'", books, findByAuthor(books, "박알고"));
        System.out.println("  새 조건(가격 2만원 미만)을 넣으려면 메서드를 또 만들어야 합니다.");
        System.out.println();

        System.out.println("[이후 — Predicate 하나로]");
        comparisons = 0;
        report("제목이 '운영체제'", books, findFirst(books, b -> b.title.equals("운영체제")));
        report("저자가 '박알고'", books, findFirst(books, b -> b.author.equals("박알고")));
        report("가격 2만원 미만", books, findFirst(books, b -> b.price < 20000));
        report("제목에 '자바' 포함", books, findFirst(books, b -> b.title.contains("자바")));
        System.out.println("  findFirst는 한 번만 만들었습니다.");
        System.out.println();

        System.out.println("[복잡도는 그대로다]");
        comparisons = 0;
        findFirst(books, b -> b.title.equals("네트워크 개론"));   // 마지막 원소
        System.out.println("  마지막 원소를 찾을 때 비교 횟수: " + comparisons + " (원소 " + books.length + "개)");
        comparisons = 0;
        findFirst(books, b -> b.title.equals("없는 책"));
        System.out.println("  못 찾았을 때 비교 횟수      : " + comparisons);
        System.out.println("  → 최악의 경우 n번. 람다를 써도 순차 탐색은 O(n)입니다.");
        System.out.println("  → O(log n)으로 줄이려면 5강의 이진 탐색이 필요합니다. 문법의 문제가 아닙니다.");
    }
}
