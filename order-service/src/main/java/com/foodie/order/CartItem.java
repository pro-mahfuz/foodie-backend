package com.foodie.order;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "dish_id" }))
class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Column(name = "customer_id", nullable = false)
	Long customerId;
	@Column(name = "dish_id", nullable = false)
	Long dishId;
	@Column(nullable = false)
	Long restaurantId;
	@Column(nullable = false)
	String dishName;
	@Column(nullable = false)
	int quantity;
	@Column(nullable = false, precision = 10, scale = 2)
	BigDecimal unitPrice;
}
