package com.foodie.service;

import com.foodie.dto.ApiDtos.*;
import com.foodie.error.*;
import com.foodie.model.*;
import com.foodie.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class OrderService {
	private final OrderRepository orders;
	private final CustomerRepository customers;
	private final RestaurantRepository restaurants;
	private final DishRepository dishes;

	public OrderService(OrderRepository o, CustomerRepository c, RestaurantRepository r, DishRepository d) {
		orders = o;
		customers = c;
		restaurants = r;
		dishes = d;
	}

	@Transactional
	public OrderView create(CreateOrderRequest request) {
		var uniqueDishIds = new HashSet<Long>();
		if (request.items().stream().map(OrderLineRequest::dishId).anyMatch(id -> !uniqueDishIds.add(id)))
			throw new BadRequestException("An order cannot contain duplicate dishes");
		
		Customer customer = customers.findById(request.customerId())
				.orElseThrow(() -> new NotFoundException("Customer not found"));
		
		Restaurant restaurant = restaurants.findById(request.restaurantId())
				.orElseThrow(() -> new NotFoundException("Restaurant not found"));
		
		CustomerOrder order = new CustomerOrder();
		order.setCustomer(customer);
		order.setRestaurant(restaurant);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.PLACED);
		
		BigDecimal total = BigDecimal.ZERO;
		
		for (OrderLineRequest line : request.items()) {
			Dish dish = dishes.findById(line.dishId())
					.orElseThrow(() -> new NotFoundException("Dish " + line.dishId() + " not found"));
			
			if (!dish.getRestaurant().getRestaurantId().equals(restaurant.getRestaurantId()))
				throw new BadRequestException("All dishes must belong to the selected restaurant");
			
			OrderItem item = new OrderItem();
			item.setDish(dish);
			item.setQuantity(line.quantity());
			item.setPrice(dish.getPrice());
			order.addItem(item);
			
			total = total.add(dish.getPrice().multiply(BigDecimal.valueOf(line.quantity())));
		}
		
		order.setTotalAmount(total);
		return OrderView.of(orders.save(order));
	}

	@Transactional(readOnly = true)
	public OrderView get(Long id) {
		return OrderView.of(orders.findById(id).orElseThrow(() -> new NotFoundException("Order not found")));
	}
}
