package com.rauljanuario.organiza_bolso.dto.dashboard_dto;

import java.math.BigDecimal;

public record CategorySummaryDTO(
        Long categoryId,
        String categoryName,
        BigDecimal total
) {
}
