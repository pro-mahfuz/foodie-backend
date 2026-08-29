package com.foodie.controller;

import com.foodie.dto.ApiDtos.*; import com.foodie.error.*; import com.foodie.model.Customer; import com.foodie.repository.CustomerRepository; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import java.nio.charset.StandardCharsets; import java.util.Base64;

@RestController @RequestMapping("/api")
public class CustomerController {
 private final CustomerRepository customers;private final PasswordEncoder encoder;
 public CustomerController(CustomerRepository c,PasswordEncoder e){customers=c;encoder=e;}
 @PostMapping("/users") ResponseEntity<CustomerView> register(@Valid @RequestBody RegisterRequest r){if(customers.existsByEmailIgnoreCase(r.email()))throw new ConflictException("Email is already registered");Customer c=new Customer();c.setName(r.name());c.setEmail(r.email().toLowerCase());c.setPhone(r.phone());c.setAddress(r.address());c.setPasswordHash(encoder.encode(r.password()));return ResponseEntity.status(HttpStatus.CREATED).body(CustomerView.of(customers.save(c)));}
 @PostMapping("/login") LoginResponse login(@Valid @RequestBody LoginRequest r){Customer c=customers.findByEmailIgnoreCase(r.email()).orElseThrow(()->new BadRequestException("Invalid email or password"));if(!encoder.matches(r.password(),c.getPasswordHash()))throw new BadRequestException("Invalid email or password");String demoToken=Base64.getUrlEncoder().withoutPadding().encodeToString((c.getCustomerId()+":"+System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));return new LoginResponse(c.getCustomerId(),demoToken,"Demo");}
}
