package com.example.demo.Holidays;

import com.example.demo.technician.Technician;
import com.example.demo.technician.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TechnicianLeaveServiceImpl implements TechnicianLeaveService {

    private final TechnicianLeaveRepository leaveRepository;
    private final TechnicianRepository technicianRepository;


    @Override
    @Transactional(readOnly = true)
    public List<TechnicianLeave> getLeavesByTechnician(Long technicianId) {
        return leaveRepository.findByTechnicianId(technicianId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianLeave> getLeavesByTechnicianOrdered(Long technicianId) {
        return leaveRepository.findByTechnicianIdOrderByStartDateDesc(technicianId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TechnicianLeave> getLeaveById(Long id) {
        return leaveRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianLeave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianLeave> getCurrentLeaves() {
        return leaveRepository.findAllCurrentLeaves(LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianLeave> getLeavesBetween(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return leaveRepository.findByStartDateBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnicianLeave> getLeavesByTechnicianBetween(Long technicianId, LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return leaveRepository.findByTechnicianIdAndStartDateBetween(technicianId, start, end);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean isTechnicianOnLeave(Long technicianId, LocalDate date) {
        return leaveRepository.isTechnicianOnLeave(technicianId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTechnicianOnLeaveNow(Long technicianId) {
        return leaveRepository.isTechnicianOnLeave(technicianId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOverlap(Long technicianId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return leaveRepository.hasOverlap(technicianId, startDate, endDate);
    }


    @Override
    public TechnicianLeave addLeave(Long technicianId, LocalDate startDate, LocalDate endDate, String reason) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found with id: " + technicianId));

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot add leave in the past");
        }

        if (hasOverlap(technicianId, startDate, endDate)) {
            throw new IllegalStateException("Leave overlaps with existing leave for this technician");
        }

        TechnicianLeave leave = TechnicianLeave.builder()
                .technician(technician)
                .startDate(startDate)
                .endDate(endDate)
                .reason(reason)
                .build();

        return leaveRepository.save(leave);
    }

    @Override
    public TechnicianLeave updateLeave(Long leaveId, LocalDate startDate, LocalDate endDate, String reason) {
        TechnicianLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave not found with id: " + leaveId));

        LocalDate newStart = startDate != null ? startDate : leave.getStartDate();
        LocalDate newEnd = endDate != null ? endDate : leave.getEndDate();

        if (newStart.isAfter(newEnd)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if ((startDate != null && !startDate.equals(leave.getStartDate())) ||
                (endDate != null && !endDate.equals(leave.getEndDate()))) {

            if (hasOverlap(leave.getTechnician().getId(), newStart, newEnd)) {
                throw new IllegalStateException("Updated leave overlaps with existing leave");
            }
        }

        if (startDate != null) leave.setStartDate(startDate);
        if (endDate != null) leave.setEndDate(endDate);
        if (reason != null) leave.setReason(reason);

        return leaveRepository.save(leave);
    }

    @Override
    public void removeLeave(Long leaveId) {

        if (!leaveRepository.existsById(leaveId)) {
            throw new IllegalArgumentException("Leave not found with id: " + leaveId);
        }
        leaveRepository.deleteById(leaveId);
    }

    @Override
    public void removeLeavesByTechnician(Long technicianId) {

        if (!technicianRepository.existsById(technicianId)) {
            throw new IllegalArgumentException("Technician not found with id: " + technicianId);
        }
        leaveRepository.deleteByTechnicianId(technicianId);
    }

    @Override
    public void cancelLeave(Long leaveId) {

        removeLeave(leaveId);
    }


    @Override
    public long countLeavesByTechnician(Long technicianId) {

        return leaveRepository.countByTechnicianId(technicianId);
    }

    @Override
    public long countLeavesByTechnicianAndYear(Long technicianId, int year) {

        return leaveRepository.countByTechnicianAndYear(technicianId, year);
    }

    @Override
    public long countCurrentLeaves() {

        return leaveRepository.findAllCurrentLeaves(LocalDate.now()).size();
    }


}