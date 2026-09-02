package com.rauljanuario.organiza_bolso.repository;

import com.rauljanuario.organiza_bolso.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

}
