package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.import_dto.ImportResponseDTO;
import com.rauljanuario.organiza_bolso.enums.FormatType;
import com.rauljanuario.organiza_bolso.enums.StatusType;
import com.rauljanuario.organiza_bolso.service.ImportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ImportControllerTest {

    @InjectMocks
    private ImportController importController;

    @Mock
    private ImportService importService;

    @Test
    void importStatement_WithValidFile_ReturnsCreated() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "statement.csv",
                "text/csv",
                "description,amount,transactionDate\nLunch,35.90,2026-10-01".getBytes()
        );
        ImportResponseDTO output = new ImportResponseDTO(
                1L,
                "statement.csv",
                FormatType.CSV,
                StatusType.COMPLETED,
                LocalDateTime.now(),
                1
        );
        Mockito.when(importService.importStatement(file)).thenReturn(output);

        var result = importController.importStatement(file);

        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertEquals(output, result.getBody());
        Mockito.verify(importService).importStatement(file);
    }
}
