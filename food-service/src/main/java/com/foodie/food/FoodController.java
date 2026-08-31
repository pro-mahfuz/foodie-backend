package com.foodie.food;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
class FoodController {
	private final RestaurantRepository restaurants;
	private final DishRepository dishes;

	FoodController(RestaurantRepository r, DishRepository d) {
		restaurants = r;
		dishes = d;
	}

	record RestaurantView(Long restaurantId, String name, String address, String phone, BigDecimal rating) {
	}

	record DishView(Long dishId, Long restaurantId, String name, String description, BigDecimal price,
			String category) {
	}

	private RestaurantView view(Restaurant r) {
		return new RestaurantView(r.restaurantId, r.name, r.address, r.phone, r.rating);
	}

	private DishView view(Dish d) {
		return new DishView(d.dishId, d.restaurant.restaurantId, d.name, d.description, d.price, d.category);
	}

	@GetMapping("/restaurants")
	List<RestaurantView> all() {
		return restaurants.findAll().stream().map(this::view).toList();
	}

	@GetMapping("/restaurants/{id}")
	RestaurantView restaurant(@PathVariable Long id) {
		return view(restaurants.findById(id).orElseThrow(() -> missing("Restaurant")));
	}

	@GetMapping("/restaurants/{id}/dishes")
	List<DishView> dishes(@PathVariable Long id) {
		if (!restaurants.existsById(id))
			throw missing("Restaurant");
		return dishes.findByRestaurantRestaurantId(id).stream().map(this::view).toList();
	}

	@GetMapping("/dishes/{id}")
	DishView dish(@PathVariable Long id) {
		return view(dishes.findById(id).orElseThrow(() -> missing("Dish")));
	}

	private ResponseStatusException missing(String what) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found");
	}
}
