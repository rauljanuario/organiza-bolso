package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.category_dto.GetCategoryDTO;
import com.rauljanuario.organiza_bolso.dto.category_dto.PostCategoryDTO;
import com.rauljanuario.organiza_bolso.enums.CategoryType;
import com.rauljanuario.organiza_bolso.service.CategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;


    @Test
    @DisplayName("Return a new category")
    void createCategory_WithValidData_ReturnsNewCategory() {

        //ARRANGE
        PostCategoryDTO dtoFictEntry = new PostCategoryDTO(1L, "Almoço", CategoryType.EXPENSE);

        ResponseEntity<GetCategoryDTO> dtoFictExit = ResponseEntity.ok(new GetCategoryDTO(1L, "Almoço", CategoryType.EXPENSE));

        Mockito.when(categoryService.saveCategory(dtoFictEntry)).thenReturn(dtoFictExit);

        //ACT
        var result = categoryController.createCategory(dtoFictEntry);

        //ASSERTIONS
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(dtoFictEntry.name(), result.getBody().name());

    }
}