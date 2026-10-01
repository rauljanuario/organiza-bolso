package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.category_rule_dto.GetCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.dto.category_rule_dto.PostCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.service.CategoryRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category-rules")
public class CategoryRuleController {

    private final CategoryRuleService categoryRuleService;

    public CategoryRuleController(CategoryRuleService categoryRuleService) {
        this.categoryRuleService = categoryRuleService;
    }

    @PostMapping
    public ResponseEntity<GetCategoryRuleDTO> createCategoryRule(@RequestBody PostCategoryRuleDTO data) {
        GetCategoryRuleDTO result = categoryRuleService.saveCategoryRule(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
