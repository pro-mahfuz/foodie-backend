package com.foodie.repository;
import com.foodie.model.CustomerOrder; import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<CustomerOrder,Long> {}
