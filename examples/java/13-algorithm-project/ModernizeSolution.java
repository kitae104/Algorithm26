import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 13강 「람다·스트림 수정 문제」 정답.
 *
 * Step4CategoryStats.java의 3단 누적(containsKey → put(0) → put(get + 1))을
 * 그룹핑으로 다시 쓴 것이다.
 *
 * 주의: 원본의 "재고 자산"(가격 x 재고의 long 누적)은 여기서 다루지 않는다.
 * 그 계산에 필요한 long 전용 변환 연산은 추가 정보 문서가 다루지 않으므로,
 * 배우지 않은 문법을 쓰지 않기 위해 반복문 그대로 두는 것이 옳다.
 */
public class ModernizeSolution {

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

    /* ─────────── 이전: Step4CategoryStats의 3단 누적 ─────────── */

    static Map<String, Integer> countByCategoryLoop(List<Product> products) {
        Map<String, Integer> countByCategory = new HashMap<>();
        for (Product p : products) {
            if (!countByCategory.containsKey(p.category)) {
                countByCategory.put(p.category, 0);
            }
            countByCategory.put(p.category, countByCategory.get(p.category) + 1);
        }
        return countByCategory;
    }

    static Map<String, Double> averagePriceLoop(List<Product> products) {
        Map<String, Integer> sum = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();
        for (Product p : products) {
            sum.put(p.category, sum.getOrDefault(p.category, 0) + p.price);
            count.put(p.category, count.getOrDefault(p.category, 0) + 1);
        }
        Map<String, Double> average = new HashMap<>();
        for (String category : sum.keySet()) {
            average.put(category, (double) sum.get(category) / count.get(category));
        }
        return average;
    }

    static List<Product> lowStockLoop(List<Product> products) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.stock < 5) {
                result.add(p);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<Product> products = loadProducts();

        /* ─────────── 문제 ① 카테고리별 상품 수 ─────────── */
        System.out.println("== 문제 ① 카테고리별 상품 수 ==");

        Map<String, Long> countStream = products.stream()
                .collect(Collectors.groupingBy(p -> p.category, Collectors.counting()));

        Map<String, Integer> countLoop = countByCategoryLoop(products);
        System.out.println("  반복문 " + new TreeMap<>(countLoop));
        System.out.println("  스트림 " + new TreeMap<>(countStream));

        boolean sameCount = countLoop.size() == countStream.size();
        for (Map.Entry<String, Integer> e : countLoop.entrySet()) {
            Long fromStream = countStream.get(e.getKey());
            if (fromStream == null || fromStream != e.getValue().longValue()) {
                sameCount = false;
            }
        }
        System.out.println("  같은가 " + sameCount);

        /* ─────────── 문제 ② 카테고리별 평균 가격 ─────────── */
        System.out.println();
        System.out.println("== 문제 ② 카테고리별 평균 가격 ==");

        // 1단계: 카테고리로 묶는다.  2단계: 각 묶음 안에서 다시 스트림을 돌려 평균을 낸다.
        Map<String, List<Product>> grouped = products.stream()
                .collect(Collectors.groupingBy(p -> p.category));

        Map<String, Double> averageStream = new HashMap<>();
        for (Map.Entry<String, List<Product>> entry : grouped.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToInt(p -> p.price)
                    .average()
                    .orElse(0);
            averageStream.put(entry.getKey(), avg);
        }

        Map<String, Double> averageLoopResult = averagePriceLoop(products);
        System.out.println("  반복문 " + new TreeMap<>(averageLoopResult));
        System.out.println("  스트림 " + new TreeMap<>(averageStream));

        boolean sameAverage = averageLoopResult.size() == averageStream.size();
        for (Map.Entry<String, Double> e : averageLoopResult.entrySet()) {
            Double fromStream = averageStream.get(e.getKey());
            if (fromStream == null || Math.abs(fromStream - e.getValue()) > 1e-9) {
                sameAverage = false;
            }
        }
        System.out.println("  같은가 " + sameAverage);

        /* ─────────── 문제 ③ 재고 부족 목록 ─────────── */
        System.out.println();
        System.out.println("== 문제 ③ 재고 5개 미만 ==");

        List<Product> lowStream = products.stream()
                .filter(p -> p.stock < 5)
                .toList();
        long lowCount = products.stream().filter(p -> p.stock < 5).count();

        List<Product> lowLoopResult = lowStockLoop(products);
        System.out.println("  반복문 " + lowLoopResult.size() + "종");
        for (Product p : lowLoopResult) {
            System.out.println("    " + p.summary());
        }
        System.out.println("  스트림 " + lowCount + "종");
        System.out.println("  같은가 " + (lowLoopResult.equals(lowStream)
                && lowLoopResult.size() == lowCount));

        System.out.println();
        System.out.println("집계는 어느 쪽도 O(n)이다. 13강의 성과는 '어떤 자료구조를 골랐는가'이지");
        System.out.println("'어떤 문법으로 적었는가'가 아니다.");
        System.out.println();
        System.out.println("참고: 원본의 '재고 자산'(가격 x 재고의 long 합)은 스트림으로 바꾸지 않았다.");
        System.out.println("      필요한 long 전용 변환 연산을 추가 정보 문서에서 다루지 않기 때문이다.");
        System.out.println("      배우지 않은 문법을 끌어오느니 반복문을 두는 편이 낫다.");
    }
}
