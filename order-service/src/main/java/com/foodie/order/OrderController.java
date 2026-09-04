package com.foodie.order;
import com.foodie.order.OrderManager.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api")
class OrderController {
    private final OrderManager service;
    private final CustomerAuthorization auth;
    OrderController(OrderManager service, CustomerAuthorization auth) { this.service=service; this.auth=auth; }
    @PostMapping("/orders")
    ResponseEntity<OrderView> create(@RequestHeader(value="Authorization",required=false) String token, @RequestBody CreateRequest body) {
        Long id=auth.authenticate(token);
        if(body.customerId()!=null && !id.equals(body.customerId())) throw forbidden();
        return ResponseEntity.status(201).body(service.create(new CreateRequest(id,body.restaurantId(),body.items())));
    }
    @GetMapping("/orders/{id}")
    OrderView get(@RequestHeader(value="Authorization",required=false) String token, @PathVariable Long id) {
        Long customerId=auth.authenticate(token);
        OrderView order=service.get(id);
        if(!customerId.equals(order.customerId())) throw forbidden();
        return order;
    }
    @GetMapping("/customers/{id}/cart")
    CartView cart(@RequestHeader(value="Authorization",required=false) String token, @PathVariable Long id) {
        auth.requireOwner(token,id); return service.cart(id);
    }
    @PostMapping("/customers/{id}/cart/items")
    CartView add(@RequestHeader(value="Authorization",required=false) String token, @PathVariable Long id, @RequestBody LineRequest body) {
        auth.requireOwner(token,id); return service.add(id,body);
    }
    @DeleteMapping("/customers/{id}/cart")
    ResponseEntity<Void> clear(@RequestHeader(value="Authorization",required=false) String token, @PathVariable Long id) {
        auth.requireOwner(token,id); service.clear(id); return ResponseEntity.noContent().build();
    }
    @PostMapping("/customers/{id}/cart/checkout")
    ResponseEntity<OrderView> checkout(@RequestHeader(value="Authorization",required=false) String token, @PathVariable Long id) {
        auth.requireOwner(token,id); return ResponseEntity.status(201).body(service.checkout(id));
    }
    private ResponseStatusException forbidden() { return new ResponseStatusException(HttpStatus.FORBIDDEN,"Cannot access another customer's data"); }
}
