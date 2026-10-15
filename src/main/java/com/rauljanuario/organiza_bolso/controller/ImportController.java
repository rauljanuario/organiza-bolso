package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.import_dto.ImportResponseDTO;
import com.rauljanuario.organiza_bolso.service.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ImportResponseDTO> importStatement(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(importService.importStatement(file));
    }

    @GetMapping
    public ResponseEntity<List<ImportResponseDTO>> getImports() {
        return ResponseEntity.ok(importService.getImports());
    }
}
