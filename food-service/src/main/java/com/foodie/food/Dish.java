package com.foodie.food;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
class Dish {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long dishId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "restaurant_id")
	Restaurant restaurant;
	@Column(nullable = false, length = 150)
	String name;
	@Column(columnDefinition = "TEXT")
	String description;
	@Column(nullable = false, precision = 10, scale = 2)
	BigDecimal price;
	@Column(nullable = false, length = 100)
	String category;
}
