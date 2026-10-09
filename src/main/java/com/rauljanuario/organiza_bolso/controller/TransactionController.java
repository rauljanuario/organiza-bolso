package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.transaction_dto.GetTransactionDTO;
import com.rauljanuario.organiza_bolso.dto.transaction_dto.PostTransactionDTO;
import com.rauljanuario.organiza_bolso.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<GetTransactionDTO>> getTransactions(
            @RequestParam("month") int month,
            @RequestParam("year") int year
    ) {
        return ResponseEntity.ok(transactionService.getTransactions(month, year));
    }

    @PostMapping
    public ResponseEntity<GetTransactionDTO> createTransaction(@RequestBody PostTransactionDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.saveTransaction(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetTransactionDTO> updateTransaction(
            @PathVariable Long id,
            @RequestBody PostTransactionDTO data
    ) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, data));
    }
}
