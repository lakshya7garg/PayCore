import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { EmployeeManagementComponent } from './features/employee/employee-management.component';
import { SalaryManagementComponent } from './features/salary/salary-management.component';
import { LeaveManagementComponent } from './features/leave/leave-management.component';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'employees', component: EmployeeManagementComponent, canActivate: [AuthGuard] },
  { path: 'salary', component: SalaryManagementComponent, canActivate: [AuthGuard] },
  { path: 'leaves', component: LeaveManagementComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' }
];
