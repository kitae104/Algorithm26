import java.util.Arrays;
import java.util.Comparator;

public class ProductSorterSolution {

    /** 상품: 이름, 가격, 평점 */
    static class Product {
        String name;
        int price;      // 원
        double rating;  // 0.0 ~ 5.0

        Product(String name, int price, double rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return name + " (" + price + "원, 평점 " + rating + ")";
        }
    }

    /** 기준 1: 가격 오름차순 */
    static final Comparator<Product> PRICE_ASC = (a, b) -> {
        return Integer.compare(a.price, b.price);
    };

    /** 기준 2: 평점 내림차순 — 인자 순서를 바꾸면 방향이 뒤집힌다 */
    static final Comparator<Product> RATING_DESC = (a, b) -> {
        return Double.compare(b.rating, a.rating);
    };

    /** 기준 3: 평점 내림차순, 동점이면 이름 오름차순 (다중 기준) */
    static final Comparator<Product> RATING_DESC_THEN_NAME = (a, b) -> {
        int byRating = Double.compare(b.rating, a.rating);
        if (byRating != 0) {
            return byRating;      // 평점이 다르면 평점으로 결정
        }
        return a.name.compareTo(b.name); // 동점일 때만 이름으로 결정
    };

    /** 직접 구현한 삽입 정렬 — 안정 정렬이므로 같은 값의 입력 순서가 유지된다. */
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

    static void printAll(String title, Product[] items) {
        System.out.println("[" + title + "]");
        for (int i = 0; i < items.length; i++) {
            System.out.println("  " + (i + 1) + ". " + items[i]);
        }
        System.out.println();
    }

    /** 비교 검증용: 같은 기준으로 Arrays.sort한 결과와 일치하는지 확인한다. */
    static boolean matchesLibrarySort(Product[] mine, Product[] original,
                                      Comparator<Product> comp) {
        Product[] expected = Arrays.copyOf(original, original.length);
        Arrays.sort(expected, comp); // 검증용으로만 사용 (구현 대체 금지!)
        return Arrays.equals(mine, expected);
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

        printAll("정렬 전 (입력 순서)", products);

        Product[] byPrice = Arrays.copyOf(products, products.length);
        insertionSort(byPrice, PRICE_ASC);
        printAll("1) 가격 오름차순", byPrice);

        Product[] byRating = Arrays.copyOf(products, products.length);
        insertionSort(byRating, RATING_DESC);
        printAll("2) 평점 내림차순 (단일 기준)", byRating);

        Product[] byMulti = Arrays.copyOf(products, products.length);
        insertionSort(byMulti, RATING_DESC_THEN_NAME);
        printAll("3) 평점 내림차순 + 동점 시 이름순 (다중 기준)", byMulti);

        System.out.println("안정성 확인: 가격이 같은 23000원 상품 두 개가");
        System.out.println("  입력 순서(무선 마우스 → 모니터 받침대)를 유지했습니다.");
        System.out.println();
        System.out.println("Arrays.sort 결과와 일치(검증): "
                + (matchesLibrarySort(byPrice, products, PRICE_ASC)
                && matchesLibrarySort(byRating, products, RATING_DESC)
                && matchesLibrarySort(byMulti, products, RATING_DESC_THEN_NAME)));
    }
}
