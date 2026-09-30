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

import java.util.List;

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

        GetCategoryDTO dtoFictExit = new GetCategoryDTO(1L, "Almoço", CategoryType.EXPENSE);

        Mockito.when(categoryService.saveCategory(dtoFictEntry)).thenReturn(dtoFictExit);

        //ACT
        var result = categoryController.createCategory(dtoFictEntry);

        //ASSERTIONS
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(dtoFictEntry.name(), result.getBody().name());

    }

    @Test
    @DisplayName("Return all categories")
    void getCategories_ReturnsAllCategories() {

        //ARRANGE
        GetCategoryDTO dtoFictEntry = new GetCategoryDTO(1L, "Almoço", CategoryType.EXPENSE);
        GetCategoryDTO dtoFictEntry2 = new GetCategoryDTO(2L, "Salário", CategoryType.INCOME);

        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(dtoFictEntry, dtoFictEntry2));

        //ACT
        var result = categoryController.getCategories();

        //ASSERTIONS
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(2, result.getBody().size());
    }
    @Test
    @DisplayName("Update a category")
    void updateCategory_WithValidData_ReturnsUpdatedCategory() {
        //ARRANGE
        Long categoryId = 1L;
        PostCategoryDTO dtoFictEntry = new PostCategoryDTO(categoryId, "Almoço", CategoryType.EXPENSE);
        PostCategoryDTO dtoFictExit = new PostCategoryDTO(categoryId, "Jantar", CategoryType.EXPENSE);

        Mockito.when(categoryService.updateCategory(categoryId, dtoFictEntry)).thenReturn(dtoFictExit);

        //ACT
        var result = categoryController.updateCategory(categoryId, dtoFictEntry);

        //ASSERTIONS
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(dtoFictExit.name(), result.getBody().name());
    }
}