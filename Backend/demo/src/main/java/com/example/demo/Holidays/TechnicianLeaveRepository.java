package com.example.demo.Holidays;

import com.example.demo.technician.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TechnicianLeaveRepository extends JpaRepository<TechnicianLeave, Long> {

    List<TechnicianLeave> findByTechnicianId(Long technicianId);

    List<TechnicianLeave> findByTechnicianIdOrderByStartDateDesc(Long technicianId);

    List<TechnicianLeave> findByTechnician(Technician technician);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM TechnicianLeave l " +
            "WHERE l.technician.id = :technicianId AND :date BETWEEN l.startDate AND l.endDate")
    boolean isTechnicianOnLeave(@Param("technicianId") Long technicianId,
                                @Param("date") LocalDate date);

    @Query("SELECT l FROM TechnicianLeave l WHERE l.technician.id = :technicianId " +
            "AND :date BETWEEN l.startDate AND l.endDate")
    List<TechnicianLeave> findCurrentLeaveByTechnician(@Param("technicianId") Long technicianId,
                                                       @Param("date") LocalDate date);

    @Query("SELECT l FROM TechnicianLeave l WHERE :date BETWEEN l.startDate AND l.endDate")
    List<TechnicianLeave> findAllCurrentLeaves(@Param("date") LocalDate date);


    List<TechnicianLeave> findByStartDateBetween(LocalDate start, LocalDate end);

    List<TechnicianLeave> findByEndDateBetween(LocalDate start, LocalDate end);

    List<TechnicianLeave> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate endDate, LocalDate startDate);


    @Query("SELECT COUNT(l) > 0 FROM TechnicianLeave l WHERE l.technician.id = :technicianId " +
            "AND (:startDate BETWEEN l.startDate AND l.endDate " +
            "OR :endDate BETWEEN l.startDate AND l.endDate " +
            "OR (l.startDate BETWEEN :startDate AND :endDate))")
    boolean hasOverlap(@Param("technicianId") Long technicianId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate);

    void deleteByTechnicianId(Long technicianId);

    long countByTechnicianId(Long technicianId);

    @Query("SELECT COUNT(l) FROM TechnicianLeave l WHERE l.technician.id = :technicianId " +
            "AND YEAR(l.startDate) = :year")
    long countByTechnicianAndYear(@Param("technicianId") Long technicianId,
                                  @Param("year") int year);

    List<TechnicianLeave> findByTechnicianIdAndStartDateBetween(
            Long technicianId,
            LocalDate startDate,
            LocalDate endDate);
}