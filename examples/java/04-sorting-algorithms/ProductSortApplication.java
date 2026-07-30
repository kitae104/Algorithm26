import java.util.Arrays;
import java.util.Comparator;

public class ProductSortApplication {

    /** 상품: 이름, 가격, 평점. 기본 정렬 기준(가격 오름차순)을 Comparable로 정의한다. */
    static class Product implements Comparable<Product> {
        String name;
        int price;      // 원
        double rating;  // 0.0 ~ 5.0

        Product(String name, int price, double rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
        }

        /** 기본 정렬 기준: 가격 오름차순. 음수=this가 앞, 0=같음, 양수=this가 뒤. */
        @Override
        public int compareTo(Product other) {
            return Integer.compare(this.price, other.price);
        }

        @Override
        public String toString() {
            return name + " (" + price + "원, 평점 " + rating + ")";
        }
    }

    /**
     * 제네릭 삽입 정렬: 어떤 타입이든 Comparator만 갈아 끼우면 정렬할 수 있다.
     * comp.compare(a, b)가 양수이면 "a가 b보다 뒤에 와야 한다"는 뜻이다.
     */
    static <T> void insertionSort(T[] arr, Comparator<? super T> comp) {
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && comp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j]; // key보다 뒤에 와야 할 값을 한 칸 민다
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

    public static void main(String[] args) {
        Product[] products = {
            new Product("보조 배터리", 29000, 4.6),
            new Product("무선 이어폰", 79000, 4.6),
            new Product("스마트폰 거치대", 12000, 4.3),
            new Product("차량용 충전기", 18000, 4.1),
            new Product("블루투스 스피커", 45000, 4.3)
        };

        printAll("정렬 전 (입력 순서)", products);

        // 1) Comparable이 정의한 기본 기준(가격 오름차순)으로 정렬
        Product[] byPrice = Arrays.copyOf(products, products.length);
        insertionSort(byPrice, Comparator.naturalOrder());
        printAll("가격 오름차순 — Comparable의 compareTo 사용", byPrice);

        // 2) Comparator로 기준 교체: 평점 내림차순, 동점이면 이름 오름차순
        Comparator<Product> byRatingDesc =
                Comparator.comparingDouble((Product p) -> p.rating)
                          .reversed()
                          .thenComparing(p -> p.name);
        Product[] byRating = Arrays.copyOf(products, products.length);
        insertionSort(byRating, byRatingDesc);
        printAll("평점 내림차순, 동점 시 이름순 — Comparator 사용", byRating);

        // 3) 같은 정렬 메서드에 또 다른 기준: 이름 오름차순
        Product[] byName = Arrays.copyOf(products, products.length);
        insertionSort(byName, Comparator.comparing(p -> p.name));
        printAll("이름 오름차순 — Comparator 사용", byName);

        System.out.println("정렬 코드는 한 줄도 바꾸지 않았습니다. 바뀐 것은 Comparator뿐입니다.");
    }
}
