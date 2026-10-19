package org.example.spring.n_plus_one.repository;

import org.example.spring.n_plus_one.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
