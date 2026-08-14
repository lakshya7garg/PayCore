export type LeaveType = 'PAID' | 'UNPAID' | 'SICK' | 'CASUAL';
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface LeaveRequest {
  id?: number;
  employeeId?: number;
  employeeName?: string;
  employeeCode?: string;
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  daysCount?: number;
  reason: string;
  status?: LeaveStatus;
  managerComments?: string;
  appliedAt?: string;
  reviewedAt?: string;
}
