import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../core/models/user.model';
import { Payslip } from '../salary/salary.model';
import { ReportFilter } from './report.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports/employee-salary`;

  constructor(private http: HttpClient) {}

  filterReportData(filter: ReportFilter): Observable<ApiResponse<Payslip[]>> {
    return this.http.post<ApiResponse<Payslip[]>>(`${this.apiUrl}/filter`, filter);
  }

  downloadCsvReport(filter: ReportFilter): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/csv`, filter, { responseType: 'blob' });
  }

  downloadPdfReport(filter: ReportFilter): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/pdf`, filter, { responseType: 'blob' });
  }
}
