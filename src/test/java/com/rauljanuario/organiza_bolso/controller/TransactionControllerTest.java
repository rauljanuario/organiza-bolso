package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.transaction_dto.GetTransactionDTO;
import com.rauljanuario.organiza_bolso.dto.transaction_dto.PostTransactionDTO;
import com.rauljanuario.organiza_bolso.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Test
    @DisplayName("Return transactions for a month")
    void getTransactions_ReturnsTransactionsForMonth() {

        // ARRANGE
        int month = 10;
        int year = 2026;
        GetTransactionDTO transaction1 = new GetTransactionDTO(
                1L,
                "Almoço",
                new BigDecimal("35.90"),
                LocalDateTime.of(2026, 10, 1, 12, 0),
                1L,
                "Alimentação",
                true
        );
        GetTransactionDTO transaction2 = new GetTransactionDTO(
                2L,
                "Salário",
                new BigDecimal("5000.00"),
                LocalDateTime.of(2026, 10, 5, 9, 0),
                2L,
                "Salário",
                false
        );

        Mockito.when(transactionService.getTransactions(month, year))
                .thenReturn(List.of(transaction1, transaction2));

        // ACT
        var result = transactionController.getTransactions(month, year);

        // ASSERTIONS
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(List.of(transaction1, transaction2), result.getBody());
        Mockito.verify(transactionService).getTransactions(month, year);
    }

    @Test
    @DisplayName("Return a new transaction")
    void createTransaction_WithValidData_ReturnsNewTransaction() {

        // ARRANGE
        PostTransactionDTO input = new PostTransactionDTO(
                null,
                "Almoço",
                new BigDecimal("35.90"),
                LocalDate.of(2026, 10, 1),
                1L
        );
        GetTransactionDTO output = new GetTransactionDTO(
                1L,
                input.description(),
                input.amount(),
                input.transactionDate().atStartOfDay(),
                input.categoryId(),
                "Alimentação",
                true
        );

        Mockito.when(transactionService.saveTransaction(input)).thenReturn(output);

        // ACT
        var result = transactionController.createTransaction(input);

        // ASSERTIONS
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(output, result.getBody());
        Mockito.verify(transactionService).saveTransaction(input);
    }

    @Test
    @DisplayName("Update an existing transaction")
    void updateTransaction_WithValidData_ReturnsUpdatedTransaction() {

        // ARRANGE
        Long transactionId = 1L;
        PostTransactionDTO input = new PostTransactionDTO(
                transactionId,
                "Almoço atualizado",
                new BigDecimal("40.00"),
                LocalDate.of(2026, 10, 2),
                1L
        );
        GetTransactionDTO output = new GetTransactionDTO(
                transactionId,
                input.description(),
                input.amount(),
                input.transactionDate().atStartOfDay(),
                input.categoryId(),
                "Alimentação",
                true
        );

        Mockito.when(transactionService.updateTransaction(transactionId, input)).thenReturn(output);

        // ACT
        var result = transactionController.updateTransaction(transactionId, input);

        // ASSERTIONS
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(output, result.getBody());
        Mockito.verify(transactionService).updateTransaction(transactionId, input);
    }
}
