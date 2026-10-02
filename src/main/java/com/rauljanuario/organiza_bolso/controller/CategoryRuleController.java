package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.category_rule_dto.GetCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.dto.category_rule_dto.PostCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.service.CategoryRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category-rules")
public class CategoryRuleController {

    private final CategoryRuleService categoryRuleService;

    public CategoryRuleController(CategoryRuleService categoryRuleService) {
        this.categoryRuleService = categoryRuleService;
    }

    @GetMapping
    public ResponseEntity<List<GetCategoryRuleDTO>> getCategoriesRules() {
        return ResponseEntity.ok(categoryRuleService.getAllCategoriesRules());
    }


    @PostMapping
    public ResponseEntity<GetCategoryRuleDTO> createCategoryRule(@RequestBody PostCategoryRuleDTO data) {
        GetCategoryRuleDTO result = categoryRuleService.saveCategoryRule(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping
    public ResponseEntity<GetCategoryRuleDTO> updateCategoryRule(@RequestBody GetCategoryRuleDTO data) {
        GetCategoryRuleDTO result = categoryRuleService.updateCategoryRule(data);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/id")
    public ResponseEntity<Void> deleteCategoryRule(@PathVariable Long id) {
        categoryRuleService.deleteCategoryRule(id);
        return ResponseEntity.noContent().build();
    }
}
