package com.example.demo.Holidays;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDay {

    @Id
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

}