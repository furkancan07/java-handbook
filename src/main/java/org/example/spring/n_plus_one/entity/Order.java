package org.example.spring.n_plus_one.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(mappedBy = "order")
    @BatchSize(size = 20) // n+1 in çözümlerinden biri 20 tane getir çok kullanılmaz
    List<OrderItem> items =new ArrayList<>();

    public void addAll(OrderItem orderItem){
        items.add(orderItem);
        orderItem.setOrder(this);
    }

}
