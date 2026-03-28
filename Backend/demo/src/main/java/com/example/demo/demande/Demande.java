package com.example.demo.demande;

import com.example.demo.AppUser.AppUser;
import com.example.demo.assign.Assignment;
import com.example.demo.service.Services;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "demandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demande {

    @Id
    @SequenceGenerator(
            name = "demande_sequence",
            sequenceName = "demande_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "demande_sequence"
    )
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private AppUser client;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Services service;


    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 500)
    private String address;

    private LocalDateTime preferredDate;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DemandeStatus status;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_assignment_id")
    private Assignment currentAssignment;


    @Column(length = 1000)
    private String notes;

    private String additionalDetails;

    @Column(name = "created_at")
    private LocalDateTime createdAt;




    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = DemandeStatus.AWAITING_CLIENT;
        }
    }


}