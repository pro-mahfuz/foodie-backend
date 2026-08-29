package com.foodie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long restaurantId;
    @NotBlank @Column(nullable=false, length=150) private String name;
    @NotBlank @Column(nullable=false) private String address;
    @NotBlank @Column(nullable=false, length=20) private String phone;
    @DecimalMin("0.0") @DecimalMax("5.0") @Column(precision=2, scale=1) private BigDecimal rating;
    public Long getRestaurantId(){return restaurantId;} public void setRestaurantId(Long v){restaurantId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public BigDecimal getRating(){return rating;} public void setRating(BigDecimal v){rating=v;}
}
