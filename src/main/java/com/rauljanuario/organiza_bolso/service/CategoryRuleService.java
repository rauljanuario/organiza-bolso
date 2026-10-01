package com.rauljanuario.organiza_bolso.service;

import com.rauljanuario.organiza_bolso.dto.category_rule_dto.GetCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.dto.category_rule_dto.PostCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.exception.CategoryNotFound;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import com.rauljanuario.organiza_bolso.model.Category;
import com.rauljanuario.organiza_bolso.model.CategoryRule;
import com.rauljanuario.organiza_bolso.model.User;
import com.rauljanuario.organiza_bolso.repository.CategoryRepository;
import com.rauljanuario.organiza_bolso.repository.CategoryRuleRepository;
import com.rauljanuario.organiza_bolso.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CategoryRuleService {

    private final CategoryRuleRepository categoryRuleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryRuleService(
            CategoryRuleRepository categoryRuleRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ) {
        this.categoryRuleRepository = categoryRuleRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GetCategoryRuleDTO saveCategoryRule(PostCategoryRuleDTO data) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository.findByIdAndUser_Id(data.categoryId(), user.getId())
                .orElseThrow(() -> new CategoryNotFound("Category not found"));

        CategoryRule categoryRule = new CategoryRule();
        categoryRule.setKeyword(data.keyword());
        categoryRule.setCategory(category);
        categoryRule.setPriority(data.priority() == null ? 1 : data.priority());
        categoryRule.setCreatedAt(LocalDateTime.now());

        CategoryRule saved = categoryRuleRepository.save(categoryRule);

        return new GetCategoryRuleDTO(
                saved.getId(),
                saved.getKeyword(),
                saved.getCategory().getId(),
                saved.getPriority()
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
