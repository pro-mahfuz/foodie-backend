package com.foodie.dto;

import com.foodie.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiDtos {
 private ApiDtos(){}
 public record RestaurantView(Long restaurantId,String name,String address,String phone,BigDecimal rating){public static RestaurantView of(Restaurant r){return new RestaurantView(r.getRestaurantId(),r.getName(),r.getAddress(),r.getPhone(),r.getRating());}}
 public record DishView(Long dishId,Long restaurantId,String name,String description,BigDecimal price,String category){public static DishView of(Dish d){return new DishView(d.getDishId(),d.getRestaurant().getRestaurantId(),d.getName(),d.getDescription(),d.getPrice(),d.getCategory());}}
 public record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@NotBlank String phone,@NotBlank String address,@NotBlank @Size(min=8,max=72) String password){}
 public record CustomerView(Long customerId,String name,String email,String phone,String address){public static CustomerView of(Customer c){return new CustomerView(c.getCustomerId(),c.getName(),c.getEmail(),c.getPhone(),c.getAddress());}}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record LoginResponse(Long customerId,String token,String tokenType){}
 public record OrderLineRequest(@NotNull Long dishId,@Min(1) int quantity){}
 public record CreateOrderRequest(@NotNull Long customerId,@NotNull Long restaurantId,@NotEmpty List<@Valid OrderLineRequest> items){}
 public record OrderItemView(Long orderItemId,Long dishId,String dishName,int quantity,BigDecimal price,BigDecimal subtotal){}
 public record OrderView(Long orderId,Long customerId,Long restaurantId,LocalDateTime orderDate,BigDecimal totalAmount,OrderStatus status,List<OrderItemView> items){
  public static OrderView of(CustomerOrder o){return new OrderView(o.getOrderId(),o.getCustomer().getCustomerId(),o.getRestaurant().getRestaurantId(),o.getOrderDate(),o.getTotalAmount(),o.getStatus(),o.getItems().stream().map(i->new OrderItemView(i.getOrderItemId(),i.getDish().getDishId(),i.getDish().getName(),i.getQuantity(),i.getPrice(),i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))).toList());}
 }
 public record CartItemRequest(@NotNull Long dishId,@Min(1) int quantity){}
 public record CartView(Long customerId,List<CartLine> items,BigDecimal total){}
 public record CartLine(Long dishId,String dishName,int quantity,BigDecimal unitPrice,BigDecimal subtotal){}
}
