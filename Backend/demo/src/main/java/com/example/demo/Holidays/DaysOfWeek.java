package com.example.demo.Holidays;

import java.util.Set;
import java.time.DayOfWeek;

public enum DaysOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;


    public static boolean isWorkingDay(java.time.DayOfWeek day) {
        return switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY -> true;
            case SUNDAY -> false;
        };
    }

    public static Set<DayOfWeek> getWorkingDays() {
        return Set.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        );
    }
}
