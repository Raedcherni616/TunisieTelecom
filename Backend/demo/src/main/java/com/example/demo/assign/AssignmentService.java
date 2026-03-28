package com.example.demo.assign;

import com.example.demo.assign.Assignment;
import com.example.demo.assign.AssignmentStatus;
import com.example.demo.demande.Demande;
import com.example.demo.technician.Technician;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentService {

    Assignment createAssignment(Demande demande, Technician technician, LocalDateTime scheduledDate);

    boolean tryAutoAssignTechnician(Demande demande);

    boolean tryAssignTechnician(Demande demande, LocalDateTime selectedDate);

    Assignment startAssignment(Long assignmentId);

    Assignment completeAssignment(Long assignmentId, String completionPhoto);

    Assignment failAssignment(Long assignmentId, String reason);

    Assignment cancelAssignment(Long assignmentId);

    List<Assignment> getAssignmentsByTechnician(Long technicianId);

    List<Assignment> getAssignmentsByDemande(Long demandeId);

    Optional<Assignment> getCurrentAssignmentByDemande(Long demandeId);

    Optional<Assignment> getAssignmentById(Long id);

    boolean isTechnicianAvailable(Technician technician, LocalDateTime dateTime, int estimatedDuration);

    long countAssignmentsByTechnicianAndDate(Long technicianId, LocalDate date);

    long countByStatus(AssignmentStatus status);

    long countByTechnician(Long technicianId);

    Double getAverageCompletionTime();
    Optional<Technician> findBestTechnician(String city, LocalDateTime dateTime, int estimatedDuration);

    List<LocalDateTime> findAlternativeSlots(String city, LocalDateTime preferredDate,
                                             int estimatedDuration, int daysToSearch);
}