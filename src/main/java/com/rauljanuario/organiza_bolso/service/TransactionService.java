package com.rauljanuario.organiza_bolso.service;

import com.rauljanuario.organiza_bolso.dto.transaction_dto.GetTransactionDTO;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import com.rauljanuario.organiza_bolso.model.Transaction;
import com.rauljanuario.organiza_bolso.model.User;
import com.rauljanuario.organiza_bolso.repository.TransactionRepository;
import com.rauljanuario.organiza_bolso.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<GetTransactionDTO> getTransactions(int month, int year) {
        User user = getAuthenticatedUser();
        YearMonth period = YearMonth.of(year, month);
        LocalDateTime start = period.atDay(1).atStartOfDay();
        LocalDateTime end = period.plusMonths(1).atDay(1).atStartOfDay();

        return transactionRepository
                .findByUser_IdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(user.getId(), start, end)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private GetTransactionDTO toDTO(Transaction transaction) {
        return new GetTransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCategory() == null ? null : transaction.getCategory().getId(),
                transaction.getCategory() == null ? null : transaction.getCategory().getName(),
                transaction.isManuallyCategorized()
        );
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("User not authenticated");
        }

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            throw new UserNotFoundException("Authenticated user has no email");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
