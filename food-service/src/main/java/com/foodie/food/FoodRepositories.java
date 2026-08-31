package com.foodie.food;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}

interface DishRepository extends JpaRepository<Dish, Long> {
	List<Dish> findByRestaurantRestaurantId(Long id);
}
