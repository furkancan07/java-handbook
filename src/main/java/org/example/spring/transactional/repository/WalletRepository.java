package org.example.spring.transactional.repository;

import org.example.spring.transactional.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("select w.balance from Wallet w where w.id = :id")
    BigDecimal findBalanceById(@Param("id") Long id);
}
