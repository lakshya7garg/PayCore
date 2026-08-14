import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LeaveService } from './leave.service';
import { AuthService } from '../../core/services/auth.service';
import { LeaveRequest, LeaveStatus, LeaveType } from './leave.model';

@Component({
  selector: 'app-leave-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './leave-management.component.html',
  styleUrls: ['./leave-management.component.css']
})
export class LeaveManagementComponent implements OnInit {
  leaveRequests: LeaveRequest[] = [];
  applyForm: FormGroup;
  reviewForm: FormGroup;

  isApplyModalOpen: boolean = false;
  isReviewModalOpen: boolean = false;
  selectedLeave?: LeaveRequest;

  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  leaveTypes: LeaveType[] = ['PAID', 'UNPAID', 'SICK', 'CASUAL'];

  constructor(
    private fb: FormBuilder,
    private leaveService: LeaveService,
    public authService: AuthService
  ) {
    const today = new Date().toISOString().split('T')[0];
    this.applyForm = this.fb.group({
      leaveType: ['PAID', Validators.required],
      startDate: [today, Validators.required],
      endDate: [today, Validators.required],
      reason: ['', [Validators.required, Validators.minLength(5)]]
    });

    this.reviewForm = this.fb.group({
      status: ['APPROVED', Validators.required],
      comments: ['']
    });
  }

  ngOnInit(): void {
    this.loadLeaveRequests();
  }

  loadLeaveRequests(): void {
    this.loading = true;
    if (this.authService.isAdmin()) {
      this.leaveService.getAllLeaveRequests().subscribe({
        next: (res) => {
          this.loading = false;
          if (res.success && res.data) {
            this.leaveRequests = res.data;
          }
        },
        error: () => this.loading = false
      });
    } else {
      this.leaveService.getMyLeaveRequests().subscribe({
        next: (res) => {
          this.loading = false;
          if (res.success && res.data) {
            this.leaveRequests = res.data;
          }
        },
        error: () => this.loading = false
      });
    }
  }

  openApplyModal(): void {
    this.errorMessage = '';
    this.successMessage = '';
    const today = new Date().toISOString().split('T')[0];
    this.applyForm.reset({
      leaveType: 'PAID',
      startDate: today,
      endDate: today,
      reason: ''
    });
    this.isApplyModalOpen = true;
  }

  closeApplyModal(): void {
    this.isApplyModalOpen = false;
  }

  onApplySubmit(): void {
    if (this.applyForm.invalid) {
      this.applyForm.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.leaveService.applyForLeave(this.applyForm.value).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = 'Leave request submitted successfully! Your manager has been notified.';
          this.closeApplyModal();
          this.loadLeaveRequests();
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to submit leave request.';
      }
    });
  }

  openReviewModal(leave: LeaveRequest): void {
    this.selectedLeave = leave;
    this.reviewForm.patchValue({
      status: 'APPROVED',
      comments: ''
    });
    this.isReviewModalOpen = true;
  }

  closeReviewModal(): void {
    this.isReviewModalOpen = false;
    this.selectedLeave = undefined;
  }

  onReviewSubmit(): void {
    if (!this.selectedLeave || !this.selectedLeave.id) return;

    const { status, comments } = this.reviewForm.value;

    this.leaveService.reviewLeaveRequest(this.selectedLeave.id, status, comments).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMessage = `Leave request marked as ${status}. Employee notified.`;
          this.closeReviewModal();
          this.loadLeaveRequests();
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to review leave request.';
      }
    });
  }
}
