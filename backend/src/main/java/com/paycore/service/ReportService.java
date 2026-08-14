package com.paycore.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.opencsv.CSVWriter;
import com.paycore.dto.PayslipDto;
import com.paycore.dto.ReportFilterDto;
import com.paycore.entity.Payslip;
import com.paycore.repository.PayslipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private SalaryService salaryService;

    public List<PayslipDto> filterReportData(ReportFilterDto filter) {
        LocalDateTime startDateTime = (filter.getFromDate() != null) ? filter.getFromDate().atStartOfDay() : null;
        LocalDateTime endDateTime = (filter.getToDate() != null) ? filter.getToDate().atTime(LocalTime.MAX) : null;

        List<Payslip> payslips = payslipRepository.filterPayslips(
                filter.getEmployeeId(),
                filter.getMonth(),
                filter.getYear(),
                startDateTime,
                endDateTime
        );

        return payslips.stream()
                .map(salaryService::mapPayslipToDto)
                .collect(Collectors.toList());
    }

    public byte[] generateCsvReport(ReportFilterDto filter) throws Exception {
        List<PayslipDto> list = filterReportData(filter);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

        // Header
        writer.writeNext(new String[]{
                "Payslip ID", "Employee Code", "Employee Name", "Designation", "Department",
                "Month", "Year", "Gross Salary (₹)", "PF Deduction (₹)", "Tax Deduction (₹)",
                "Unpaid Leave Days", "Unpaid Leave Deduction (₹)", "Total Deductions (₹)", "Net Pay (₹)"
        });

        // Data rows
        for (PayslipDto p : list) {
            writer.writeNext(new String[]{
                    String.valueOf(p.getId()),
                    p.getEmployeeCode(),
                    p.getEmployeeName(),
                    p.getDesignation(),
                    p.getDepartment(),
                    p.getMonthName(),
                    String.valueOf(p.getYear()),
                    p.getGrossSalary().toString(),
                    p.getPfDeduction().toString(),
                    p.getTaxDeduction().toString(),
                    String.valueOf(p.getUnpaidLeaveDays()),
                    p.getUnpaidLeaveDeduction().toString(),
                    p.getTotalDeductions().toString(),
                    p.getNetPay().toString()
            });
        }

        writer.flush();
        writer.close();
        return out.toByteArray();
    }

    public byte[] generatePdfReport(ReportFilterDto filter) throws Exception {
        List<PayslipDto> list = filterReportData(filter);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        Paragraph title = new Paragraph("PayCore - Employee Salary & Payroll Summary Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph meta = new Paragraph("Generated on: " + LocalDateTime.now().toString() + " | Total Records: " + list.size(), subTitleFont);
        meta.setAlignment(Element.ALIGN_CENTER);
        meta.setSpacingAfter(15);
        document.add(meta);

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2.2f, 1.8f, 1.2f, 1.0f, 1.5f, 1.5f, 1.5f, 1.5f, 1.6f});

        String[] headers = {"Emp Code", "Employee Name", "Designation", "Month/Year", "Leave Days", "Gross Pay", "PF Ded.", "Tax Ded.", "Leave Ded.", "Net Pay"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
            cell.setBackgroundColor(new Color(40, 50, 70));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        boolean alternate = false;
        for (PayslipDto p : list) {
            Color bg = alternate ? new Color(245, 247, 250) : Color.WHITE;
            alternate = !alternate;

            addCell(table, p.getEmployeeCode(), tableCellFont, bg, Element.ALIGN_CENTER);
            addCell(table, p.getEmployeeName(), tableCellFont, bg, Element.ALIGN_LEFT);
            addCell(table, p.getDesignation(), tableCellFont, bg, Element.ALIGN_LEFT);
            addCell(table, p.getMonthName() + " " + p.getYear(), tableCellFont, bg, Element.ALIGN_CENTER);
            addCell(table, String.valueOf(p.getUnpaidLeaveDays()), tableCellFont, bg, Element.ALIGN_CENTER);
            addCell(table, "₹" + p.getGrossSalary(), tableCellFont, bg, Element.ALIGN_RIGHT);
            addCell(table, "₹" + p.getPfDeduction(), tableCellFont, bg, Element.ALIGN_RIGHT);
            addCell(table, "₹" + p.getTaxDeduction(), tableCellFont, bg, Element.ALIGN_RIGHT);
            addCell(table, "₹" + p.getUnpaidLeaveDeduction(), tableCellFont, bg, Element.ALIGN_RIGHT);
            addCell(table, "₹" + p.getNetPay(), tableCellFont, bg, Element.ALIGN_RIGHT);
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }
}
