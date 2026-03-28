package com.example.demo.assign;

import com.example.demo.Holidays.DaysOfWeek;
import com.example.demo.Holidays.HolidayService;
import com.example.demo.Holidays.TechnicianLeaveService;
import com.example.demo.demande.Demande;
import com.example.demo.demande.DemandeRepository;
import com.example.demo.demande.DemandeStatus;
import com.example.demo.technician.Technician;
import com.example.demo.technician.TechnicianService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final DemandeRepository demandeRepository;
    private final TechnicianService technicianService;
    private final HolidayService holidayService;
    private final TechnicianLeaveService leaveService;

    // ========== 1. إنشاء التعيينات ==========

    @Override
    @Transactional
    public Assignment createAssignment(Demande demande, Technician technician, LocalDateTime scheduledDate) {
        if (demande == null || technician == null || scheduledDate == null) {
            throw new IllegalArgumentException("Demande, technician and scheduled date cannot be null");
        }

        if (scheduledDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled date cannot be in the past");
        }

        Assignment assignment = Assignment.builder()
                .demande(demande)
                .technician(technician)
                .scheduledDate(scheduledDate)
                .estimatedDuration(demande.getService().getEstimatedDuration())
                .status(AssignmentStatus.ASSIGNED)
                .build();

        assignment = assignmentRepository.save(assignment);

        demande.setCurrentAssignment(assignment);
        demande.setStatus(DemandeStatus.CONFIRMED);
        demandeRepository.save(demande);

        return assignment;
    }

    @Override
    @Transactional
    public boolean tryAutoAssignTechnician(Demande demande) {
        Optional<Technician> bestTechnician = findBestTechnician(
                demande.getCity(),
                demande.getPreferredDate(),
                demande.getService().getEstimatedDuration()
        );

        if (bestTechnician.isPresent()) {
            createAssignment(demande, bestTechnician.get(), demande.getPreferredDate());
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean tryAssignTechnician(Demande demande, LocalDateTime selectedDate) {
        Optional<Technician> technician = findBestTechnician(
                demande.getCity(),
                selectedDate,
                demande.getService().getEstimatedDuration()
        );

        if (technician.isPresent()) {
            createAssignment(demande, technician.get(), selectedDate);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Assignment startAssignment(Long assignmentId) {
        Assignment assignment = findAssignmentById(assignmentId);

        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new IllegalStateException("Cannot start assignment in status: " + assignment.getStatus());
        }

        assignment.setStatus(AssignmentStatus.IN_PROGRESS);
        assignment.setStartTime(LocalDateTime.now());

        Demande demande = assignment.getDemande();
        demande.setStatus(DemandeStatus.IN_PROGRESS);
        demandeRepository.save(demande);

        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment completeAssignment(Long assignmentId, String completionPhoto) {
        Assignment assignment = findAssignmentById(assignmentId);

        if (assignment.getStatus() != AssignmentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete assignment in status: " + assignment.getStatus());
        }

        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setEndTime(LocalDateTime.now());
        assignment.setCompletionPhoto(completionPhoto);

        Demande demande = assignment.getDemande();
        demande.setStatus(DemandeStatus.COMPLETED);
        demandeRepository.save(demande);

        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment failAssignment(Long assignmentId, String reason) {
        Assignment assignment = findAssignmentById(assignmentId);
        assignment.setStatus(AssignmentStatus.FAILED);
        assignment.setNotes(reason);
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment cancelAssignment(Long assignmentId) {
        Assignment assignment = findAssignmentById(assignmentId);
        assignment.setStatus(AssignmentStatus.FAILED);
        assignment.setNotes("Assignment cancelled");
        return assignmentRepository.save(assignment);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Assignment> getAssignmentsByTechnician(Long technicianId) {
        return assignmentRepository.findByTechnicianIdOrderByScheduledDateAsc(technicianId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> getAssignmentsByDemande(Long demandeId) {
        return assignmentRepository.findByDemandeIdOrderByCreatedAtDesc(demandeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assignment> getCurrentAssignmentByDemande(Long demandeId) {
        return demandeRepository.findById(demandeId)
                .map(Demande::getCurrentAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean isTechnicianAvailable(Technician technician, LocalDateTime dateTime, int estimatedDuration) {
        LocalDate date = dateTime.toLocalDate();

        if (!DaysOfWeek.isWorkingDay(dateTime.getDayOfWeek())) {
            return false;
        }

        if (holidayService.isHoliday(date)) {
            return false;
        }

        if (leaveService.isTechnicianOnLeave(technician.getId(), date)) {
            return false;
        }

        if (hasTimeConflict(technician, dateTime, estimatedDuration)) {
            return false;
        }

        long tasksCount = countAssignmentsByTechnicianAndDate(technician.getId(), date);
        return tasksCount < technician.getMaxTasksPerDay();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAssignmentsByTechnicianAndDate(Long technicianId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return assignmentRepository.countByTechnicianIdAndScheduledDateBetween(
                technicianId, startOfDay, endOfDay
        );
    }


    @Override
    public Optional<Technician> findBestTechnician(String city, LocalDateTime dateTime, int estimatedDuration) {
        List<Technician> technicians = technicianService.getAvailableTechniciansByRegion(city);

        if (technicians.isEmpty()) {
            return Optional.empty();
        }

        List<Technician> available = technicians.stream()
                .filter(tech -> isTechnicianAvailable(tech, dateTime, estimatedDuration))
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            return Optional.empty();
        }

        available.sort(Comparator.comparingLong(tech ->
                countAssignmentsByTechnicianAndDate(tech.getId(), dateTime.toLocalDate())
        ));

        return Optional.of(available.get(0));
    }

    @Override
    public List<LocalDateTime> findAlternativeSlots(String city, LocalDateTime preferredDate,
                                                    int estimatedDuration, int daysToSearch) {
        List<LocalDateTime> alternatives = new ArrayList<>();
        List<Technician> technicians = technicianService.getAvailableTechniciansByRegion(city);

        if (technicians.isEmpty()) {
            return alternatives;
        }

        for (int day = 1; day <= daysToSearch; day++) {
            LocalDate currentDate = preferredDate.toLocalDate().plusDays(day);

            for (int hour = 9; hour <= 17; hour++) {
                LocalDateTime slot = currentDate.atTime(hour, 0);

                boolean anyAvailable = technicians.stream()
                        .anyMatch(tech -> isTechnicianAvailable(tech, slot, estimatedDuration));

                if (anyAvailable) {
                    alternatives.add(slot);
                    if (alternatives.size() >= 5) break;
                }
            }
            if (alternatives.size() >= 5) break;
        }

        return alternatives;
    }


    @Override
    @Transactional(readOnly = true)
    public long countByStatus(AssignmentStatus status) {
        return assignmentRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTechnician(Long technicianId) {
        return assignmentRepository.countByTechnicianId(technicianId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageCompletionTime() {
        List<Assignment> completedAssignments = assignmentRepository.findByStatus(AssignmentStatus.COMPLETED);

        return completedAssignments.stream()
                .filter(a -> a.getStartTime() != null && a.getEndTime() != null)
                .mapToLong(a -> java.time.Duration.between(a.getStartTime(), a.getEndTime()).toMinutes())
                .average()
                .orElse(0.0);
    }


    private Assignment findAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with id: " + id));
    }

    private boolean hasTimeConflict(Technician technician, LocalDateTime requestedSlot, int estimatedDuration) {
        LocalDateTime requestedEnd = requestedSlot.plusMinutes(estimatedDuration + 30);
        LocalDate date = requestedSlot.toLocalDate();

        List<Assignment> allAssignments = getAssignmentsByTechnician(technician.getId());
        List<Assignment> dayAssignments = allAssignments.stream()
                .filter(a -> a.getScheduledDate().toLocalDate().equals(date))
                .toList();

        for (Assignment assignment : dayAssignments) {
            LocalDateTime existingStart = assignment.getScheduledDate();
            LocalDateTime existingEnd = existingStart
                    .plusMinutes(assignment.getEstimatedDuration() + 30);

            if (requestedSlot.isBefore(existingEnd) && requestedEnd.isAfter(existingStart)) {
                return true;
            }
        }

        return false;
    }
}