package org.example.spring.idempotency.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(Long fromAccountId, Long toAccountId, BigDecimal amount, Instant at) {

}
