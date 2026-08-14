import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiResponse, AuthResponse, UserRole } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const savedUser = localStorage.getItem('paycore_user');
    if (savedUser) {
      try {
        this.currentUserSubject.next(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('paycore_user');
      }
    }
  }

  public get currentUserValue(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  public get token(): string | null {
    return this.currentUserValue?.token || null;
  }

  public get userRole(): UserRole | null {
    return this.currentUserValue?.role || null;
  }

  public get employeeId(): number | undefined {
    return this.currentUserValue?.employeeId;
  }

  public isAdmin(): boolean {
    return this.userRole === 'ROLE_ADMIN';
  }

  public isEmployee(): boolean {
    return this.userRole === 'ROLE_EMPLOYEE';
  }

  login(email: string, password: String): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, { email, password })
      .pipe(
        tap(res => {
          if (res.success && res.data) {
            localStorage.setItem('paycore_user', JSON.stringify(res.data));
            this.currentUserSubject.next(res.data);
          }
        })
      );
  }

  logout(): void {
    localStorage.removeItem('paycore_user');
    this.currentUserSubject.next(null);
  }
}
