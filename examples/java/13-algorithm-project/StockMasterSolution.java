import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 최종 프로그램 문제 — 편의점 재고 관리 시스템 "StockMaster" 정답 코드.
 * 파이프라인: 데이터 로드 → 코드 순 정렬(4강) → 검색 요청 처리(5강 + 예외 방어)
 *            → 카테고리 집계(3강) → 재고 경고(2강)
 */
public class StockMasterSolution {

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

    /** 편의점 상품 8종 */
    static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(5501, "블루투스 스피커", "전자", 52000, 6));
        products.add(new Product(1203, "게이밍 마우스", "전자", 35000, 2));
        products.add(new Product(7010, "보온 도시락", "생활", 21000, 15));
        products.add(new Product(3305, "무선 충전기", "전자", 28000, 9));
        products.add(new Product(2101, "여행용 파우치", "잡화", 13000, 4));
        products.add(new Product(8804, "스테인리스 텀블러", "생활", 16000, 22));
        products.add(new Product(4409, "휴대용 선풍기", "생활", 19000, 3));
        products.add(new Product(6607, "가죽 키링", "잡화", 8000, 11));
        return products;
    }

    /** [요구사항 1] 삽입 정렬 — 기준은 Comparator로 받는다 (4강) */
    static void insertionSort(List<Product> list, Comparator<Product> comparator) {
        for (int i = 1; i < list.size(); i++) {
            Product key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));   // key보다 뒤에 와야 하는 원소를 뒤로 민다
                j--;
            }
            list.set(j + 1, key);
        }
    }

    /** [요구사항 2] 이진 탐색 — 찾으면 Product, 없으면 null (5강) */
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
                low = mid + 1;    // 왼쪽 절반 버리기
            } else {
                high = mid - 1;   // 오른쪽 절반 버리기
            }
        }
        return null;
    }

    /** [요구사항 3] 검색 요청 1건 처리 — 잘못된 입력에도 멈추지 않는다 */
    static void handleSearchRequest(List<Product> sorted, String rawInput) {
        System.out.println("검색 요청 \"" + rawInput + "\"");
        try {
            int code = Integer.parseInt(rawInput.trim());
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
            System.out.println("  → 오류: \"" + rawInput + "\"은(는) 숫자가 아닙니다.");
        }
    }

    /** [요구사항 4] 카테고리별 상품 수와 재고 자산(가격 × 재고 합계) 집계 (3강) */
    static void printCategoryReport(List<Product> products) {
        Map<String, Integer> countByCategory = new HashMap<>();
        Map<String, Long> valueByCategory = new HashMap<>();
        List<String> categoryOrder = new ArrayList<>();   // 처음 등장한 순서 = 출력 순서

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
    }

    /** [요구사항 5] 재고 부족 경고 — 재고 5개 미만 상품 출력 (2강) */
    static void printLowStockWarning(List<Product> products) {
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
        List<Product> products = loadProducts();

        System.out.println("== 1. 상품 " + products.size() + "종 로드 → 코드 순 정렬 ==");
        insertionSort(products, (a, b) -> a.code - b.code);
        for (Product p : products) {
            System.out.println("  " + p.summary());
        }

        System.out.println();
        System.out.println("== 2. 검색 요청 처리 (이진 탐색 + 예외 처리) ==");
        String[] requests = {"5501", "1203", "9999", "12O3", "-100"};
        for (String request : requests) {
            handleSearchRequest(products, request);
        }

        System.out.println();
        printCategoryReport(products);

        System.out.println();
        printLowStockWarning(products);
    }
}
