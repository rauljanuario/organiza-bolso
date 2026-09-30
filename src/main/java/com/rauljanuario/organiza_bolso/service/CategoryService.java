package com.rauljanuario.organiza_bolso.service;

import com.rauljanuario.organiza_bolso.dto.category_dto.GetCategoryDTO;
import com.rauljanuario.organiza_bolso.dto.category_dto.PostCategoryDTO;
import com.rauljanuario.organiza_bolso.exception.CategoryNotFound;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import com.rauljanuario.organiza_bolso.model.Category;
import com.rauljanuario.organiza_bolso.model.User;
import com.rauljanuario.organiza_bolso.repository.CategoryRepository;
import com.rauljanuario.organiza_bolso.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public GetCategoryDTO saveCategory(PostCategoryDTO data) {

        User user = getAuthenticatedUser();

        var category = new Category();
        category.setUser(user);
        category.setName(data.name());
        category.setType(data.type());
        category.setCreatedAt(java.time.LocalDateTime.now());

        Category saved = categoryRepository.save(category);

        return new GetCategoryDTO(saved.getId(), saved.getName(), saved.getType());
    }

    public List<GetCategoryDTO> getAllCategories() {

        User user = getAuthenticatedUser();

        List<Category> categories = categoryRepository.findByUser_Id(user.getId());

        return categories.stream()
                .map(category -> new GetCategoryDTO(category.getId(), category.getName(), category.getType()))
                .toList();
    }

    public PostCategoryDTO updateCategory(Long id, PostCategoryDTO data) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new CategoryNotFound("Category not found"));

        category.setName(data.name());
        category.setType(data.type());

        categoryRepository.save(category);

        return new PostCategoryDTO(category.getId(), category.getName(), category.getType());
    }

    @Transactional
    public void deleteCategory(Long id) {
        User user = getAuthenticatedUser();

        Category category = categoryRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new CategoryNotFound("Category not found"));

        categoryRepository.delete(category);
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
