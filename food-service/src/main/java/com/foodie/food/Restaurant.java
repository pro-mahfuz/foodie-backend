package com.foodie.food;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long restaurantId;
	@Column(nullable = false, length = 150)
	String name;
	@Column(nullable = false)
	String address;
	@Column(nullable = false, length = 20)
	String phone;
	@Column(precision = 2, scale = 1)
	BigDecimal rating;
}
