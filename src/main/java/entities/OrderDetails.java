package entities;


import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class OrderDetails implements FormatObj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer amount;

    private Double discount;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderDetails(Integer amount, Double discount) {
        this.amount = amount;
        this.discount = discount;
    }

    public OrderDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    @Override
    public String toString() {
        return "OrderDetails{" +
                "id=" + id +
                ", amount=" + amount +
                ", discount=" + discount +
                ", orderId=" + order.getOrderId() +
                ", productId=" + product.getProductId() +
                '}';
    }

    public String getFormattedObject() {
        BigDecimal bigDecimal = new BigDecimal("" + amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal("" + product.getUnitPrice()));
        bigDecimal = bigDecimal.multiply(new BigDecimal("" + (100 - discount)));
        bigDecimal = bigDecimal.divide(new BigDecimal("100"), RoundingMode.CEILING);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.CEILING);


        return String.format("%-6d | %-10d | %-10d | %-25s | %-10d | %-10.2f | %-10.2f | %15.2f",
                id, order.getOrderId(), product.getProductId(), product.getProductName(),
                amount, product.getUnitPrice(), discount, bigDecimal);
    }

    public String getHeader() {
        return String.format("%-6s | %-10s | %-10s | %-25s | %-10s | %-10s | %-10s | %15s",
                "id", "order_id", "product_id", "product_name",
                "amount", "unit_price", "discount", "total");
    }
}
