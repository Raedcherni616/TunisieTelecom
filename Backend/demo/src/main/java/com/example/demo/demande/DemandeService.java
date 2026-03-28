package com.example.demo.demande;

import com.example.demo.AppUser.AppUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandeService {

    Demande createDemande(DemandeRequest request);

    Demande confirmDemande(Long demandeId, LocalDateTime selectedDate);

    void cancelDemande(Long demandeId, AppUser user);

    Demande updateDemandeStatus(Long demandeId, DemandeStatus status);

    List<Demande> getDemandesByClient(Long clientId);

    Optional<Demande> getDemandeById(Long id);

    List<Demande> getAllDemandes();

    List<Demande> searchDemandes(DemandeStatus status, String city, Long clientId);

    long countByStatus(DemandeStatus status);

    long countByCity(String city);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
