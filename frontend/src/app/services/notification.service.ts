import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/user.model';
import { NotificationItem } from '../models/notification.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getMyNotifications(): Observable<ApiResponse<NotificationItem[]>> {
    return this.http.get<ApiResponse<NotificationItem[]>>(this.apiUrl);
  }

  markAsRead(id: number): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.apiUrl}/${id}/read`, null);
  }
}
