import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/user.model';
import { LeaveRequest, LeaveStatus } from '../models/leave.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LeaveService {
  private apiUrl = `${environment.apiUrl}/leaves`;

  constructor(private http: HttpClient) {}

  getAllLeaveRequests(): Observable<ApiResponse<LeaveRequest[]>> {
    return this.http.get<ApiResponse<LeaveRequest[]>>(this.apiUrl);
  }

  getMyLeaveRequests(): Observable<ApiResponse<LeaveRequest[]>> {
    return this.http.get<ApiResponse<LeaveRequest[]>>(`${this.apiUrl}/my`);
  }

  applyForLeave(leaveRequest: LeaveRequest): Observable<ApiResponse<LeaveRequest>> {
    return this.http.post<ApiResponse<LeaveRequest>>(`${this.apiUrl}/apply`, leaveRequest);
  }

  reviewLeaveRequest(id: number, status: LeaveStatus, comments?: string): Observable<ApiResponse<LeaveRequest>> {
    let params = new HttpParams().set('status', status);
    if (comments) {
      params = params.set('comments', comments);
    }
    return this.http.put<ApiResponse<LeaveRequest>>(`${this.apiUrl}/${id}/review`, null, { params });
  }
}
