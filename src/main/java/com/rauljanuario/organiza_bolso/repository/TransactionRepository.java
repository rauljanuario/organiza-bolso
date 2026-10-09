package com.rauljanuario.organiza_bolso.repository;


import com.rauljanuario.organiza_bolso.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser_IdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<Transaction> findByIdAndUser_Id(Long id, Long userId);
}
