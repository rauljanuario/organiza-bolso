package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.category_dto.GetCategoryDTO;
import com.rauljanuario.organiza_bolso.dto.category_dto.PostCategoryDTO;
import com.rauljanuario.organiza_bolso.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<GetCategoryDTO> createCategory(@RequestBody PostCategoryDTO data) {

        GetCategoryDTO result = categoryService.saveCategory(data).getBody();

        return ResponseEntity.ok(result);

    }

}
