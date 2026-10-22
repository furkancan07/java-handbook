package org.example.spring.transactional.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.spring.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "tx_wallet")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseEntity {

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    public void debit(BigDecimal amount) {
        validatePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Yetersiz bakiye");
        }
        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        validatePositive(amount);
        balance = balance.add(amount);
    }

    private void validatePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Tutar sıfırdan büyük olmalı");
        }
    }
}
