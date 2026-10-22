package org.example.spring.transactional.service;

import lombok.RequiredArgsConstructor;
import org.example.spring.transactional.exception.CheckedTransferException;
import org.example.spring.transactional.model.Wallet;
import org.example.spring.transactional.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AtomicityScenarioService {

    private final WalletRepository walletRepository;

    /**
     * Ortak transaction yoktur. İlk saveAndFlush kendi repository transaction'ında
     * commit olabilir; ardından hata gelirse kaynak cüzdandaki eksilme kalır.
     */
    public void transferWithoutTransaction(Long fromId, Long toId, BigDecimal amount) {
        Wallet from = findWallet(fromId);
        from.debit(amount);
        walletRepository.saveAndFlush(from);

        throw new RuntimeException("İlk kayıt commit olduktan sonra hata oluştu");

        // Bu satırlar senaryoda çalışmaz:
        // Wallet to = findWallet(toId);
        // to.credit(amount);
        // walletRepository.saveAndFlush(to);
    }

    /**
     * saveAndFlush SQL'i erkenden çalıştırsa bile commit etmez. RuntimeException
     * transaction sınırından çıktığı için iki cüzdan da rollback olur.
     */
    @Transactional
    public void transferWithRuntimeFailure(Long fromId, Long toId, BigDecimal amount) {
        Wallet from = findWallet(fromId);
        Wallet to = findWallet(toId);

        from.debit(amount);
        walletRepository.flush();

        if (toId != null) {
            throw new RuntimeException("Transaction rollback senaryosu");
        }

        to.credit(amount);
    }

    @Transactional
    public void successfulTransfer(Long fromId, Long toId, BigDecimal amount) {
        Wallet from = findWallet(fromId);
        Wallet to = findWallet(toId);
        from.debit(amount);
        to.credit(amount);
        // save çağrısı gerekmiyor; managed entity'ler commit sırasında dirty checking ile yazılır.
    }

    /**
     * Checked exception varsayılan rollback kuralına girmez netot exception ile
     * bitmesine rağmen debit işlemi commit edilir.
     */
    @Transactional
    public void checkedExceptionDoesNotRollback(Long walletId, BigDecimal amount)
            throws CheckedTransferException {
        Wallet wallet = findWallet(walletId);
        wallet.debit(amount);
        walletRepository.flush();
        throw new CheckedTransferException("Checked exception: varsayılan davranış commit");
    }

    @Transactional(rollbackFor = CheckedTransferException.class)
    public void checkedExceptionRollsBack(Long walletId, BigDecimal amount)
            throws CheckedTransferException {
        Wallet wallet = findWallet(walletId);
        wallet.debit(amount);
        walletRepository.flush();
        throw new CheckedTransferException("rollbackFor nedeniyle rollback");
    }

    private Wallet findWallet(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cüzdan bulunamadı: " + id));
    }
}
