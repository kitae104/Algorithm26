/** 상품 1개의 정보를 담는 데이터 클래스 */
public class Product {
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
