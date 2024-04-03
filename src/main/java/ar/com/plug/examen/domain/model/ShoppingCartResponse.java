package ar.com.plug.examen.domain.model;

import ar.com.plug.examen.domain.util.CartStatusEnum;

public class ShoppingCartResponse {
    private long order_id;
    private int dni;
    private double totalAmount;
    private CartStatusEnum status;

    public long getOrderId() {
        return order_id;
    }

    public void setOrderId(long orderId) {
        this.order_id = orderId;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public CartStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CartStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
                "order_id=" + order_id +
                ", dni=" + dni +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}
