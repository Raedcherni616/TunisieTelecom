package com.example.demo.Holidays;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TechnicianLeaveService {

    List<TechnicianLeave> getLeavesByTechnician(Long technicianId);
    List<TechnicianLeave> getLeavesByTechnicianOrdered(Long technicianId);
    Optional<TechnicianLeave> getLeaveById(Long id);
    List<TechnicianLeave> getAllLeaves();
    List<TechnicianLeave> getCurrentLeaves();
    List<TechnicianLeave> getLeavesBetween(LocalDate start, LocalDate end);
    List<TechnicianLeave> getLeavesByTechnicianBetween(Long technicianId, LocalDate start, LocalDate end);


    boolean isTechnicianOnLeave(Long technicianId, LocalDate date);
    boolean isTechnicianOnLeaveNow(Long technicianId);
    boolean hasOverlap(Long technicianId, LocalDate startDate, LocalDate endDate);

    TechnicianLeave addLeave(Long technicianId, LocalDate startDate, LocalDate endDate, String reason);
    TechnicianLeave updateLeave(Long leaveId, LocalDate startDate, LocalDate endDate, String reason);
    void removeLeave(Long leaveId);
    void removeLeavesByTechnician(Long technicianId);
    void cancelLeave(Long leaveId);

    long countLeavesByTechnician(Long technicianId);
    long countLeavesByTechnicianAndYear(Long technicianId, int year);
    long countCurrentLeaves();
}
