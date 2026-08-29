package com.foodie.repository;
import com.foodie.model.Restaurant; import org.springframework.data.jpa.repository.JpaRepository;
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {}
