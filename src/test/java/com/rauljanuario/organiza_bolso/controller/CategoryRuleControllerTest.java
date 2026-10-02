package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.category_rule_dto.GetCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.dto.category_rule_dto.PostCategoryRuleDTO;
import com.rauljanuario.organiza_bolso.service.CategoryRuleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;


import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryRuleControllerTest {

    @InjectMocks
    private CategoryRuleController categoryRuleController;

    @Mock
    private CategoryRuleService categoryRuleService;

    @Test
    @DisplayName("Return all category rules")
    void getCategories_Rules_ReturnsAllCategoryRules() {

        // ARRANGE
        GetCategoryRuleDTO rule1 = new GetCategoryRuleDTO(1L, "mercado", 1L, 1);
        GetCategoryRuleDTO rule2 = new GetCategoryRuleDTO(2L, "supermercado", 2L, 2);

        Mockito.when(categoryRuleService.getAllCategoriesRules()).thenReturn(List.of(rule1, rule2));

        // ACT
        var result = categoryRuleController.getCategoriesRules();

        // ASSERTIONS
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(2, result.getBody().size());
        Assertions.assertEquals(rule1, result.getBody().get(0));
        Assertions.assertEquals(rule2, result.getBody().get(1));
    }

    @Test
    @DisplayName("Return a new category rule")
    void createCategoryRule_WithValidData_ReturnsNewCategoryRule() {

        // ARRANGE
        PostCategoryRuleDTO input = new PostCategoryRuleDTO(null, "mercado", 1L, 1);
        GetCategoryRuleDTO output = new GetCategoryRuleDTO(1L, "mercado", 1L, 1);

        Mockito.when(categoryRuleService.saveCategoryRule(input)).thenReturn(output);

        // ACT
        var result = categoryRuleController.createCategoryRule(input);

        // ASSERTIONS
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(output, result.getBody());
    }

    @Test
    @DisplayName("Update an existing category rule")
    void updateCategoryRule_WithValidData_ReturnsUpdatedCategoryRule() {

        // ARRANGE
        Long categoryRuleId = 1L;
        PostCategoryRuleDTO input = new PostCategoryRuleDTO(null, "mercado", 1L, 1);
        GetCategoryRuleDTO serviceInput = new GetCategoryRuleDTO(categoryRuleId, "mercado", 1L, 1);
        GetCategoryRuleDTO output = new GetCategoryRuleDTO(1L, "mercado", 1L, 2);

        Mockito.when(categoryRuleService.updateCategoryRule(serviceInput)).thenReturn(output);

        // ACT
        var result = categoryRuleController.updateCategoryRule(categoryRuleId, input);

        // ASSERTIONS
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(output, result.getBody());
        Mockito.verify(categoryRuleService).updateCategoryRule(serviceInput);
    }

    @Test
    @DisplayName("Delete an existing category rule")
    void deleteCategoryRule_WithValidId_ReturnsNoContent() {

        // ARRANGE
        Long categoryRuleId = 1L;

        // ACT
        var result = categoryRuleController.deleteCategoryRule(categoryRuleId);

        // ASSERTIONS
        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Mockito.verify(categoryRuleService).deleteCategoryRule(categoryRuleId);
    }
}