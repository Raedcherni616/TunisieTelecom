package com.example.demo.Holidays;

import com.example.demo.technician.Technician;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "technician_id", nullable = false)
    private Technician technician;

    private LocalDate startDate;


    private LocalDate endDate;

    private String reason;

}