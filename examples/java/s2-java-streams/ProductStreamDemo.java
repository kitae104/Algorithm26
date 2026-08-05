import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 4강 ProductSortApplication의 데이터를 스트림으로 다룬다.
 *
 * 핵심 두 가지
 *   1) 스트림은 원본을 건드리지 않는다 — Arrays.copyOf가 필요 없어진다.
 *   2) 연산 순서가 비용을 바꾼다 — filter를 먼저, sorted를 나중에.
 */
public class ProductStreamDemo {

    static class Product {
        final String name;
        final int price;
        final double rating;

        Product(String name, int price, double rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return name + "(" + price + "원, " + rating + ")";
        }
    }

    /** 정렬 비용을 눈으로 보기 위해 비교 횟수를 세는 Comparator로 감싼다. */
    static int comparisons = 0;

    static Comparator<Product> counting(Comparator<Product> inner) {
        return (a, b) -> {
            comparisons++;
            return inner.compare(a, b);
        };
    }

    public static void main(String[] args) {
        Product[] products = {
            new Product("보조 배터리", 29000, 4.6),
            new Product("무선 이어폰", 79000, 4.6),
            new Product("스마트폰 거치대", 12000, 4.3),
            new Product("차량용 충전기", 18000, 4.1),
            new Product("블루투스 스피커", 45000, 4.3),
            new Product("USB 허브", 23000, 4.7),
            new Product("노트북 파우치", 15000, 3.9),
            new Product("무선 마우스", 27000, 4.5)
        };

        System.out.println("상품 " + products.length + "개");
        System.out.println();

        /* ── 거르기 · 정렬 · 뽑기 · 변환을 한 줄기로 ──────────── */
        List<String> top3 = Arrays.stream(products)
                .filter(p -> p.rating >= 4.5)
                .sorted(Comparator.comparingInt(p -> p.price))
                .limit(3)
                .map(p -> p.name)
                .toList();

        System.out.println("[평점 4.5 이상, 가격이 싼 순으로 3개]");
        System.out.println("  " + top3);
        System.out.println();

        /* ── 원본은 그대로다 ─────────────────────────────── */
        System.out.println("[원본 배열의 첫 원소] " + products[0]);
        System.out.println("  → 4강에서 Arrays.copyOf로 복사본을 만들던 이유가 사라집니다.");
        System.out.println("    스트림은 원본을 정렬하지 않고 새 결과를 만들어 냅니다.");
        System.out.println();

        /* ── 연산 순서가 비용을 바꾼다 ───────────────────── */
        comparisons = 0;
        Arrays.stream(products)
              .sorted(counting(Comparator.comparingInt(p -> p.price)))
              .filter(p -> p.rating >= 4.5)
              .limit(3)
              .toList();
        int slow = comparisons;

        comparisons = 0;
        Arrays.stream(products)
              .filter(p -> p.rating >= 4.5)
              .sorted(counting(Comparator.comparingInt(p -> p.price)))
              .limit(3)
              .toList();
        int fast = comparisons;

        System.out.println("[정렬 순서에 따른 비교 횟수]");
        System.out.println("  sorted → filter (전부 정렬) : " + slow + "회");
        System.out.println("  filter → sorted (걸러서 정렬): " + fast + "회");
        System.out.println("  → 결과는 같지만 일의 양이 다릅니다. 1강의 '같은 결과, 다른 비용'입니다.");
        System.out.println("    상품이 8개라 차이가 작지만, 1000개라면 확연히 벌어집니다.");
        System.out.println();

        /* ── 그 밖의 자주 쓰는 최종 연산 ─────────────────── */
        System.out.println("[집계]");
        System.out.println("  평균 가격      : "
                + String.format("%.0f원", Arrays.stream(products).mapToInt(p -> p.price).average().orElse(0)));
        System.out.println("  가장 비싼 상품 : "
                + Arrays.stream(products).max(Comparator.comparingInt(p -> p.price)).orElse(null));
        System.out.println("  4.0 미만이 있나: "
                + Arrays.stream(products).anyMatch(p -> p.rating < 4.0));
        System.out.println("  모두 1만원 이상: "
                + Arrays.stream(products).allMatch(p -> p.price >= 10000));
    }
}
