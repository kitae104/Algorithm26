import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step4CategoryStats {

    /** 상품 1개의 정보를 담는 데이터 클래스 (Step1과 같은 구조) */
    static class Product {
        int code;
        String name;
        String category;
        int price;
        int stock;

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

    public static void main(String[] args) {
        List<Product> products = loadProducts();

        // HashMap(3강): 카테고리 이름 → 개수 / 재고 자산(가격 × 재고의 합)
        Map<String, Integer> countByCategory = new HashMap<>();
        Map<String, Long> valueByCategory = new HashMap<>();
        // HashMap은 순서를 보장하지 않으므로, 처음 등장한 순서를 따로 기억해 출력 순서를 고정한다
        List<String> categoryOrder = new ArrayList<>();

        for (Product p : products) {
            if (!countByCategory.containsKey(p.category)) {
                categoryOrder.add(p.category);          // 처음 보는 카테고리 등록
                countByCategory.put(p.category, 0);
                valueByCategory.put(p.category, 0L);
            }
            countByCategory.put(p.category, countByCategory.get(p.category) + 1);
            valueByCategory.put(p.category,
                    valueByCategory.get(p.category) + (long) p.price * p.stock);
        }

        System.out.println("[카테고리별 집계] — 배열 순회(2강) + HashMap(3강)");
        long totalValue = 0;
        for (String category : categoryOrder) {
            long value = valueByCategory.get(category);
            totalValue += value;
            System.out.println("  " + category + " : 상품 " + countByCategory.get(category)
                    + "종 | 재고 자산 " + value + "원");
        }
        System.out.println("  전체 재고 자산: " + totalValue + "원");

        System.out.println();

        // 조건 검색(2강): 재고가 5개 미만인 상품 = 발주가 필요한 상품
        System.out.println("[재고 부족 경고] — 재고 5개 미만");
        int warningCount = 0;
        for (Product p : products) {
            if (p.stock < 5) {
                warningCount++;
                System.out.println("  " + p.summary());
            }
        }
        System.out.println("  경고 대상: " + warningCount + "종");
    }
}
