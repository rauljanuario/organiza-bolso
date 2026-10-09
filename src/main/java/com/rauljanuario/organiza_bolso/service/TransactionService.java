package com.rauljanuario.organiza_bolso.service;

import com.rauljanuario.organiza_bolso.dto.transaction_dto.PostTransactionDTO;
import com.rauljanuario.organiza_bolso.dto.transaction_dto.GetTransactionDTO;
import com.rauljanuario.organiza_bolso.exception.CategoryNotFound;
import com.rauljanuario.organiza_bolso.exception.TransactionNotFound;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import com.rauljanuario.organiza_bolso.model.Category;
import com.rauljanuario.organiza_bolso.model.Transaction;
import com.rauljanuario.organiza_bolso.model.User;
import com.rauljanuario.organiza_bolso.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository,
                               UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GetTransactionDTO saveTransaction(PostTransactionDTO data) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findByIdAndUser_Id(data.categoryId(), user.getId())
                .orElseThrow(() -> new CategoryNotFound("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setDescription(data.description());
        transaction.setAmount(data.amount());
        transaction.setDate(data.transactionDate().atStartOfDay());
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction.setManuallyCategorized(true);
        transaction.setCreatedAt(LocalDateTime.now());

        return toDTO(transactionRepository.save(transaction));
    }

    @Transactional
    public GetTransactionDTO updateTransaction(Long id, PostTransactionDTO data) {
        User user = getAuthenticatedUser();
        Transaction transaction = transactionRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new TransactionNotFound("Transaction not found"));
        Category category = categoryRepository.findByIdAndUser_Id(data.categoryId(), user.getId())
                .orElseThrow(() -> new CategoryNotFound("Category not found"));

        transaction.setDescription(data.description());
        transaction.setAmount(data.amount());
        transaction.setDate(data.transactionDate().atStartOfDay());
        transaction.setCategory(category);
        transaction.setManuallyCategorized(true);

        return toDTO(transactionRepository.save(transaction));
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
