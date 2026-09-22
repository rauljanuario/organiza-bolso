package com.rauljanuario.organiza_bolso.service;

import com.rauljanuario.organiza_bolso.dto.category_dto.GetCategoryDTO;
import com.rauljanuario.organiza_bolso.dto.category_dto.PostCategoryDTO;
import com.rauljanuario.organiza_bolso.exception.UserNotFoundException;
import com.rauljanuario.organiza_bolso.model.Category;
import com.rauljanuario.organiza_bolso.model.User;
import com.rauljanuario.organiza_bolso.repository.CategoryRepository;
import com.rauljanuario.organiza_bolso.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public GetCategoryDTO saveCategory(PostCategoryDTO data) {

        User user = userRepository.findById(data.id())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var category = new Category();
        category.setUser(user);
        category.setName(data.name());
        category.setType(data.type());
        category.setCreatedAt(java.time.LocalDateTime.now());

        Category saved = categoryRepository.save(category);

        return new GetCategoryDTO(saved.getId(), saved.getName(), saved.getType());
    }


}
