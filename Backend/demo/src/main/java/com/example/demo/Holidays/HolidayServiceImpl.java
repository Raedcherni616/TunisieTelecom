package com.example.demo.Holidays;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Holiday> getHolidayByDate(LocalDate date) {
        return holidayRepository.findByDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysBetween(LocalDate start, LocalDate end) {
        return holidayRepository.findByDateBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysByYear(int year) {
        return holidayRepository.findByYear(year);
    }


    @Override
    public Holiday addHoliday(LocalDate date, String description) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (holidayRepository.existsByDate(date)) {
            throw new IllegalStateException("Holiday already exists for date: " + date);
        }

        Holiday holiday = Holiday.builder()
                .date(date)
                .description(description)
                .build();

        return holidayRepository.save(holiday);
    }

    @Override
    public Holiday updateHoliday(Long id, String newDescription) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found with id: " + id));

        holiday.setDescription(newDescription);
        return holidayRepository.save(holiday);
    }

    @Override
    public void removeHoliday(Long id) {
        if (!holidayRepository.existsById(id)) {
            throw new IllegalArgumentException("Holiday not found with id: " + id);
        }
        holidayRepository.deleteById(id);
    }

    @Override
    public void removeHolidayByDate(LocalDate date) {
        Holiday holiday = holidayRepository.findByDate(date)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found for date: " + date));
        holidayRepository.delete(holiday);
    }


    @Override
    public long countHolidaysInYear(int year) {
        return holidayRepository.findByYear(year).size();
    }

    @Override
    public boolean isWorkingDay(LocalDate date) {
        return DaysOfWeek.isWorkingDay(date.getDayOfWeek()) && !isHoliday(date);
    }
}