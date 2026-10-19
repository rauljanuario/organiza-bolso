package com.rauljanuario.organiza_bolso.dto.dashboard_dto;

import java.math.BigDecimal;

public record SummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {
}
