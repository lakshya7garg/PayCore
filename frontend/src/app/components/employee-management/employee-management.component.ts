import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/common';
import { CommonModule } from '@angular/common';
import { EmployeeService } from '../../services/employee.service';
import { AuthService } from '../../services/auth.service';
import { Employee } from '../../models/employee.model';

@Component({
  selector: 'app-employee-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './employee-management.component.html',
  styleUrls: ['./employee-management.component.css']
})
export class EmployeeManagementComponent implements OnInit {
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  searchTerm: string = '';

  employeeForm: FormGroup;
  isModalOpen: boolean = false;
  isEditMode: boolean = false;
  selectedEmployeeId?: number;

  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  // Self profile state for regular employee role
  selfProfile?: Employee;

  designationOptions: string[] = [
    'Software Engineer',
    'Senior Software Engineer',
    'Tech Lead',
    'QA Engineer',
    'Product Manager',
    'HR Manager',
    'Accountant',
    'Financial Controller',
    'UI/UX Designer',
    'System Administrator'
  ];

  departmentOptions: string[] = [
    'Engineering',
    'Quality Assurance',
    'Human Resources',
    'Finance',
    'Product',
    'Executive'
  ];

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    public authService: AuthService
  ) {
    this.employeeForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      dob: ['', Validators.required],
      mobileNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      designation: ['', Validators.required],
      department: ['Engineering', Validators.required],
      joiningDate: [new Date().toISOString().split('T')[0], Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['Password123!'],
      role: ['ROLE_EMPLOYEE', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.authService.isAdmin() || this.authService.isAccountant()) {
      this.loadEmployees();
    } else {
      this.loadSelfProfile();
    }
  }

  loadEmployees(): void {
    this.loading = true;
    this.employeeService.getAllEmployees().subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success && res.data) {
          this.employees = res.data;
          this.filterEmployees();
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Failed to load employee list.';
      }
    });
  }

  loadSelfProfile(): void {
    this.employeeService.getSelfProfile().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.selfProfile = res.data;
        }
      }
    });
  }

  filterEmployees(): void {
    if (!this.searchTerm.trim()) {
      this.filteredEmployees = [...this.employees];
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredEmployees = this.employees.filter(e =>
      e.firstName.toLowerCase().includes(term) ||
      e.lastName.toLowerCase().includes(term) ||
      e.employeeCode?.toLowerCase().includes(term) ||
      e.mobileNumber.includes(term) ||
      e.designation.toLowerCase().includes(term) ||
      (e.department && e.department.toLowerCase().includes(term))
    );
  }

  openAddModal(): void {
    this.isEditMode = false;
    this.selectedEmployeeId = undefined;
    this.errorMessage = '';
    this.successMessage = '';
    this.employeeForm.reset({
      department: 'Engineering',
      joiningDate: new Date().toISOString().split('T')[0],
      password: 'Password123!',
      role: 'ROLE_EMPLOYEE'
    });
    this.isModalOpen = true;
  }

  openEditModal(emp: Employee): void {
    this.isEditMode = true;
    this.selectedEmployeeId = emp.id;
    this.errorMessage = '';
    this.successMessage = '';
    this.employeeForm.patchValue({
      firstName: emp.firstName,
      lastName: emp.lastName,
      dob: emp.dob,
      mobileNumber: emp.mobileNumber,
      designation: emp.designation,
      department: emp.department || 'Engineering',
      joiningDate: emp.joiningDate || new Date().toISOString().split('T')[0],
      email: emp.email,
      role: emp.role || 'ROLE_EMPLOYEE'
    });
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onSubmit(): void {
    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    const empData: Employee = this.employeeForm.value;

    if (this.isEditMode && this.selectedEmployeeId) {
      this.employeeService.updateEmployee(this.selectedEmployeeId, empData).subscribe({
        next: (res) => {
          if (res.success) {
            this.successMessage = 'Employee updated successfully!';
            this.closeModal();
            this.loadEmployees();
          } else {
            this.errorMessage = res.message;
          }
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error updating employee record.';
        }
      });
    } else {
      this.employeeService.createEmployee(empData).subscribe({
        next: (res) => {
          if (res.success) {
            this.successMessage = 'Employee created successfully! Notification sent to new employee.';
            this.closeModal();
            this.loadEmployees();
          } else {
            this.errorMessage = res.message;
          }
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Failed to add employee. Verify mobile number uniqueness.';
        }
      });
    }
  }

  deleteEmployee(emp: Employee): void {
    if (confirm(`Are you sure you want to delete employee ${emp.firstName} ${emp.lastName}?`)) {
      if (emp.id) {
        this.employeeService.deleteEmployee(emp.id).subscribe({
          next: (res) => {
            if (res.success) {
              this.loadEmployees();
            }
          },
          error: (err) => {
            alert('Failed to delete employee: ' + (err.error?.message || 'Server error'));
          }
        });
      }
    }
  }
}
