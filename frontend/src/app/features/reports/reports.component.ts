import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReportService } from './report.service';
import { EmployeeService } from '../employee/employee.service';
import { AuthService } from '../../core/services/auth.service';
import { Payslip } from '../salary/salary.model';
import { Employee } from '../employee/employee.model';
import { ReportFilter } from './report.model';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  filterForm: FormGroup;
  employees: Employee[] = [];
  reportResults: Payslip[] = [];

  loading: boolean = false;
  downloadingCsv: boolean = false;
  downloadingPdf: boolean = false;
  errorMessage: string = '';

  monthOptions = [
    { value: null, label: 'All Months' },
    { value: 1, label: 'January' },
    { value: 2, label: 'February' },
    { value: 3, label: 'March' },
    { value: 4, label: 'April' },
    { value: 5, label: 'May' },
    { value: 6, label: 'June' },
    { value: 7, label: 'July' },
    { value: 8, label: 'August' },
    { value: 9, label: 'September' },
    { value: 10, label: 'October' },
    { value: 11, label: 'November' },
    { value: 12, label: 'December' }
  ];

  yearOptions = [null, 2024, 2025, 2026, 2027];

  constructor(
    private fb: FormBuilder,
    private reportService: ReportService,
    private employeeService: EmployeeService,
    public authService: AuthService
  ) {
    this.filterForm = this.fb.group({
      month: [null],
      year: [null],
      fromDate: [null],
      toDate: [null],
      employeeId: [null]
    });
  }

  ngOnInit(): void {
    this.loadEmployees();
    this.onGenerateReport();
  }

  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe(res => {
      if (res.success && res.data) {
        this.employees = res.data;
      }
    });
  }

  getFilterPayload(): ReportFilter {
    const raw = this.filterForm.value;
    return {
      month: raw.month ? Number(raw.month) : null,
      year: raw.year ? Number(raw.year) : null,
      fromDate: raw.fromDate || null,
      toDate: raw.toDate || null,
      employeeId: raw.employeeId ? Number(raw.employeeId) : null
    };
  }

  onGenerateReport(): void {
    this.loading = true;
    this.errorMessage = '';
    const filter = this.getFilterPayload();

    this.reportService.filterReportData(filter).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success && res.data) {
          this.reportResults = res.data;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Failed to generate report data.';
      }
    });
  }

  resetFilters(): void {
    this.filterForm.reset({
      month: null,
      year: null,
      fromDate: null,
      toDate: null,
      employeeId: null
    });
    this.onGenerateReport();
  }

  downloadCsv(): void {
    this.downloadingCsv = true;
    const filter = this.getFilterPayload();

    this.reportService.downloadCsvReport(filter).subscribe({
      next: (blob) => {
        this.downloadingCsv = false;
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'PayCore_Employee_Salary_Report.csv';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.downloadingCsv = false;
        alert('Error downloading CSV report.');
      }
    });
  }

  downloadPdf(): void {
    this.downloadingPdf = true;
    const filter = this.getFilterPayload();

    this.reportService.downloadPdfReport(filter).subscribe({
      next: (blob) => {
        this.downloadingPdf = false;
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'PayCore_Employee_Salary_Report.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.downloadingPdf = false;
        alert('Error downloading PDF report.');
      }
    });
  }
}
