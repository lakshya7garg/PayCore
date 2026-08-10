import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { EmployeeManagementComponent } from './components/employee-management/employee-management.component';
import { SalaryManagementComponent } from './components/salary-management/salary-management.component';
import { LeaveManagementComponent } from './components/leave-management/leave-management.component';
import { ReportsComponent } from './components/reports/reports.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'employees', component: EmployeeManagementComponent, canActivate: [AuthGuard] },
  { path: 'salary', component: SalaryManagementComponent, canActivate: [AuthGuard] },
  { path: 'leaves', component: LeaveManagementComponent, canActivate: [AuthGuard] },
  { 
    path: 'reports', 
    component: ReportsComponent, 
    canActivate: [AuthGuard, RoleGuard], 
    data: { roles: ['ROLE_ACCOUNTANT', 'ROLE_ADMIN'] } 
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
