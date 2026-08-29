package com.foodie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="order_item", uniqueConstraints=@UniqueConstraint(columnNames={"order_id","dish_id"}))
public class OrderItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long orderItemId;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="order_id") private CustomerOrder order;
    @ManyToOne(optional=false) @JoinColumn(name="dish_id") private Dish dish;
    @Column(nullable=false) private int quantity;
    @Column(nullable=false, precision=10, scale=2) private BigDecimal price;
    public Long getOrderItemId(){return orderItemId;} public CustomerOrder getOrder(){return order;} public void setOrder(CustomerOrder v){order=v;}
    public Dish getDish(){return dish;} public void setDish(Dish v){dish=v;}
    public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
}
