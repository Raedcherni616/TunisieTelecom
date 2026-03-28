package com.example.demo.Holidays;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingDayRepository extends JpaRepository<WorkingDay,Long> {
    boolean existsByDay(DaysOfWeek days);

}
