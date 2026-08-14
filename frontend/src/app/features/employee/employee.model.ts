import { UserRole } from '../../core/models/user.model';

export interface Employee {
  id?: number;
  employeeCode?: string;
  firstName: string;
  lastName: string;
  dob: string;
  mobileNumber: string;
  designation: string;
  department?: string;
  joiningDate?: string;
  email: string;
  password?: string;
  role?: UserRole;
}
