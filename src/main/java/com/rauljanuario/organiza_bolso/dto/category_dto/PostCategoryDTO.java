package com.rauljanuario.organiza_bolso.dto.category_dto;

import com.rauljanuario.organiza_bolso.enums.CategoryType;

public record PostCategoryDTO(

        String name,
        CategoryType type


) {
}
