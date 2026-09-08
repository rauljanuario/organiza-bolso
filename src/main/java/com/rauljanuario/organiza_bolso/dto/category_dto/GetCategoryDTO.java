package com.rauljanuario.organiza_bolso.dto.category_dto;

import com.rauljanuario.organiza_bolso.enums.CategoryType;

public record GetCategoryDTO(

        Long id,
        String name,
        CategoryType type

) {
}
