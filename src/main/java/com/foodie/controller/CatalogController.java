package com.foodie.controller;

import com.foodie.dto.ApiDtos.*; import com.foodie.error.NotFoundException; import com.foodie.repository.*; import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api")
public class CatalogController {
 private final RestaurantRepository restaurants;private final DishRepository dishes;
 public CatalogController(RestaurantRepository r,DishRepository d){restaurants=r;dishes=d;}
 @GetMapping("/restaurants") List<RestaurantView> restaurants(){return restaurants.findAll().stream().map(RestaurantView::of).toList();}
 @GetMapping("/restaurants/{id}") RestaurantView restaurant(@PathVariable Long id){return RestaurantView.of(restaurants.findById(id).orElseThrow(()->new NotFoundException("Restaurant not found")));}
 @GetMapping("/restaurants/{id}/dishes") List<DishView> restaurantDishes(@PathVariable Long id){if(!restaurants.existsById(id))throw new NotFoundException("Restaurant not found");return dishes.findByRestaurantRestaurantId(id).stream().map(DishView::of).toList();}
 @GetMapping("/dishes/{id}") DishView dish(@PathVariable Long id){return DishView.of(dishes.findById(id).orElseThrow(()->new NotFoundException("Dish not found")));}
}
