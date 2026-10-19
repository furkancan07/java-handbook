package org.example.spring.n_plus_one.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue
    Long id;

    int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    Product product;
}
