package com.foodie.service;

import com.foodie.dto.ApiDtos.*;
import com.foodie.error.*;
import com.foodie.model.Dish;
import com.foodie.repository.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {
	private final DishRepository dishes;
	private final CustomerRepository customers;
	private final Map<Long, Map<Long, Integer>> carts = new ConcurrentHashMap<>();

	public CartService(DishRepository d, CustomerRepository c) {
		dishes = d;
		customers = c;
	}

	public CartView add(Long customerId, CartItemRequest request) {
		ensureCustomer(customerId);
		Dish dish = dishes.findById(request.dishId()).orElseThrow(() -> new NotFoundException("Dish not found"));
		Map<Long, Integer> cart = carts.computeIfAbsent(customerId, k -> new ConcurrentHashMap<>());
		if (!cart.isEmpty()) {
			Dish existing = dishes.findById(cart.keySet().iterator().next()).orElseThrow();
			if (!existing.getRestaurant().getRestaurantId().equals(dish.getRestaurant().getRestaurantId()))
				throw new BadRequestException("A cart may contain dishes from one restaurant only");
		}
		cart.merge(dish.getDishId(), request.quantity(), Integer::sum);
		return get(customerId);
	}

	public CartView get(Long customerId) {
		ensureCustomer(customerId);
		Map<Long, Integer> cart = carts.getOrDefault(customerId, Map.of());
		List<CartLine> lines = cart.entrySet().stream().map(e -> {
			Dish d = dishes.findById(e.getKey()).orElseThrow(() -> new NotFoundException("Dish not found"));
			BigDecimal sub = d.getPrice().multiply(BigDecimal.valueOf(e.getValue()));
			return new CartLine(d.getDishId(), d.getName(), e.getValue(), d.getPrice(), sub);
		}).toList();
		return new CartView(customerId, lines,
				lines.stream().map(CartLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
	}

	public void clear(Long customerId) {
		ensureCustomer(customerId);
		carts.remove(customerId);
	}

	private void ensureCustomer(Long id) {
		if (!customers.existsById(id))
			throw new NotFoundException("Customer not found");
	}
}
