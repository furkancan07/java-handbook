package org.example.spring.idempotency.controller;

import lombok.RequiredArgsConstructor;
import org.example.spring.idempotency.dto.TransferRequest;
import org.example.spring.idempotency.dto.TransferResponse;
import org.example.spring.idempotency.service.IdempotencyService;
import org.example.spring.idempotency.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final IdempotencyService idempotencyService;

    @PostMapping("/transfer")
    ResponseEntity<TransferResponse> transfer(@RequestHeader("Idempotency-key") String key, @RequestBody TransferRequest req){
        TransferResponse response= idempotencyService.execute(key, TransferResponse.class,()->transferService.transfer(req));
        return ResponseEntity.ok(response);
    }

}
