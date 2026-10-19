package org.example.spring.n_plus_one.repository;

import org.example.spring.n_plus_one.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
