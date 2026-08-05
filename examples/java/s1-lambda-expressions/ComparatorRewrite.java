import java.util.Arrays;
import java.util.Comparator;

/**
 * 4강 ProductSortApplication의 정렬 기준을 익명 클래스 → 람다식 → 조립 메서드로
 * 세 단계에 걸쳐 바꿔 본다. 정렬 알고리즘(insertionSort)은 한 줄도 바뀌지 않는다.
 *
 * 세 기준으로 정렬한 결과가 모두 같은지 프로그램이 스스로 확인한다.
 */
public class ComparatorRewrite {

    static class Product {
        final String name;
        final int price;
        final double rating;

        Product(String name, int price, double rating) {
            this.name = name;
            this.price = price;
            this.rating = rating;
        }

        String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name + " (" + price + "원, 평점 " + rating + ")";
        }
    }

    /** 4강의 제네릭 삽입 정렬 — 기준만 갈아 끼우면 어떤 순서로도 정렬한다. */
    static <T> void insertionSort(T[] arr, Comparator<? super T> comp) {
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && comp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    static Product[] sortedCopy(Product[] src, Comparator<Product> comp) {
        Product[] copy = Arrays.copyOf(src, src.length);
        insertionSort(copy, comp);
        return copy;
    }

    static void print(String title, Product[] items) {
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

        /* ── 1단계: 익명 클래스 (람다 이전의 방식) ───────────────── */
        Comparator<Product> step1 = new Comparator<Product>() {
            @Override
            public int compare(Product a, Product b) {
                int byRating = Double.compare(b.rating, a.rating);   // 내림차순이라 b가 앞
                if (byRating != 0) {
                    return byRating;
                }
                return a.name.compareTo(b.name);                     // 동점이면 이름순
            }
        };

        /* ── 2단계: 람다식 — 몸통은 그대로, 껍데기만 걷어냈다 ────── */
        Comparator<Product> step2 = (a, b) -> {
            int byRating = Double.compare(b.rating, a.rating);
            if (byRating != 0) {
                return byRating;
            }
            return a.name.compareTo(b.name);
        };

        /* ── 3단계: Comparator의 조립 메서드 ─────────────────────
           "평점으로 비교 → 뒤집기 → 동점이면 이름으로"가 순서대로 읽힌다.
           부호를 머릿속에서 따져 볼 필요가 없어진다.
           comparingDouble에 타입을 적은 이유: 이 자리에는 p의 타입을 추론할
           근거가 없다. 뒤의 thenComparing은 앞에서 정해졌으므로 생략된다. */
        Comparator<Product> step3 =
                Comparator.comparingDouble((Product p) -> p.rating)
                          .reversed()
                          .thenComparing(p -> p.name);

        Product[] r1 = sortedCopy(products, step1);
        Product[] r2 = sortedCopy(products, step2);
        Product[] r3 = sortedCopy(products, step3);

        print("평점 내림차순, 동점 시 이름순", r3);

        /* 1강에서 배운 습관: 표현을 바꿨으면 결과가 같은지 검증한다. */
        System.out.println("익명 클래스 == 람다식 : " + Arrays.equals(r1, r2));
        System.out.println("람다식 == 조립 메서드 : " + Arrays.equals(r2, r3));
        System.out.println();

        /* 기준을 값으로 다루면 여러 기준을 나란히 놓기 쉬워진다. */
        print("가격 오름차순", sortedCopy(products, Comparator.comparingInt(p -> p.price)));
        print("이름 오름차순 (메서드 참조)", sortedCopy(products, Comparator.comparing(Product::getName)));

        System.out.println("insertionSort는 세 번 모두 같은 코드입니다. 바뀐 것은 Comparator뿐입니다.");
    }
}
