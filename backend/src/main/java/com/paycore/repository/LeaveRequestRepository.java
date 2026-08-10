package com.paycore.repository;

import com.paycore.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByAppliedAtDesc(Long employeeId);

    @Query("SELECT COALESCE(SUM(l.daysCount), 0) FROM LeaveRequest l WHERE " +
           "l.employee.id = :employeeId AND " +
           "l.leaveType = :leaveType AND " +
           "l.status = 'APPROVED' AND " +
           "l.startDate >= :startDate AND l.endDate <= :endDate")
    Integer countApprovedLeaveDaysByTypeAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("leaveType") LeaveRequest.LeaveType leaveType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
