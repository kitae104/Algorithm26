import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Step2SortProducts {

    /** 상품 1개의 정보를 담는 데이터 클래스 (Step1과 같은 구조) */
    static class Product {
        int code;        // 상품 코드 (고유한 값 — 검색의 기준)
        String name;     // 상품 이름
        String category; // 카테고리 (분류의 기준)
        int price;       // 가격(원)
        int stock;       // 재고 수량 (경고의 기준)

        Product(int code, String name, String category, int price, int stock) {
            this.code = code;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        String summary() {
            return "[" + code + "] " + name + " | " + category
                    + " | " + price + "원 | 재고 " + stock + "개";
        }
    }

    /** 입고 순서 그대로의 상품 8개 (Step1과 같은 데이터) */
    static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(2005, "유선 키보드", "전자", 23000, 12));
        products.add(new Product(1003, "무선 마우스", "전자", 18000, 25));
        products.add(new Product(3010, "머그컵", "생활", 7000, 2));
        products.add(new Product(1007, "마우스 패드", "잡화", 4000, 18));
        products.add(new Product(2002, "USB 메모리", "전자", 9000, 5));
        products.add(new Product(3001, "텀블러", "생활", 12000, 30));
        products.add(new Product(1010, "노트북 파우치", "잡화", 15000, 3));
        products.add(new Product(2008, "웹캠", "전자", 45000, 4));
        return products;
    }

    /**
     * 삽입 정렬(4강) — 정렬 기준을 Comparator로 받아서
     * "코드 순", "가격 순" 등 어떤 기준으로도 같은 코드를 재사용한다.
     */
    static void insertionSort(List<Product> list, Comparator<Product> comparator) {
        for (int i = 1; i < list.size(); i++) {
            Product key = list.get(i);   // 이번에 제자리를 찾아줄 상품
            int j = i - 1;
            // key보다 "뒤에 와야 하는" 상품을 한 칸씩 뒤로 민다
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);        // 비게 된 자리에 key를 넣는다
        }
    }

    public static void main(String[] args) {
        List<Product> products = loadProducts();

        // 기준 1: 상품 코드 오름차순 — 이진 탐색(5강)을 쓰기 위한 준비
        insertionSort(products, (a, b) -> a.code - b.code);
        System.out.println("[정렬 1] 상품 코드 오름차순 — 이진 탐색 준비 완료");
        for (Product p : products) {
            System.out.println("  " + p.summary());
        }

        System.out.println();

        // 기준 2: 가격 내림차순 — Comparator만 바꾸면 정렬 코드는 그대로!
        insertionSort(products, (a, b) -> b.price - a.price);
        System.out.println("[정렬 2] 가격 내림차순 — 비싼 상품부터 보고서");
        for (Product p : products) {
            System.out.println("  " + p.summary());
        }
    }
}
