import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SalaryService } from './salary.service';
import { EmployeeService } from '../employee/employee.service';
import { AuthService } from '../../core/services/auth.service';
import { Payslip, SalaryStructure } from './salary.model';
import { Employee } from '../employee/employee.model';

@Component({
  selector: 'app-salary-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './salary-management.component.html',
  styleUrls: ['./salary-management.component.css']
})
export class SalaryManagementComponent implements OnInit {
  activeTab: 'structures' | 'payslips' | 'generate' = 'payslips';

  salaryStructures: SalaryStructure[] = [];
  employees: Employee[] = [];
  payslips: Payslip[] = [];

  structureForm: FormGroup;
  generateForm: FormGroup;

  isStructureModalOpen: boolean = false;
  isPayslipModalOpen: boolean = false;
  selectedPayslip?: Payslip;

  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  monthOptions = [
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

  yearOptions = [2024, 2025, 2026, 2027];

  constructor(
    private fb: FormBuilder,
    private salaryService: SalaryService,
    private employeeService: EmployeeService,
    public authService: AuthService
  ) {
    this.structureForm = this.fb.group({
      employeeId: ['', Validators.required],
      basicSalary: [5000, [Validators.required, Validators.min(0)]],
      hra: [1500, [Validators.required, Validators.min(0)]],
      allowances: [1000, [Validators.required, Validators.min(0)]],
      medicalAllowance: [300, [Validators.required, Validators.min(0)]],
      pfDeduction: [400, [Validators.required, Validators.min(0)]],
      taxDeduction: [500, [Validators.required, Validators.min(0)]]
    });

    const now = new Date();
    this.generateForm = this.fb.group({
      employeeId: ['', Validators.required],
      month: [now.getMonth() + 1, Validators.required],
      year: [now.getFullYear(), Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.authService.isAdmin()) {
      this.activeTab = 'structures';
      this.loadSalaryStructures();
      this.loadEmployees();
      this.loadAllPayslips();
    } else {
      this.activeTab = 'payslips';
      this.loadMyPayslips();
    }
  }

  loadSalaryStructures(): void {
    this.salaryService.getAllSalaryStructures().subscribe(res => {
      if (res.success && res.data) {
        this.salaryStructures = res.data;
      }
    });
  }

  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe(res => {
      if (res.success && res.data) {
        this.employees = res.data;
      }
    });
  }

  loadAllPayslips(): void {
    // If employees exist, load first employee's payslips as default sample
    this.employeeService.getAllEmployees().subscribe(res => {
      if (res.success && res.data && res.data.length > 0) {
        const empId = res.data[0].id!;
        this.salaryService.getEmployeePayslips(empId).subscribe(pRes => {
          if (pRes.success && pRes.data) {
            this.payslips = pRes.data;
          }
        });
      }
    });
  }

  loadMyPayslips(): void {
    this.salaryService.getMyPayslips().subscribe(res => {
      if (res.success && res.data) {
        this.payslips = res.data;
      }
    });
  }

  onEmployeeSelectForPayslips(empIdEvent: Event): void {
    const target = empIdEvent.target as HTMLSelectElement;
    const empId = Number(target.value);
    if (empId) {
      this.salaryService.getEmployeePayslips(empId).subscribe(res => {
        if (res.success && res.data) {
          this.payslips = res.data;
        }
      });
    }
  }

  openStructureModal(structure?: SalaryStructure): void {
    this.errorMessage = '';
    this.successMessage = '';
    if (structure) {
      this.structureForm.patchValue(structure);
    } else {
      this.structureForm.reset({
        basicSalary: 5000,
        hra: 1500,
        allowances: 1000,
        medicalAllowance: 300,
        pfDeduction: 400,
        taxDeduction: 500
      });
    }
    this.isStructureModalOpen = true;
  }

  closeStructureModal(): void {
    this.isStructureModalOpen = false;
  }

  onSaveStructure(): void {
    if (this.structureForm.invalid) {
      this.structureForm.markAllAsTouched();
      return;
    }

    this.salaryService.saveSalaryStructure(this.structureForm.value).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = 'Salary structure saved successfully!';
          this.closeStructureModal();
          this.loadSalaryStructures();
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error saving structure.';
      }
    });
  }

  onGeneratePayslip(): void {
    if (this.generateForm.invalid) {
      this.generateForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const { employeeId, month, year } = this.generateForm.value;

    this.salaryService.generatePayslip(employeeId, month, year).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success && res.data) {
          this.successMessage = `Payslip generated for ${res.data.employeeName} (${res.data.monthName} ${res.data.year}). Unpaid Leave Deduction: ₹${res.data.unpaidLeaveDeduction}`;
          this.openPayslipModal(res.data);
          this.salaryService.getEmployeePayslips(employeeId).subscribe(pRes => {
            if (pRes.success && pRes.data) {
              this.payslips = pRes.data;
            }
          });
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Error generating payslip. Ensure salary structure is configured.';
      }
    });
  }

  openPayslipModal(payslip: Payslip): void {
    this.selectedPayslip = payslip;
    this.isPayslipModalOpen = true;
  }

  closePayslipModal(): void {
    this.isPayslipModalOpen = false;
    this.selectedPayslip = undefined;
  }

  printPayslip(): void {
    window.print();
  }
}
