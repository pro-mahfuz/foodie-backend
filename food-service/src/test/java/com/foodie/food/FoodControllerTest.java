package com.foodie.food;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodControllerTest {
    @Test void createsRestaurantAndDishAndRejectsWrongRestaurant() {
        var restaurants=mock(RestaurantRepository.class); var dishes=mock(DishRepository.class);
        var auth=mock(AdminAuthorization.class); var controller=new FoodController(restaurants,dishes,auth);
        when(restaurants.save(any())).thenAnswer(i -> { Restaurant r=i.getArgument(0); r.restaurantId=1L; return r; });
        var result=controller.createRestaurant("Bearer admin",new FoodController.RestaurantRequest("Test","Dubai","123",new BigDecimal("4.5")));
        assertEquals(201,result.getStatusCode().value());
        verify(auth).requireAdmin("Bearer admin");
        Restaurant r=new Restaurant();r.restaurantId=1L;
        when(restaurants.findById(1L)).thenReturn(Optional.of(r));
        when(dishes.save(any())).thenAnswer(i -> { Dish d=i.getArgument(0);d.dishId=2L;return d; });
        var body=new FoodController.DishRequest("Dish","Description",new BigDecimal("12.50"),"MAIN");
        assertEquals(201,controller.createDish("Bearer admin",1L,body).getStatusCode().value());
        Dish d=new Dish();d.restaurant=r;d.dishId=2L;
        when(dishes.findById(2L)).thenReturn(Optional.of(d));
        assertThrows(ResponseStatusException.class,()->controller.updateDish("Bearer admin",3L,2L,body));
        assertEquals("Dish",controller.updateDish("Bearer admin",1L,2L,body).name());
    }
}
