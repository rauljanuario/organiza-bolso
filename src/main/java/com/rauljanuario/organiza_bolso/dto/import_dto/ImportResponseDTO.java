package com.rauljanuario.organiza_bolso.dto.import_dto;

import com.rauljanuario.organiza_bolso.enums.FormatType;
import com.rauljanuario.organiza_bolso.enums.StatusType;

import java.time.LocalDateTime;

public record ImportResponseDTO(
        Long id,
        String fileName,
        FormatType format,
        StatusType status,
        LocalDateTime importedAt,
        int transactionsImported
) {
}
