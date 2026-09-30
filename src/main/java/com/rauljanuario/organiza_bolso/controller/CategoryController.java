package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.category_dto.GetCategoryDTO;
import com.rauljanuario.organiza_bolso.dto.category_dto.PostCategoryDTO;
import com.rauljanuario.organiza_bolso.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    public ResponseEntity<List<GetCategoryDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }


    @PostMapping
    public ResponseEntity<GetCategoryDTO> createCategory(@RequestBody PostCategoryDTO data) {

        GetCategoryDTO result = categoryService.saveCategory(data);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }

    @PutMapping("/{id}")
    public ResponseEntity<PostCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody PostCategoryDTO data) {
        PostCategoryDTO result = categoryService.updateCategory(id, data);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();

    }
}
