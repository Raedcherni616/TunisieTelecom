package com.example.demo.Holidays;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsByDate(LocalDate date);

    Optional<Holiday> findByDate(LocalDate date);

    List<Holiday> findByDateBetween(LocalDate start, LocalDate end);

    List<Holiday> findByDateAfter(LocalDate date);

    List<Holiday> findByDateBefore(LocalDate date);

    @Query("SELECT h FROM Holiday h WHERE YEAR(h.date) = :year")
    List<Holiday> findByYear(@Param("year") int year);

    @Query("SELECT h FROM Holiday h WHERE YEAR(h.date) = :year ORDER BY h.date")
    List<Holiday> findByYearOrderByDate(@Param("year") int year);

    @Query("SELECT h FROM Holiday h WHERE YEAR(h.date) = :year AND MONTH(h.date) = :month")
    List<Holiday> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    void deleteByDate(LocalDate date);

    long countByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT COUNT(h) FROM Holiday h WHERE YEAR(h.date) = :year")
    long countByYear(@Param("year") int year);
}