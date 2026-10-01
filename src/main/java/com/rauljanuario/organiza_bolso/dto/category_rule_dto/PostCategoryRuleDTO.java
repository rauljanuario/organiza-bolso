package com.rauljanuario.organiza_bolso.dto.category_rule_dto;

public record PostCategoryRuleDTO(

        Long id,
        String keyword,
        Long categoryId,
        Integer priority

) {
}
