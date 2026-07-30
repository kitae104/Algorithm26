import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 처리 과정 추적 — 완성 프로그램의 핵심 두 단계(정렬, 이진 탐색)가
 * 내부에서 어떻게 움직이는지 회차별 상태를 출력한다.
 * 시각화(8번 섹션)와 똑같이 동작하는지 눈으로 확인하는 것이 목적이다.
 */
public class ProductManagerTrace {

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

    /** 리스트의 상품 코드만 한 줄로 만든다 (추적 출력용) */
    static String codeLine(List<Product> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i).code);
        }
        return sb.append("]").toString();
    }

    /** 삽입 정렬 — 회차(i)마다 밀어낸 칸 수와 배열 상태를 출력한다 */
    static void insertionSortTrace(List<Product> list, Comparator<Product> comparator) {
        System.out.println("[1] 삽입 정렬 추적 (기준: 코드 오름차순)");
        System.out.println("  시작       " + codeLine(list));
        for (int i = 1; i < list.size(); i++) {
            Product key = list.get(i);
            int j = i - 1;
            int shiftCount = 0;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
                shiftCount++;
            }
            list.set(j + 1, key);
            System.out.println("  i=" + i + " key=" + key.code
                    + " → " + shiftCount + "칸 밀고 " + (j + 1) + "번 자리에 삽입 "
                    + codeLine(list));
        }
    }

    /** 이진 탐색 — 회차마다 low/mid/high와 판단 결과를 출력한다 */
    static void binarySearchTrace(List<Product> sorted, int targetCode) {
        System.out.println("[2] 이진 탐색 추적 (목표: 코드 " + targetCode + ")");
        int low = 0;
        int high = sorted.size() - 1;
        int round = 0;
        while (low <= high) {
            round++;
            int mid = (low + high) / 2;
            int midCode = sorted.get(mid).code;
            System.out.print("  " + round + "회차: low=" + low + " high=" + high
                    + " mid=" + mid + " → 코드 " + midCode);
            if (midCode == targetCode) {
                System.out.println(" == " + targetCode + " → 발견! ("
                        + sorted.get(mid).name + ", 비교 " + round + "번)");
                return;
            }
            if (midCode < targetCode) {
                System.out.println(" < " + targetCode + " → 왼쪽 절반 버림 (low=" + (mid + 1) + ")");
                low = mid + 1;
            } else {
                System.out.println(" > " + targetCode + " → 오른쪽 절반 버림 (high=" + (mid - 1) + ")");
                high = mid - 1;
            }
        }
        System.out.println("  범위가 사라짐(low > high) → 코드 " + targetCode + " 없음 (비교 " + round + "번)");
    }

    public static void main(String[] args) {
        List<Product> products = loadProducts();

        insertionSortTrace(products, (a, b) -> a.code - b.code);

        System.out.println();
        binarySearchTrace(products, 2005);   // 있는 코드

        System.out.println();
        binarySearchTrace(products, 1500);   // 없는 코드
    }
}
