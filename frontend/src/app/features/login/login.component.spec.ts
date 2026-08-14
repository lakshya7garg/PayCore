import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the login component', () => {
    expect(component).toBeTruthy();
  });

  it('should validate invalid email and empty fields', () => {
    component.loginForm.controls['email'].setValue('invalid-email');
    component.loginForm.controls['password'].setValue('');
    expect(component.loginForm.valid).toBeFalse();
  });

  it('should navigate to dashboard on successful login', () => {
    component.loginForm.controls['email'].setValue('admin@paycore.com');
    component.loginForm.controls['password'].setValue('Password123!');

    authServiceSpy.login.and.returnValue(of({
      success: true,
      message: 'Login success',
      data: {
        token: 'fake-jwt',
        type: 'Bearer',
        userId: 1,
        email: 'admin@paycore.com',
        role: 'ROLE_ADMIN',
        fullName: 'Admin User'
      }
    }));

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith('admin@paycore.com', 'Password123!');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should display error message on invalid login', () => {
    component.loginForm.controls['email'].setValue('wrong@paycore.com');
    component.loginForm.controls['password'].setValue('wrongpass');

    authServiceSpy.login.and.returnValue(throwError(() => ({
      error: { message: 'Invalid credentials' }
    })));

    component.onSubmit();

    expect(component.errorMessage).toBe('Invalid credentials');
  });
});
