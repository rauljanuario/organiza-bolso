package com.rauljanuario.organiza_bolso.service;

import com.rauljanuario.organiza_bolso.dto.dashboard_dto.CategorySummaryDTO;
import com.rauljanuario.organiza_bolso.dto.dashboard_dto.SummaryDTO;
import com.rauljanuario.organiza_bolso.enums.CategoryType;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import com.rauljanuario.organiza_bolso.model.Transaction;
import com.rauljanuario.organiza_bolso.model.User;
import com.rauljanuario.organiza_bolso.repository.TransactionRepository;
import com.rauljanuario.organiza_bolso.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public SummaryDTO getSummary(int month, int year) {
        User user = getAuthenticatedUser();
        YearMonth period = YearMonth.of(year, month);
        LocalDateTime start = period.atDay(1).atStartOfDay();
        LocalDateTime end = period.plusMonths(1).atDay(1).atStartOfDay();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactionRepository
                .findByUser_IdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(user.getId(), start, end)) {
            if (transaction.getCategory() == null) {
                continue;
            }

            if (transaction.getCategory().getType() == CategoryType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if (transaction.getCategory().getType() == CategoryType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        return new SummaryDTO(totalIncome, totalExpense, totalIncome.subtract(totalExpense));
    }

    @Transactional(readOnly = true)
    public List<CategorySummaryDTO> getCategorySummary(int month, int year, CategoryType type) {
        User user = getAuthenticatedUser();
        YearMonth period = YearMonth.of(year, month);
        LocalDateTime start = period.atDay(1).atStartOfDay();
        LocalDateTime end = period.plusMonths(1).atDay(1).atStartOfDay();

        Map<Long, CategorySummaryDTO> summaries = new LinkedHashMap<>();

        for (Transaction transaction : transactionRepository
                .findByUser_IdAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(user.getId(), start, end)) {
            if (transaction.getCategory() == null || transaction.getCategory().getType() != type) {
                continue;
            }

            var category = transaction.getCategory();
            summaries.compute(category.getId(), (categoryId, summary) -> {
                BigDecimal total = summary == null
                        ? transaction.getAmount()
                        : summary.total().add(transaction.getAmount());
                return new CategorySummaryDTO(categoryId, category.getName(), total);
            });
        }

        return new ArrayList<>(summaries.values());
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
