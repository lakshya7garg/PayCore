import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { NotificationService } from './services/notification.service';
import { NotificationItem } from './models/notification.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'PayCore';
  notifications: NotificationItem[] = [];
  isNotificationMenuOpen: boolean = false;
  unreadCount: number = 0;

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.loadNotifications();
      } else {
        this.notifications = [];
        this.unreadCount = 0;
      }
    });
  }

  loadNotifications(): void {
    this.notificationService.getMyNotifications().subscribe(res => {
      if (res.success && res.data) {
        this.notifications = res.data;
        this.unreadCount = res.data.filter(n => !n.isRead).length;
      }
    });
  }

  toggleNotifications(): void {
    this.isNotificationMenuOpen = !this.isNotificationMenuOpen;
    if (this.isNotificationMenuOpen) {
      this.loadNotifications();
    }
  }

  markRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe(() => {
      this.loadNotifications();
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
