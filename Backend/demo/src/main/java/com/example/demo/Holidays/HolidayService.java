package com.example.demo.Holidays;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayService {

    List<Holiday> getAllHolidays();

    boolean isHoliday(LocalDate date);

    Optional<Holiday> getHolidayByDate(LocalDate date);

    List<Holiday> getHolidaysBetween(LocalDate start, LocalDate end);

    List<Holiday> getHolidaysByYear(int year);

    Holiday addHoliday(LocalDate date, String description);

    Holiday updateHoliday(Long id, String newDescription);

    void removeHoliday(Long id);

    void removeHolidayByDate(LocalDate date);

    long countHolidaysInYear(int year);

    boolean isWorkingDay(LocalDate date);
}