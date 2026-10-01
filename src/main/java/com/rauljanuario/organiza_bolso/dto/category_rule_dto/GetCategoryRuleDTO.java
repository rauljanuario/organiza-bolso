package com.rauljanuario.organiza_bolso.dto.category_rule_dto;

public record GetCategoryRuleDTO(

        Long id,
        String keyword,
        Long categoryId,
        Integer priority

) {
}
