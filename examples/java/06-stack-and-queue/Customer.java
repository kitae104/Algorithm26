/** 은행 고객: 이름, 접수 번호, VIP 여부 */
public class Customer {
    String name;
    int ticketNo;
    boolean vip;

    Customer(String name, int ticketNo, boolean vip) {
        this.name = name;
        this.ticketNo = ticketNo;
        this.vip = vip;
    }

    @Override
    public String toString() {
        return name + "(" + ticketNo + "번" + (vip ? ", VIP" : "") + ")";
    }
}
