import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../core/models/user.model';
import { Payslip, SalaryStructure } from './salary.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SalaryService {
  private apiUrl = `${environment.apiUrl}/salary`;

  constructor(private http: HttpClient) {}

  getAllSalaryStructures(): Observable<ApiResponse<SalaryStructure[]>> {
    return this.http.get<ApiResponse<SalaryStructure[]>>(`${this.apiUrl}/structures`);
  }

  getSalaryStructureByEmployee(employeeId: number): Observable<ApiResponse<SalaryStructure>> {
    return this.http.get<ApiResponse<SalaryStructure>>(`${this.apiUrl}/structures/employee/${employeeId}`);
  }

  saveSalaryStructure(structure: SalaryStructure): Observable<ApiResponse<SalaryStructure>> {
    return this.http.post<ApiResponse<SalaryStructure>>(`${this.apiUrl}/structures`, structure);
  }

  generatePayslip(employeeId: number, month: number, year: number): Observable<ApiResponse<Payslip>> {
    let params = new HttpParams()
      .set('employeeId', employeeId.toString())
      .set('month', month.toString())
      .set('year', year.toString());
    return this.http.post<ApiResponse<Payslip>>(`${this.apiUrl}/payslips/generate`, null, { params });
  }

  getMyPayslips(): Observable<ApiResponse<Payslip[]>> {
    return this.http.get<ApiResponse<Payslip[]>>(`${this.apiUrl}/payslips/my`);
  }

  getEmployeePayslips(employeeId: number): Observable<ApiResponse<Payslip[]>> {
    return this.http.get<ApiResponse<Payslip[]>>(`${this.apiUrl}/payslips/employee/${employeeId}`);
  }

  getPayslipById(id: number): Observable<ApiResponse<Payslip>> {
    return this.http.get<ApiResponse<Payslip>>(`${this.apiUrl}/payslips/${id}`);
  }
}
