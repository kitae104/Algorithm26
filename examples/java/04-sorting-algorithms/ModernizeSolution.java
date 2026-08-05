import java.util.Arrays;
import java.util.Comparator;

/**
 * 4강 「람다·스트림 수정 문제」 정답.
 *
 * ProductSorterSolution.java의 블록 몸통 람다 세 개를
 * Comparator의 조립 메서드로 다시 쓴 것이다.
 * 정렬 알고리즘(삽입 정렬)은 한 줄도 바뀌지 않는다 — 최악 O(n^2) 그대로다.
 */
public class ModernizeSolution {

    static class Product {
        String name;
        int price;
        double rating;

        Product(String name, int price, double rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
        }

        String getName() { return name; }

        @Override
        public String toString() {
            return name + " (" + price + "원, 평점 " + rating + ")";
        }
    }

    /* ─────────── 이전: ProductSorterSolution의 블록 몸통 람다 ─────────── */

    static final Comparator<Product> PRICE_ASC_OLD = (a, b) -> {
        return Integer.compare(a.price, b.price);
    };

    static final Comparator<Product> RATING_DESC_OLD = (a, b) -> {
        return Double.compare(b.rating, a.rating);   // 인자 순서를 뒤집어 내림차순
    };

    static final Comparator<Product> RATING_DESC_THEN_NAME_OLD = (a, b) -> {
        int byRating = Double.compare(b.rating, a.rating);
        if (byRating != 0) {
            return byRating;                          // 평점이 다르면 평점으로 결정
        }
        return a.name.compareTo(b.name);              // 동점일 때만 이름으로 결정
    };

    /* ─────────── 이후: 조립해서 만든 기준 ─────────── */

    // "무엇으로 비교할지"만 준다. 부호를 따질 일이 없다.
    static final Comparator<Product> PRICE_ASC_NEW =
            Comparator.comparingInt(p -> p.price);

    // 내림차순은 인자 순서를 뒤집는 것이 아니라 reversed()로 말한다.
    static final Comparator<Product> RATING_DESC_NEW =
            Comparator.comparingDouble((Product p) -> p.rating).reversed();

    // "평점으로 비교 → 뒤집기 → 동점이면 이름으로"가 순서대로 읽힌다.
    static final Comparator<Product> RATING_DESC_THEN_NAME_NEW =
            Comparator.comparingDouble((Product p) -> p.rating)
                      .reversed()
                      .thenComparing(Product::getName);

    /** ProductSorterSolution과 같은 삽입 정렬 — 이 코드는 바뀌지 않는다 */
    static void insertionSort(Product[] arr, Comparator<Product> comp) {
        for (int i = 1; i < arr.length; i++) {
            Product key = arr[i];
            int j = i - 1;
            // 주의: > 0 이어야 안정 정렬이다 (>= 0으로 쓰면 같은 값의 순서가 뒤집힌다)
            while (j >= 0 && comp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    static String namesOf(Product[] items) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(items[i].name);
        }
        return sb.toString();
    }

    /** 같은 배열에 두 기준을 각각 적용해 결과 순서가 같은지 확인한다 */
    static boolean sameOrder(Product[] source,
                             Comparator<Product> oldRule,
                             Comparator<Product> newRule) {
        Product[] a = Arrays.copyOf(source, source.length);
        Product[] b = Arrays.copyOf(source, source.length);
        insertionSort(a, oldRule);
        insertionSort(b, newRule);
        return Arrays.equals(a, b);
    }

    public static void main(String[] args) {
        Product[] products = {
            new Product("무선 마우스", 23000, 4.5),
            new Product("기계식 키보드", 89000, 4.8),
            new Product("USB 허브", 15000, 4.2),
            new Product("모니터 받침대", 23000, 4.7),
            new Product("노트북 파우치", 18000, 4.5),
            new Product("웹캠", 54000, 4.2)
        };

        System.out.println("== 문제 ① 단일 기준 ==");
        System.out.println("  가격 오름차순 같은가 "
                + sameOrder(products, PRICE_ASC_OLD, PRICE_ASC_NEW));
        System.out.println("  평점 내림차순 같은가 "
                + sameOrder(products, RATING_DESC_OLD, RATING_DESC_NEW));

        System.out.println();
        System.out.println("== 문제 ② 다중 기준 ==");
        System.out.println("  평점 내림차순 + 동점 시 이름순 같은가 "
                + sameOrder(products, RATING_DESC_THEN_NAME_OLD, RATING_DESC_THEN_NAME_NEW));

        Product[] sorted = Arrays.copyOf(products, products.length);
        insertionSort(sorted, RATING_DESC_THEN_NAME_NEW);
        System.out.println("  결과: " + namesOf(sorted));

        System.out.println();
        System.out.println("== 안정 정렬이 깨지지 않았는지 ==");
        // 가격이 같은 23000원 두 상품이 입력 순서를 지키는지 확인한다
        Product[] byPrice = Arrays.copyOf(products, products.length);
        insertionSort(byPrice, PRICE_ASC_NEW);
        System.out.println("  " + namesOf(byPrice));
        System.out.println("  23000원 두 상품이 입력 순서(무선 마우스 → 모니터 받침대)를 유지: "
                + (byPrice[2].name.equals("무선 마우스") && byPrice[3].name.equals("모니터 받침대")));

        System.out.println();
        System.out.println("정렬 코드(insertionSort)는 한 줄도 바뀌지 않았다.");
        System.out.println("비교 횟수도 같다 — 삽입 정렬은 여전히 최악 O(n^2)이다.");
    }
}
