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
        GetCategoryRuleDTO rule1 = new GetCategoryRuleDTO(1L, "mercado", 1L, 1);
        GetCategoryRuleDTO rule2 = new GetCategoryRuleDTO(2L, "supermercado", 2L, 2);

        Mockito.when(categoryRuleService.getAllCategoriesRules()).thenReturn(List.of(rule1, rule2));

        var result = categoryRuleController.getCategoriesRules();

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(2, result.getBody().size());
        Assertions.assertEquals(rule1, result.getBody().get(0));
        Assertions.assertEquals(rule2, result.getBody().get(1));
    }

    @Test
    @DisplayName("Return a new category rule")
    void createCategoryRule_WithValidData_ReturnsNewCategoryRule() {
        PostCategoryRuleDTO input = new PostCategoryRuleDTO(null, "mercado", 1L, 1);
        GetCategoryRuleDTO output = new GetCategoryRuleDTO(1L, "mercado", 1L, 1);

        Mockito.when(categoryRuleService.saveCategoryRule(input)).thenReturn(output);

        var result = categoryRuleController.createCategoryRule(input);

        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(output, result.getBody());
    }

    @Test
    @DisplayName("Update an existing category rule")
    void updateCategoryRule_WithValidData_ReturnsUpdatedCategoryRule() {
        GetCategoryRuleDTO input = new GetCategoryRuleDTO(1L, "mercado", 1L, 1);
        GetCategoryRuleDTO output = new GetCategoryRuleDTO(1L, "mercado", 1L, 2);

        Mockito.when(categoryRuleService.updateCategoryRule(input)).thenReturn(output);

        var result = categoryRuleController.updateCategoryRule(input);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(output, result.getBody());
    }
}