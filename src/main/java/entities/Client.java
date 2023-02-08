package entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity

public class Client implements FormatObj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String phone;
    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST)
    private List<Orders> ordersList = new ArrayList<>();

    public Client(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public Client() {
    }


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long id) {
        this.clientId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    @Override
    public String toString() {
        return "Entities.Client{" +
                "id=" + clientId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", ordersList.size=" + ordersList.size() +
                '}';
    }

    public void addOrder(Orders orders) {
        ordersList.add(orders);
        orders.setClient(this);
    }

    public String getFormattedObject() {
        return String.format("%-6d | %-25s | %-40s | %-16s | %-5d",
                clientId, name, address, phone, ordersList.size());
    }

    public String getHeader() {
        return String.format("%-6s | %-25s | %-40s | %-16s | %-5s",
                "id", "name", "address", "phone", "orders");
    }

}
