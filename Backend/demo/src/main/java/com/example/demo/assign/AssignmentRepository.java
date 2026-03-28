package com.example.demo.assign;

import com.example.demo.assign.Assignment;
import com.example.demo.assign.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByTechnicianIdOrderByScheduledDateAsc(Long technicianId);

    List<Assignment> findByTechnicianIdAndStatus(Long technicianId, AssignmentStatus status);

    List<Assignment> findByTechnicianIdAndScheduledDateBetween(
            Long technicianId,
            LocalDateTime start,
            LocalDateTime end);


    List<Assignment> findByDemandeIdOrderByCreatedAtDesc(Long demandeId);


    boolean existsByTechnicianIdAndScheduledDate(Long technicianId, LocalDateTime scheduledDate);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.technician.id = :technicianId " +
            "AND DATE(a.scheduledDate) = DATE(:date)")
    long countByTechnicianIdAndDate(@Param("technicianId") Long technicianId,
                                    @Param("date") LocalDateTime date);

    long countByTechnicianIdAndScheduledDateBetween(
            Long technicianId,
            LocalDateTime start,
            LocalDateTime end);



    List<Assignment> findByScheduledDateBetween(LocalDateTime start, LocalDateTime end);

    List<Assignment> findByStatus(AssignmentStatus status);


    long countByStatus(AssignmentStatus status);

    long countByTechnicianId(Long technicianId);

    long countByTechnicianIdAndStatus(Long technicianId, AssignmentStatus status);



    @Query("SELECT a FROM Assignment a WHERE " +
            "(:technicianId IS NULL OR a.technician.id = :technicianId) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:startDate IS NULL OR a.scheduledDate >= :startDate) AND " +
            "(:endDate IS NULL OR a.scheduledDate <= :endDate)")
    List<Assignment> searchAssignments(@Param("technicianId") Long technicianId,
                                       @Param("status") AssignmentStatus status,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
}