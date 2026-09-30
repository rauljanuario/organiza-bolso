package com.rauljanuario.organiza_bolso.repository;

import com.rauljanuario.organiza_bolso.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser_Id(Long userId);
    Optional<Category> findByIdAndUser_Id(Long id, Long userId);

}
