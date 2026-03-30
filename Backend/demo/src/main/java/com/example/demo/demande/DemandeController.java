package com.example.demo.demande;

import com.example.demo.AppUser.AppUser;
import com.example.demo.AppUser.AppUserService;
import com.example.demo.assign.AssignmentService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@AllArgsConstructor
public class DemandeController {

    private final DemandeService demandeService;
    private final AssignmentService assignmentService;
    private final AppUserService appUserService;

    @PostMapping("/create")
    public ResponseEntity<DemandeResponse> createDemande(@RequestBody DemandeRequest request) {

        Demande demande = demandeService.createDemande(request);

        if (demande.getStatus() == DemandeStatus.CONFIRMED) {
            return ResponseEntity.ok(
                    DemandeResponse.builder()
                            .demandeId(demande.getId())
                            .status(demande.getStatus().name())
                            .message("Technician assigned successfully!")
                            .build()
            );
        }

        List<LocalDateTime> alternatives = assignmentService.findAlternativeSlots(
                demande.getCity(),
                demande.getPreferredDate(),
                demande.getService().getEstimatedDuration(),
                7
        );

        if (alternatives.isEmpty()) {
            return ResponseEntity.status(503).body(
                    DemandeResponse.builder()
                            .demandeId(demande.getId())
                            .status("NO_AVAILABILITY")
                            .message("No technician available in the next 7 days")
                            .build()
            );
        }

        return ResponseEntity.ok(
                DemandeResponse.builder()
                        .demandeId(demande.getId())
                        .status(demande.getStatus().name())
                        .alternativeSlots(alternatives)
                        .message("Please choose an alternative date")
                        .build()
        );
    }

    @PostMapping("/{demandeId}/confirm")
    public ResponseEntity<DemandeResponse> confirmDemande(
            @PathVariable Long demandeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime selectedDate) {

        Demande demande = demandeService.confirmDemande(demandeId, selectedDate);

        return ResponseEntity.ok(
                DemandeResponse.builder()
                        .demandeId(demande.getId())
                        .status(demande.getStatus().name())
                        .message("Demande confirmed successfully!")
                        .build()
        );
    }

    @DeleteMapping("/{demandeId}/cancel")
    public ResponseEntity<DemandeResponse> cancelDemande(
            @PathVariable Long demandeId,
            @RequestParam Long userId) {

        AppUser user = appUserService.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with id " + userId));

        demandeService.cancelDemande(demandeId, user);

        return ResponseEntity.ok(
                DemandeResponse.builder()
                        .demandeId(demandeId)
                        .status("CANCELLED")
                        .message("Demande cancelled successfully!")
                        .build()
        );
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Demande>> getDemandesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(demandeService.getDemandesByClient(clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Demande> getDemandeById(@PathVariable Long id) {
        return demandeService.getDemandeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Demande>> getAllDemandes() {
        return ResponseEntity.ok(demandeService.getAllDemandes());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Demande>> searchDemandes(
            @RequestParam(required = false) DemandeStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long clientId) {
        return ResponseEntity.ok(demandeService.searchDemandes(status, city, clientId));
    }

    @PatchMapping("/{demandeId}/status")
    public ResponseEntity<DemandeResponse> updateStatus(
            @PathVariable Long demandeId,
            @RequestParam DemandeStatus status) {

        Demande demande = demandeService.updateDemandeStatus(demandeId, status);

        return ResponseEntity.ok(
                DemandeResponse.builder()
                        .demandeId(demande.getId())
                        .status(demande.getStatus().name())
                        .message("Status updated successfully!")
                        .build()
        );
    }

    @GetMapping("/{demandeId}/alternatives")
    public ResponseEntity<List<LocalDateTime>> getAlternatives(@PathVariable Long demandeId) {
        Demande demande = demandeService.getDemandeById(demandeId)
                .orElseThrow(() -> new IllegalStateException("Demande not found with id: " + demandeId));
        List<LocalDateTime> alternatives = assignmentService.findAlternativeSlots(
                demande.getCity(),
                demande.getPreferredDate(),
                demande.getService().getEstimatedDuration(),
                7
        );
        return ResponseEntity.ok(alternatives);
    }
}