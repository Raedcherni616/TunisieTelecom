package com.example.demo.demande;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DemandeResponse {
    private Long demandeId;
    private String status;
    private List<LocalDateTime> alternativeSlots;
    private String message;
}