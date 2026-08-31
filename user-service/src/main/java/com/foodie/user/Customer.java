package com.foodie.user;
import jakarta.persistence.*;
@Entity public class Customer{@Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long customerId;@Column(nullable=false,length=150) String name;@Column(nullable=false,unique=true) String email;@Column(nullable=false,length=20) String phone;@Column(nullable=false) String address;@Column(nullable=false,name="password_hash") String passwordHash;}
