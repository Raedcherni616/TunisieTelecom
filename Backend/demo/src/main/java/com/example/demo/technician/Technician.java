package com.example.demo.technician;

import com.example.demo.AppUser.AppUser;
import com.example.demo.Holidays.TechnicianLeave;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Technician {

    @SequenceGenerator(
            name = "technician_sequence",
            sequenceName = "technician_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "technician_sequence"
    )
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @Column(nullable = false)
    private Integer maxTasksPerDay = 5;

    @Column(nullable = false)
    private Boolean available = true;

    private LocalDate hireDate;

    private String profilePicture;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "technician", cascade = CascadeType.ALL)
    private Set<TechnicianLeave> leaves = new HashSet<>();

    public Technician(String profilePicture,
                      LocalDate hireDate,
                      Boolean available,
                      Region region,
                      Integer maxTasksPerDay,
                      AppUser appUser) {

        this.profilePicture = profilePicture;
        this.hireDate = hireDate;
        this.available = available;
        this.region = region;
        this.maxTasksPerDay = maxTasksPerDay;
        this.appUser = appUser;
    }

    public Technician(LocalDate hireDate,
                      Boolean available,
                      Region region,
                      Integer maxTasksPerDay,
                      AppUser appUser) {

        this.hireDate = hireDate;
        this.available = available;
        this.region = region;
        this.maxTasksPerDay = maxTasksPerDay;
        this.appUser = appUser;
    }


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

