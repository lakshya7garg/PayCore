package com.paycore.controller;

import com.paycore.dto.ApiResponse;
import com.paycore.dto.PayslipDto;
import com.paycore.dto.ReportFilterDto;
import com.paycore.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/employee-salary/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PayslipDto>>> getFilteredReportData(@RequestBody ReportFilterDto filter) {
        List<PayslipDto> data = reportService.filterReportData(filter);
        return ResponseEntity.ok(new ApiResponse<>(true, "Report data retrieved", data));
    }

    @PostMapping("/employee-salary/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadCsvReport(@RequestBody ReportFilterDto filter) {
        try {
            byte[] csvBytes = reportService.generateCsvReport(filter);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_salary_report.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/employee-salary/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadPdfReport(@RequestBody ReportFilterDto filter) {
        try {
            byte[] pdfBytes = reportService.generatePdfReport(filter);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_salary_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
