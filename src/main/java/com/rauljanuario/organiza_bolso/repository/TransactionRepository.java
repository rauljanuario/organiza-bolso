package com.rauljanuario.organiza_bolso.repository;


import com.rauljanuario.organiza_bolso.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
