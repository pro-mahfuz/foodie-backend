package com.foodie.repository;
import com.foodie.model.Dish; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface DishRepository extends JpaRepository<Dish,Long> { List<Dish> findByRestaurantRestaurantId(Long restaurantId); }
