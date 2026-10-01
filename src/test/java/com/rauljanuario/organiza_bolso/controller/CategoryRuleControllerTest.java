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

@ExtendWith(MockitoExtension.class)
class CategoryRuleControllerTest {

    @InjectMocks
    private CategoryRuleController categoryRuleController;

    @Mock
    private CategoryRuleService categoryRuleService;

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
}
