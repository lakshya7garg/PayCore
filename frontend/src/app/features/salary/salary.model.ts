export interface SalaryStructure {
  id?: number;
  employeeId: number;
  employeeName?: string;
  employeeCode?: string;
  basicSalary: number;
  hra: number;
  allowances: number;
  medicalAllowance: number;
  pfDeduction: number;
  taxDeduction: number;
  totalGross?: number;
  totalNet?: number;
}

export interface Payslip {
  id: number;
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  designation: string;
  department: string;
  month: number;
  year: number;
  monthName: string;
  basicSalary: number;
  hra: number;
  allowances: number;
  grossSalary: number;
  pfDeduction: number;
  taxDeduction: number;
  unpaidLeaveDays: number;
  unpaidLeaveDeduction: number;
  totalDeductions: number;
  netPay: number;
  generatedAt: string;
}
