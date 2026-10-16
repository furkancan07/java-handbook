package org.example.spring.idempotency.service;

import lombok.RequiredArgsConstructor;
import org.example.spring.idempotency.dto.TransferRequest;
import org.example.spring.idempotency.dto.TransferResponse;
import org.example.spring.idempotency.entity.Account;
import org.example.spring.idempotency.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;

    @Transactional
    public TransferResponse transfer(TransferRequest req){
        if (req.amount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        //  deadlock olmasın dite küçük idden kitleyelim
        Long first=Math.min(req.fromAccountId(), req.toAccountId());
        Long second=Math.max(req.fromAccountId(), req.toAccountId());
        // repository de lock koydum forupdate davranışı  yapcak
        Account a=accountRepository.findById(first).orElseThrow();
        Account b=accountRepository.findById(second).orElseThrow();

        Account from=a.getId().equals(req.fromAccountId()) ? a : b;
        Account to=from==a ? b : a;

        if(from.getBalance().compareTo(req.amount())<0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(req.amount()));
        to.setBalance(to.getBalance().add(req.amount()));

        return new TransferResponse(from.getId(),to.getId(),req.amount(), Instant.now());



    }

}
