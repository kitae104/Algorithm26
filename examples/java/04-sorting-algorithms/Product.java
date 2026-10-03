/** 상품: 이름, 가격, 평점. 기본 정렬 기준(가격 오름차순)을 Comparable로 정의한다. */
public class Product implements Comparable<Product> {
    String name;
    int price;      // 원
    double rating;  // 0.0 ~ 5.0

    Product(String name, int price, double rating) {
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    /** 기본 정렬 기준: 가격 오름차순. 음수=this가 앞, 0=같음, 양수=this가 뒤. */
    @Override
    public int compareTo(Product other) {
        return Integer.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return name + " (" + price + "원, 평점 " + rating + ")";
    }
}
