package com.rauljanuario.organiza_bolso.controller;

import com.rauljanuario.organiza_bolso.dto.dashboard_dto.CategorySummaryDTO;
import com.rauljanuario.organiza_bolso.dto.dashboard_dto.SummaryDTO;
import com.rauljanuario.organiza_bolso.enums.CategoryType;
import com.rauljanuario.organiza_bolso.service.DashboardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @InjectMocks
    private DashboardController dashboardController;

    @Mock
    private DashboardService dashboardService;

    @Test
    @DisplayName("Return the summary for a month")
    void getSummary_ReturnsSummaryForMonth() {
        int month = 10;
        int year = 2026;
        SummaryDTO summary = new SummaryDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("35.90"),
                new BigDecimal("4964.10")
        );

        Mockito.when(dashboardService.getSummary(month, year)).thenReturn(summary);

        var result = dashboardController.getSummary(month, year);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(summary, result.getBody());
        Mockito.verify(dashboardService).getSummary(month, year);
    }

    @Test
    @DisplayName("Return category totals for a month and type")
    void getCategories_ReturnsCategoryTotalsForMonthAndType() {
        int month = 10;
        int year = 2026;
        CategoryType type = CategoryType.EXPENSE;
        List<CategorySummaryDTO> summaries = List.of(
                new CategorySummaryDTO(1L, "Alimentação", new BigDecimal("120.00"))
        );

        Mockito.when(dashboardService.getCategorySummary(month, year, type)).thenReturn(summaries);

        var result = dashboardController.getCategories(month, year, type);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals(summaries, result.getBody());
        Mockito.verify(dashboardService).getCategorySummary(month, year, type);
    }
}
