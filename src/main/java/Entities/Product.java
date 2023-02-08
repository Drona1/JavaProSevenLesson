package Entities;

import javax.persistence.*;
import java.util.*;

@Entity

public class Product implements FormatObj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private String unitName;
    @Column(nullable = false)
    private Double unitPrice;
    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private List<OrderDetails> orderDetails = new ArrayList<>();


    public Product(String productName, String unitName, Double unitPrice) {
        this.productName = productName;
        this.unitName = unitName;
        this.unitPrice = unitPrice;
    }

    public Product() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long id) {
        this.productId = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<OrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unitName='" + unitName + '\'' +
                ", unitPrice=" + unitPrice +
                ", orderDetails.size=" + orderDetails.size() +
                '}';
    }


    public void addPosition(OrderDetails orderDetails) {
        this.orderDetails.add(orderDetails);
        orderDetails.setProduct(this);
    }

    public String getFormattedObject() {
        return String.format("%-6d | %-25s | %-10s | %-15.2f | %-5d",
                productId, productName, unitName, unitPrice, orderDetails.size());
    }

    public String getHeader() {
        return String.format("%-6s | %-25s | %-10s | %-15s | %-5s",
                "id", "product_name", "unit_name", "unit_price", "orders");
    }
}
