package com.example.demo.technician;

import com.example.demo.AppUser.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@AllArgsConstructor
@Data

public class TechnicianRequest {

    private Long id;

    private AppUser appUser;

    @Enumerated(EnumType.STRING)
    private Region region;

    private Integer maxTasksPerDay;

    private Boolean available;

    private LocalDate hireDate;

    private String profilePicture;

    public TechnicianRequest(AppUser appUser,
                             Region region,
                             Integer maxTasksPerDay,
                             Boolean available,
                             LocalDate hireDate,
                             String profilePicture) {

        this.appUser = appUser;
        this.region = region;
        this.maxTasksPerDay = maxTasksPerDay;
        this.available = available;
        this.hireDate = hireDate;
        this.profilePicture = profilePicture;
    }
}
