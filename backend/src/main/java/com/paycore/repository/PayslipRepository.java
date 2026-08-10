package com.paycore.repository;

import com.paycore.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByEmployeeIdOrderByYearDescMonthDesc(Long employeeId);
    Optional<Payslip> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    @Query("SELECT p FROM Payslip p WHERE " +
           "(:employeeId IS NULL OR p.employee.id = :employeeId) AND " +
           "(:month IS NULL OR p.month = :month) AND " +
           "(:year IS NULL OR p.year = :year) AND " +
           "(:startDate IS NULL OR p.generatedAt >= :startDate) AND " +
           "(:endDate IS NULL OR p.generatedAt <= :endDate) " +
           "ORDER BY p.year DESC, p.month DESC")
    List<Payslip> filterPayslips(
            @Param("employeeId") Long employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
