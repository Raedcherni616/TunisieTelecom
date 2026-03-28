package com.example.demo.demande;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandeRepository extends JpaRepository<Demande,Long> {

    List<Demande> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<Demande> findByClientIdAndStatusOrderByCreatedAtDesc(Long clientId, DemandeStatus status);

    List<Demande> findByStatus(DemandeStatus status);

    List<Demande> findByCity(String city);

    List<Demande> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Demande> findByStatusAndCity(DemandeStatus status, String city);

    @Query("SELECT d FROM Demande d WHERE " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:city IS NULL OR d.city = :city) AND " +
            "(:clientId IS NULL OR d.client.id = :clientId)")
    List<Demande> searchDemandes(@Param("status") DemandeStatus status,
                                 @Param("city") String city,
                                 @Param("clientId") Long clientId);

    long countByStatus(DemandeStatus status);

    long countByCity(String city);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT d FROM Demande d JOIN FETCH d.client WHERE d.id = :id")
    Optional<Demande> findByIdWithClient(@Param("id") Long id);




}
