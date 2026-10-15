package com.rauljanuario.organiza_bolso.repository;

import com.rauljanuario.organiza_bolso.model.StatementImport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementImportRepository extends JpaRepository<StatementImport, Long> {

    Optional<StatementImport> findByIdAndUser_Id(Long id, Long userId);

    List<StatementImport> findByUser_IdOrderByImportedAtDesc(Long userId);

}
