import java.util.ArrayList;
import java.util.List;

public class Step1ProductData {

    // Product 클래스는 같은 폴더의 Product.java에 정의되어 있다.

    public static void main(String[] args) {
        // 입고된 순서 그대로 등록한다 (아직 아무 순서도 없다!)
        List<Product> products = new ArrayList<>();
        products.add(new Product(2005, "유선 키보드", "전자", 23000, 12));
        products.add(new Product(1003, "무선 마우스", "전자", 18000, 25));
        products.add(new Product(3010, "머그컵", "생활", 7000, 2));
        products.add(new Product(1007, "마우스 패드", "잡화", 4000, 18));
        products.add(new Product(2002, "USB 메모리", "전자", 9000, 5));
        products.add(new Product(3001, "텀블러", "생활", 12000, 30));
        products.add(new Product(1010, "노트북 파우치", "잡화", 15000, 3));
        products.add(new Product(2008, "웹캠", "전자", 45000, 4));

        System.out.println("등록된 상품 수: " + products.size());
        for (Product p : products) {
            System.out.println(p.summary());
        }

        System.out.println();
        System.out.println("주의: 아직 코드 순서로 정렬되어 있지 않다.");
        System.out.println("     이 상태로는 이진 탐색(5강)을 쓸 수 없다 — 다음 단계에서 정렬 모듈을 만든다.");
    }
}
