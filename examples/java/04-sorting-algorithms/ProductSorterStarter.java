import java.util.Comparator;

public class ProductSorterStarter {

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
        // TODO 1: 가격이 싼 상품이 앞에 오도록 비교 결과를 반환하세요.
        //         (힌트: Integer.compare(a.price, b.price))
        return 0;
    };

    /** 기준 2: 평점 내림차순 */
    static final Comparator<Product> RATING_DESC = (a, b) -> {
        // TODO 2: 평점이 높은 상품이 앞에 오도록 비교 결과를 반환하세요.
        //         (힌트: 인자 순서를 바꾼 Double.compare(b.rating, a.rating))
        return 0;
    };

    /** 기준 3: 평점 내림차순, 평점이 같으면 이름 오름차순 (다중 기준) */
    static final Comparator<Product> RATING_DESC_THEN_NAME = (a, b) -> {
        // TODO 3: 먼저 평점을 내림차순으로 비교하고, 결과가 0(동점)일 때만
        //         a.name.compareTo(b.name)으로 이름을 비교하세요.
        return 0;
    };

    /**
     * 직접 구현하는 삽입 정렬. Arrays.sort는 사용하지 않는다!
     * comp.compare(arr[j], key)가 양수이면 arr[j]는 key보다 뒤로 가야 한다.
     */
    static void insertionSort(Product[] arr, Comparator<Product> comp) {
        // TODO 4: i를 1부터 끝까지 옮기며 key = arr[i]를 꺼내고,
        //         왼쪽의 정렬된 영역에서 key보다 뒤에 와야 할 값들을
        //         한 칸씩 뒤로 민 다음, 빈 자리에 key를 넣으세요.
    }

    static void printAll(String title, Product[] items) {
        System.out.println("[" + title + "]");
        for (int i = 0; i < items.length; i++) {
            System.out.println("  " + (i + 1) + ". " + items[i]);
        }
        System.out.println();
    }

    static Product[] copyOf(Product[] src) {
        Product[] copy = new Product[src.length];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i];
        }
        return copy;
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

        Product[] byPrice = copyOf(products);
        insertionSort(byPrice, PRICE_ASC);
        printAll("1) 가격 오름차순", byPrice);

        Product[] byRating = copyOf(products);
        insertionSort(byRating, RATING_DESC);
        printAll("2) 평점 내림차순 (단일 기준)", byRating);

        Product[] byMulti = copyOf(products);
        insertionSort(byMulti, RATING_DESC_THEN_NAME);
        printAll("3) 평점 내림차순 + 동점 시 이름순 (다중 기준)", byMulti);

        // TODO 5: 1)의 결과에서 가격이 같은 23000원 상품 두 개의 순서가
        //         입력 순서(무선 마우스 → 모니터 받침대)를 유지하는지 확인해 보세요.
        //         (삽입 정렬이 안정 정렬이기 때문입니다)
    }
}
