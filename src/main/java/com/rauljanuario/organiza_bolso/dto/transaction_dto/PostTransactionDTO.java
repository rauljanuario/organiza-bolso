package com.rauljanuario.organiza_bolso.dto.transaction_dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PostTransactionDTO(
        Long id,
        String description,
        BigDecimal amount,
        LocalDate transactionDate,
        Long categoryId
) {
}
