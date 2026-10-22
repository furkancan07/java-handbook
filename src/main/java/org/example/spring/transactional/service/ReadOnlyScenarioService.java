package org.example.spring.transactional.service;

import lombok.RequiredArgsConstructor;
import org.example.spring.errorhandling.BusinessValidationException;
import org.example.spring.errorhandling.BusinessValidationRule;
import org.example.spring.transactional.model.Wallet;
import org.example.spring.transactional.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReadOnlyScenarioService {
    private final WalletRepository walletRepository;
    // salt okuma
    @Transactional(readOnly = true)
    public Wallet findWallet(Long id){
        return getWallet(id);
    }
    /** dirty checking : hibernate bakar bir değişiklik var mı varsa db ye save vağırmamana rağmen yollar
    eğer ki readonly true yaparsak değişiklik olsun olmasın save çağırmadığımız sürece db ye yollmaz kısaca flushu kapatır */
    @Transactional
    public void changeBalance(Long id, BigDecimal newBalance){
        Wallet wallet=getWallet(id);
        wallet.setBalance(newBalance);
    }

    private Wallet getWallet(Long id){
        return walletRepository.findById(id).orElseThrow(()->new BusinessValidationException(BusinessValidationRule.NOT_FOUND));
    }
}
