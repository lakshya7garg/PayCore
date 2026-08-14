import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { EmployeeManagementComponent } from './employee-management.component';
import { EmployeeService } from './employee.service';
import { AuthService } from '../../core/services/auth.service';

describe('EmployeeManagementComponent', () => {
  let component: EmployeeManagementComponent;
  let fixture: ComponentFixture<EmployeeManagementComponent>;
  let employeeServiceSpy: jasmine.SpyObj<EmployeeService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    employeeServiceSpy = jasmine.createSpyObj('EmployeeService', ['getAllEmployees', 'createEmployee', 'getSelfProfile']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isAdmin', 'isEmployee']);

    employeeServiceSpy.getAllEmployees.and.returnValue(of({
      success: true,
      message: 'OK',
      data: [
        {
          id: 1,
          employeeCode: 'EMP-1001',
          firstName: 'Alice',
          lastName: 'Smith',
          dob: '1990-01-01',
          mobileNumber: '9876543210',
          designation: 'Developer',
          department: 'Engineering',
          email: 'alice@paycore.com'
        }
      ]
    }));

    authServiceSpy.isAdmin.and.returnValue(true);

    await TestBed.configureTestingModule({
      imports: [EmployeeManagementComponent, ReactiveFormsModule, FormsModule],
      providers: [
        { provide: EmployeeService, useValue: employeeServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create employee management component and load employees', () => {
    expect(component).toBeTruthy();
    expect(component.employees.length).toBe(1);
    expect(component.employees[0].firstName).toBe('Alice');
  });

  it('should filter employees based on search term', () => {
    component.searchTerm = 'Alice';
    component.filterEmployees();
    expect(component.filteredEmployees.length).toBe(1);

    component.searchTerm = 'NonExistent';
    component.filterEmployees();
    expect(component.filteredEmployees.length).toBe(0);
  });
});
