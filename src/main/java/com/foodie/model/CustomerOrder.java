package com.foodie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="customer_order")
public class CustomerOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long orderId;
    @ManyToOne(optional=false) @JoinColumn(name="customer_id") private Customer customer;
    @ManyToOne(optional=false) @JoinColumn(name="restaurant_id") private Restaurant restaurant;
    @Column(nullable=false) private LocalDateTime orderDate;
    @Column(nullable=false, precision=10, scale=2) private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30) private OrderStatus status;
    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true) private List<OrderItem> items=new ArrayList<>();
    public void addItem(OrderItem item){items.add(item);item.setOrder(this);}
    public Long getOrderId(){return orderId;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;}
    public Restaurant getRestaurant(){return restaurant;} public void setRestaurant(Restaurant v){restaurant=v;}
    public LocalDateTime getOrderDate(){return orderDate;} public void setOrderDate(LocalDateTime v){orderDate=v;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;}
    public OrderStatus getStatus(){return status;} public void setStatus(OrderStatus v){status=v;}
    public List<OrderItem> getItems(){return items;}
}
