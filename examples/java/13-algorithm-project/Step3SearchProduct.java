import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Step3SearchProduct {

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

    /** 삽입 정렬(4강) — Step2와 같은 코드 */
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

    /** 마지막 탐색의 비교 횟수 (1강의 연산 카운터 기법) */
    static int compareCount = 0;

    /**
     * 이진 탐색(5강) — 코드 오름차순으로 정렬된 리스트에서만 동작한다!
     * 찾으면 해당 Product, 없으면 null을 반환한다.
     */
    static Product binarySearchByCode(List<Product> sorted, int targetCode) {
        compareCount = 0;
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;      // 남은 범위의 가운데
            compareCount++;
            int midCode = sorted.get(mid).code;
            if (midCode == targetCode) {
                return sorted.get(mid);      // 발견!
            }
            if (midCode < targetCode) {
                low = mid + 1;               // 왼쪽 절반 버리기
            } else {
                high = mid - 1;              // 오른쪽 절반 버리기
            }
        }
        return null;                          // 범위가 사라짐 = 없음
    }

    public static void main(String[] args) {
        List<Product> products = loadProducts();

        // 이진 탐색의 전제 조건: 반드시 먼저 코드 순으로 정렬한다 (Step2의 도구 재사용)
        insertionSort(products, (a, b) -> a.code - b.code);

        int[] targets = {2005, 3010, 1500};
        for (int code : targets) {
            Product found = binarySearchByCode(products, code);
            if (found != null) {
                System.out.println("코드 " + code + " 검색: " + found.summary()
                        + "  (비교 " + compareCount + "번)");
            } else {
                System.out.println("코드 " + code + " 검색: 없음  (비교 " + compareCount + "번)");
            }
        }

        System.out.println();
        System.out.println("상품 8개에서 최대 비교 4번 — 순차 탐색(최악 8번)의 절반 이하다.");
        System.out.println("상품이 1,000개라면? 이진 탐색 최대 10번 vs 순차 탐색 최대 1,000번.");
    }
}
