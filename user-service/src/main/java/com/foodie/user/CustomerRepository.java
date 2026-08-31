package com.foodie.user;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.Optional;
interface CustomerRepository extends JpaRepository<Customer,Long>{Optional<Customer> findByEmailIgnoreCase(String email);boolean existsByEmailIgnoreCase(String email);}
