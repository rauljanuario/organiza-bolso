package com.rauljanuario.organiza_bolso.repository;

import com.rauljanuario.organiza_bolso.model.CategoryRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRuleRepository extends JpaRepository<CategoryRule, Long> {

    List<CategoryRule> findByCategoryId(Long categoryId);

}
