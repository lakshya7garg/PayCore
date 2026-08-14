export type UserRole = 'ROLE_ADMIN' | 'ROLE_EMPLOYEE';

export interface User {
  id: number;
  email: string;
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  type: string;
  userId: number;
  email: string;
  role: UserRole;
  employeeId?: number;
  employeeCode?: string;
  fullName: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
