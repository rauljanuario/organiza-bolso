package com.rauljanuario.organiza_bolso.dto.transaction_dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetTransactionDTO(
        Long id,
        String description,
        BigDecimal amount,
        LocalDateTime transactionDate,
        Long categoryId,
        String categoryName,
        boolean manuallyCategorized
) {
}
