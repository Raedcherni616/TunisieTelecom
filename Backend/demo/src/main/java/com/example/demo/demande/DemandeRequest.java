package com.example.demo.demande;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeRequest {

    private Long serviceId;

    private String city;

    private Long clientId;

    private String address;

    private String additionalDetails;

    private LocalDateTime preferredDate;

    private String notes;
}