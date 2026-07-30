import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 최종 프로그램 문제 — 편의점 재고 관리 시스템 "StockMaster" 학생 시작 코드.
 * TODO를 위에서부터 순서대로 채우면 완성된다. (TODO 상태 그대로도 컴파일된다)
 *
 * 파이프라인: 데이터 로드 → 코드 순 정렬 → 검색 요청 처리(예외 방어) → 집계 → 재고 경고
 */
public class StockMasterStarter {

    /** 상품 1개의 정보를 담는 데이터 클래스 (완성되어 있음 — 수정 불필요) */
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

    /** 편의점 상품 8종 (완성되어 있음 — 수정 불필요) */
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
        // TODO 1: i를 1부터 끝까지 진행하며,
        //         list.get(i)를 key로 꺼내고
        //         key보다 뒤에 와야 하는 원소(comparator.compare(...) > 0)를
        //         한 칸씩 뒤로 민 뒤 key를 삽입하세요. (Step2 참고)
    }

    /** [요구사항 2] 이진 탐색 — 찾으면 Product, 없으면 null (5강) */
    static Product binarySearchByCode(List<Product> sorted, int targetCode) {
        // TODO 2: low=0, high=size-1에서 시작해
        //         mid의 코드와 targetCode를 비교하며 절반씩 버리세요. (Step3 참고)
        return null;   // 스텁: 아직 항상 "없음"을 반환한다
    }

    /** [요구사항 3] 검색 요청 1건 처리 — 잘못된 입력에도 멈추지 않아야 한다 */
    static void handleSearchRequest(List<Product> sorted, String rawInput) {
        System.out.println("검색 요청 \"" + rawInput + "\"");
        // TODO 3: try-catch로 Integer.parseInt를 감싸고,
        //         (1) 숫자가 아니면 "숫자가 아닙니다" 오류 출력
        //         (2) 0 이하이면 "1 이상의 정수여야 합니다" 오류 출력
        //         (3) 정상 코드면 binarySearchByCode 결과를 출력하세요.
        System.out.println("  → (TODO: 아직 처리 로직이 없습니다)");
    }

    /** [요구사항 4] 카테고리별 상품 수와 재고 자산(가격 × 재고 합계) 집계 (3강) */
    static void printCategoryReport(List<Product> products) {
        System.out.println("== 3. 카테고리별 집계 ==");
        // TODO 4: HashMap<String, Integer>(개수)와 HashMap<String, Long>(재고 자산),
        //         그리고 처음 등장한 순서를 기억할 List<String>을 사용해
        //         카테고리별 집계와 전체 재고 자산을 출력하세요. (Step4 참고)
    }

    /** [요구사항 5] 재고 부족 경고 — 재고 5개 미만 상품 출력 (2강) */
    static void printLowStockWarning(List<Product> products) {
        System.out.println("== 4. 재고 부족 경고 (5개 미만) ==");
        // TODO 5: 리스트를 순회하며 stock < 5인 상품을 출력하고,
        //         마지막에 "경고 대상: N종"을 출력하세요.
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
