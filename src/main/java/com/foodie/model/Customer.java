package com.foodie.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Customer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long customerId;
    @Column(nullable=false, length=150) private String name;
    @Column(nullable=false, unique=true) private String email;
    @Column(nullable=false, length=20) private String phone;
    @Column(nullable=false) private String address;
    @JsonIgnore @Column(nullable=false, name="password_hash") private String passwordHash;
    public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
}
