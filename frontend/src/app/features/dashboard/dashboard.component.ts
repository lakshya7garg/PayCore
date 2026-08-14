import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { EmployeeService } from '../employee/employee.service';
import { LeaveService } from '../leave/leave.service';
import { SalaryService } from '../salary/salary.service';
import { Employee } from '../employee/employee.model';
import { LeaveRequest } from '../leave/leave.model';
import { Payslip } from '../salary/salary.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  role: string | null = '';
  fullName: string = '';
  employeeCode: string = '';

  // Stats
  totalEmployees: number = 0;
  pendingLeavesCount: number = 0;
  myLeaveCount: number = 0;
  myPayslipsCount: number = 0;

  // Recent data
  recentEmployees: Employee[] = [];
  recentLeaves: LeaveRequest[] = [];
  myPayslips: Payslip[] = [];
  selfProfile?: Employee;

  constructor(
    public authService: AuthService,
    private employeeService: EmployeeService,
    private leaveService: LeaveService,
    private salaryService: SalaryService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    if (user) {
      this.role = user.role;
      this.fullName = user.fullName;
      this.employeeCode = user.employeeCode || '';
    }

    if (this.authService.isAdmin()) {
      this.loadAdminDashboardData();
    } else {
      this.loadEmployeeDashboardData();
    }
  }

  loadAdminDashboardData(): void {
    this.employeeService.getAllEmployees().subscribe(res => {
      if (res.success && res.data) {
        this.totalEmployees = res.data.length;
        this.recentEmployees = res.data.slice(0, 5);
      }
    });

    this.leaveService.getAllLeaveRequests().subscribe(res => {
      if (res.success && res.data) {
        this.recentLeaves = res.data.slice(0, 5);
        this.pendingLeavesCount = res.data.filter(l => l.status === 'PENDING').length;
      }
    });
  }

  loadEmployeeDashboardData(): void {
    this.employeeService.getSelfProfile().subscribe(res => {
      if (res.success && res.data) {
        this.selfProfile = res.data;
      }
    });

    this.leaveService.getMyLeaveRequests().subscribe(res => {
      if (res.success && res.data) {
        this.recentLeaves = res.data.slice(0, 5);
        this.myLeaveCount = res.data.length;
      }
    });

    this.salaryService.getMyPayslips().subscribe(res => {
      if (res.success && res.data) {
        this.myPayslips = res.data;
        this.myPayslipsCount = res.data.length;
      }
    });
  }
}
