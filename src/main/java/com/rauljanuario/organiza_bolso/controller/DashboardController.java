package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.dashboard_dto.CategorySummaryDTO;
import com.rauljanuario.organiza_bolso.dto.dashboard_dto.SummaryDTO;
import com.rauljanuario.organiza_bolso.enums.CategoryType;
import com.rauljanuario.organiza_bolso.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> getSummary(
            @RequestParam("month") int month,
            @RequestParam("year") int year
    ) {
        return ResponseEntity.ok(dashboardService.getSummary(month, year));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategorySummaryDTO>> getCategories(
            @RequestParam("month") int month,
            @RequestParam("year") int year,
            @RequestParam("type") CategoryType type
    ) {
        return ResponseEntity.ok(dashboardService.getCategorySummary(month, year, type));
    }
}
