package com.foodie.food;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api")
@Transactional
class FoodController {
	private final RestaurantRepository restaurants;
	private final DishRepository dishes;
    private final AdminAuthorization authorization;

	FoodController(RestaurantRepository r, DishRepository d, AdminAuthorization authorization) {
		restaurants = r;
		dishes = d;
        this.authorization = authorization;
	}

    record RestaurantRequest(@NotBlank @Size(max=150) String name, @NotBlank @Size(max=255) String address,
        @NotBlank @Size(max=20) String phone, @DecimalMin("0.0") @DecimalMax("5.0") @Digits(integer=1,fraction=1) BigDecimal rating) {}
    record DishRequest(@NotBlank @Size(max=150) String name, String description,
        @NotNull @DecimalMin("0.01") @Digits(integer=8,fraction=2) BigDecimal price,
        @NotBlank @Size(max=100) String category) {}

    @PostMapping("/restaurants")
    ResponseEntity<RestaurantView> createRestaurant(@RequestHeader(value="Authorization",required=false) String token,
            @Valid @RequestBody RestaurantRequest body) {
        authorization.requireAdmin(token);
        Restaurant restaurant = new Restaurant();
        apply(restaurant, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(view(restaurants.save(restaurant)));
    }

    @PutMapping("/restaurants/{id}")
    RestaurantView updateRestaurant(@RequestHeader(value="Authorization",required=false) String token,
            @PathVariable Long id, @Valid @RequestBody RestaurantRequest body) {
        authorization.requireAdmin(token);
        Restaurant restaurant = restaurants.findById(id).orElseThrow(() -> missing("Restaurant"));
        apply(restaurant, body);
        return view(restaurants.save(restaurant));
    }

    @PostMapping("/restaurants/{id}/dishes")
    ResponseEntity<DishView> createDish(@RequestHeader(value="Authorization",required=false) String token,
            @PathVariable Long id, @Valid @RequestBody DishRequest body) {
        authorization.requireAdmin(token);
        Dish dish = new Dish();
        dish.restaurant = restaurants.findById(id).orElseThrow(() -> missing("Restaurant"));
        apply(dish, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(view(dishes.save(dish)));
    }

    @PutMapping("/restaurants/{id}/dishes/{dishId}")
    DishView updateDish(@RequestHeader(value="Authorization",required=false) String token,
            @PathVariable Long id, @PathVariable Long dishId, @Valid @RequestBody DishRequest body) {
        authorization.requireAdmin(token);
        Dish dish = dishes.findById(dishId).orElseThrow(() -> missing("Dish"));
        if (!dish.restaurant.getRestaurantId().equals(id)) throw missing("Dish in restaurant");
        apply(dish, body);
        return view(dishes.save(dish));
    }

    private void apply(Restaurant r, RestaurantRequest body) {
        r.name=body.name(); r.address=body.address(); r.phone=body.phone(); r.rating=body.rating();
    }
    private void apply(Dish d, DishRequest body) {
        d.name=body.name(); d.description=body.description(); d.price=body.price(); d.category=body.category();
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
		return new DishView(d.dishId, d.restaurant.getRestaurantId(), d.name, d.description, d.price, d.category);
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
