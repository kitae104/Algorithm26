import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 13강 「람다·스트림 변경」.
 *
 * ProductManagerComplete.java(실습 코드)를 같은 결과가 나오도록 람다와 스트림으로 다시 쓴 것이다.
 */
public class ModernizeSolution {

    // Product 클래스는 같은 폴더의 Product.java에 정의되어 있다.

    /** 입고 순서 그대로의 상품 8개 */
    static List<Product> loadProducts() {
        return List.of(
                new Product(2005, "유선 키보드", "전자", 23000, 12),
                new Product(1003, "무선 마우스", "전자", 18000, 25),
                new Product(3010, "머그컵", "생활", 7000, 2),
                new Product(1007, "마우스 패드", "잡화", 4000, 18),
                new Product(2002, "USB 메모리", "전자", 9000, 5),
                new Product(3001, "텀블러", "생활", 12000, 30),
                new Product(1010, "노트북 파우치", "잡화", 15000, 3),
                new Product(2008, "웹캠", "전자", 45000, 4));
    }

    /** 이진 탐색(5강) — low·high를 직접 옮기는 알고리즘이라 반복문 그대로 둔다 */
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

    /** 검색 요청 1건 처리 — 잘못된 입력을 예외 처리로 방어한다 */
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

    /** 재고 자산 = 가격 × 재고의 합 (long으로 누적) */
    static long assetValue(List<Product> products) {
        return products.stream()
                .map(p -> (long) p.price * p.stock)
                .reduce(0L, (acc, v) -> acc + v);
    }

    /** 카테고리별 집계 + 재고 경고 */
    static void printReport(List<Product> products) {
        Map<String, List<Product>> byCategory = products.stream()
                .collect(Collectors.groupingBy(p -> p.category));
        List<String> categoryOrder = products.stream()   // 출력 순서 고정용
                .map(p -> p.category)
                .distinct()
                .toList();

        System.out.println("== 3. 카테고리별 집계 ==");
        categoryOrder.forEach(category -> {
            List<Product> group = byCategory.get(category);
            System.out.println("  " + category + " : 상품 " + group.size()
                    + "종 | 재고 자산 " + assetValue(group) + "원");
        });
        System.out.println("  전체 재고 자산: " + assetValue(products) + "원");

        System.out.println();
        System.out.println("== 4. 재고 부족 경고 (5개 미만) ==");
        List<Product> lowStock = products.stream()
                .filter(p -> p.stock < 5)
                .toList();
        lowStock.forEach(p -> System.out.println("  " + p.summary()));
        System.out.println("  경고 대상: " + lowStock.size() + "종");
    }

    public static void main(String[] args) {
        List<Product> products = loadProducts();
        System.out.println("== 1. 상품 " + products.size() + "종 로드 → 코드 순 정렬 ==");

        List<Product> sorted = products.stream()
                .sorted(Comparator.comparingInt(p -> p.code))
                .toList();
        sorted.forEach(p -> System.out.println("  " + p.summary()));

        System.out.println();
        System.out.println("== 2. 검색 요청 처리 (이진 탐색 + 예외 처리) ==");
        String[] requests = {"2005", "3010", "1500", "20A5", "-7"};
        Arrays.stream(requests).forEach(request -> handleSearchRequest(sorted, request));

        System.out.println();
        printReport(sorted);
    }
}
