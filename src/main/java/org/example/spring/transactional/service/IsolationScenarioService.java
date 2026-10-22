package org.example.spring.transactional.service;

import lombok.RequiredArgsConstructor;
import org.example.spring.transactional.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


/**
 * READ_UNCOMMITTED
 * Commit edilmemiş değişiklik görülebilir.
 *
 * READ_COMMITTED
 *  İlk okuma 1000, ikinci okuma 500 olabilir.
 *
 * REPEATABLE_READ
 *  İlk okuma 1000, ikinci okuma yine 1000 olabilir.
 *
 * SERIALIZABLE
 *  Diğer işlem bekleyebilir veya hata/timeout alabilir.
 *
 *
 * **/

@Service
@RequiredArgsConstructor
public class IsolationScenarioService {
    private final WalletRepository repository;
    /*
     * veritabanının varsayılan isolation seviyesi kullanılır.
     * H2 PostgreSQL ve MySQL'in varsayılanları aynı olmak zorunda değil
     */
    @Transactional(isolation = Isolation.DEFAULT)
    public BalanceSnapshot defaultIsolation(
            Long walletId,
            Runnable concurrentOperation
    ) {
        return readTwice(
                "DEFAULT",
                walletId,
                concurrentOperation
        );
    }

    /*
     * başka transaction henüz commit etmediği veri okunabilir.
     *
     * dirty read ihtimali vardır.
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public BalanceSnapshot readUncommitted(
            Long walletId,
            Runnable concurrentOperation
    ) {
        return readTwice(
                "READ_UNCOMMITTED",
                walletId,
                concurrentOperation
        );
    }

    /*
     * sadece commit edilmiş veriler okunur
     *
     * dirty read engellenir
     * ancak aynı satır ikinci kez okunduğunda farklı sonuç gelebilir.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BalanceSnapshot readCommitted(
            Long walletId,
            Runnable concurrentOperation
    ) {
        return readTwice(
                "READ_COMMITTED",
                walletId,
                concurrentOperation
        );
    }

    /*
     * Transaction boyunca aynı satır tekrar okunduğunda
     * aynı değer görülmesi hedeflenir.
     *
     * Non repeatable read engellenir.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BalanceSnapshot repeatableRead(
            Long walletId,
            Runnable concurrentOperation
    ) {
        return readTwice(
                "REPEATABLE_READ",
                walletId,
                concurrentOperation
        );
    }

    /*
     * Transaction'lar sıralı çalışıyormuş gibi davranmayı hedefler.
     *
     * En güçlü isolation seviyesidir fakat lock, bekleme,
     * timeout ve düşük concurrency oluşturabilir.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BalanceSnapshot serializable(
            Long walletId,
            Runnable concurrentOperation
    ) {
        return readTwice(
                "SERIALIZABLE",
                walletId,
                concurrentOperation
        );
    }

    private BalanceSnapshot readTwice(
            String isolation,
            Long walletId,
            Runnable concurrentOperation
    ) {
        BigDecimal firstRead =
                repository.findBalanceById(walletId);

        concurrentOperation.run();

        BigDecimal secondRead =
                repository.findBalanceById(walletId);

        return new BalanceSnapshot(
                isolation,
                firstRead,
                secondRead
        );
    }

    public record BalanceSnapshot(
            String isolation,
            BigDecimal firstRead,
            BigDecimal secondRead
    ) {
        public boolean balanceChangedBetweenReads() {
            return firstRead.compareTo(secondRead) != 0;
        }
    }
}
