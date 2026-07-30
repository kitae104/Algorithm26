import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 콘솔 상품 관리·분석 프로그램 — 완성판.
 * Step1(데이터 모델) + Step2(정렬) + Step3(이진 탐색) + Step4(집계)에
 * "잘못된 입력 방어(예외 처리)"를 더해 하나의 프로그램으로 통합한다.
 *
 * 처리 파이프라인: 데이터 로드 → 코드 순 정렬 → 검색 요청 처리 → 카테고리 집계 → 재고 경고
 */
public class ProductManagerComplete {

    /** 상품 1개의 정보를 담는 데이터 클래스 */
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

    /** 입고 순서 그대로의 상품 8개 */
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

    /** 삽입 정렬(4강) — Comparator로 기준을 갈아 끼운다 */
    static void insertionSort(List<Product> list, Comparator<Product> comparator) {
        for (int i = 1; i < list.size(); i++) {
            Product key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    /** 이진 탐색(5강) — 코드 오름차순 정렬이 전제 조건 */
    static Product binarySearchByCode(List<Product> sorted, int targetCode) {
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int midCode = sorted.get(mid).code;
            if (midCode == targetCode) {
                return sorted.get(mid);
            }
            if (midCode < targetCode) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    /**
     * 검색 요청 1건 처리 — 잘못된 입력을 예외 처리로 방어한다.
     * 사용자가 입력한 문자열은 숫자가 아닐 수도, 음수일 수도, 없는 코드일 수도 있다.
     */
    static void handleSearchRequest(List<Product> sorted, String rawInput) {
        System.out.println("검색 요청 \"" + rawInput + "\"");
        try {
            int code = Integer.parseInt(rawInput.trim()); // 숫자가 아니면 NumberFormatException
            if (code <= 0) {
                System.out.println("  → 오류: 상품 코드는 1 이상의 정수여야 합니다.");
                return;
            }
            Product found = binarySearchByCode(sorted, code);
            if (found != null) {
                System.out.println("  → " + found.summary());
            } else {
                System.out.println("  → 코드 " + code + " 상품 없음 (미등록 코드)");
            }
        } catch (NumberFormatException e) {
            // 프로그램을 멈추지 않고, 문제를 알린 뒤 다음 요청으로 넘어간다
            System.out.println("  → 오류: \"" + rawInput + "\"은(는) 숫자가 아닙니다.");
        }
    }

    /** 카테고리별 집계(3강 HashMap) + 재고 경고(2강 조건 검색) */
    static void printReport(List<Product> products) {
        Map<String, Integer> countByCategory = new HashMap<>();
        Map<String, Long> valueByCategory = new HashMap<>();
        List<String> categoryOrder = new ArrayList<>(); // 출력 순서 고정용

        for (Product p : products) {
            if (!countByCategory.containsKey(p.category)) {
                categoryOrder.add(p.category);
                countByCategory.put(p.category, 0);
                valueByCategory.put(p.category, 0L);
            }
            countByCategory.put(p.category, countByCategory.get(p.category) + 1);
            valueByCategory.put(p.category,
                    valueByCategory.get(p.category) + (long) p.price * p.stock);
        }

        System.out.println("== 3. 카테고리별 집계 ==");
        long totalValue = 0;
        for (String category : categoryOrder) {
            long value = valueByCategory.get(category);
            totalValue += value;
            System.out.println("  " + category + " : 상품 " + countByCategory.get(category)
                    + "종 | 재고 자산 " + value + "원");
        }
        System.out.println("  전체 재고 자산: " + totalValue + "원");

        System.out.println();
        System.out.println("== 4. 재고 부족 경고 (5개 미만) ==");
        int warningCount = 0;
        for (Product p : products) {
            if (p.stock < 5) {
                warningCount++;
                System.out.println("  " + p.summary());
            }
        }
        System.out.println("  경고 대상: " + warningCount + "종");
    }

    public static void main(String[] args) {
        // 1) 데이터 로드 (Step1)
        List<Product> products = loadProducts();
        System.out.println("== 1. 상품 " + products.size() + "종 로드 → 코드 순 정렬 ==");

        // 2) 코드 오름차순 정렬 (Step2) — 이후 모든 검색의 기반
        insertionSort(products, (a, b) -> a.code - b.code);
        for (Product p : products) {
            System.out.println("  " + p.summary());
        }

        System.out.println();
        System.out.println("== 2. 검색 요청 처리 (이진 탐색 + 예외 처리) ==");
        // 실제 서비스라면 Scanner로 받을 입력 — 여기서는 시나리오로 고정한다
        String[] requests = {"2005", "3010", "1500", "20A5", "-7"};
        for (String request : requests) {
            handleSearchRequest(products, request);
        }

        System.out.println();
        // 3) + 4) 집계와 경고 (Step4)
        printReport(products);
    }
}
