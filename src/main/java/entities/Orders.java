package entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Orders implements FormatObj {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Column (nullable = false)
    private String status;
    @Column(name = "local_date",  columnDefinition = "DATE")
    private LocalDate date;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderDetails> orderDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;


    public Orders() {
    }

    public Orders(String status, LocalDate date) {
        this.status = status;
        this.date = date;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDateTime() {
        return date;
    }

    public void setDateTime(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<OrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void addPosition (OrderDetails orderDetails){
        this.orderDetails.add(orderDetails);
        orderDetails.setOrder(this);
    }



    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", dateTime=" + date +
                ", client=" + client +
                ", orderDetails.size=" + orderDetails.size() +
                '}';
    }

    public String getFormattedObject(){
        return String.format("%-10d | %-10d | %-15s | %-15s | %-5d",
                orderId, client.getClientId(),status,date, orderDetails.size());
    }
    public String getHeader(){
        return String.format("%-10s | %-10s | %-15s | %-15s |%-5s",
                "order_id","client_id","status","date", "position_amount");
    }
}
