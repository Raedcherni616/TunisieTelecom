package com.example.demo.demande;

import com.example.demo.AppUser.AppUser;
import com.example.demo.AppUser.AppUserService;
import com.example.demo.service.ServiceService;
import com.example.demo.service.Services;
import com.example.demo.assign.AssignmentService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor

public class DemandeServiceImpl implements DemandeService{

    private final AppUserService appUserService;
    private final DemandeRepository demandeRepository;
    private final ServiceService serviceService;
    private final AssignmentService assignmentService;
    @Override
    public Demande createDemande(DemandeRequest request) {

        AppUser appUser = appUserService.findById(request.getClientId())
                .orElseThrow(()->new IllegalStateException("Client not find with id "+request.getClientId()));

        Services services =  serviceService.getServiceById(request.getServiceId())
                .orElseThrow(()->new IllegalStateException("Service not find with id "+request.getServiceId()));

        Demande demande = Demande.builder()
                .client(appUser)
                .city(request.getCity())
                .service(services)
                .notes(request.getNotes())
                .additionalDetails(request.getAdditionalDetails())
                .address(request.getAddress())
                .preferredDate(request.getPreferredDate())
        .build();

        demandeRepository.save(demande);

        assignmentService.tryAutoAssignTechnician(demande);

        return demande;

    }

    @Transactional
    @Override
    public Demande confirmDemande(Long demandeId, LocalDateTime selectedDate) {

        Demande demande = demandeRepository.findByIdWithClient(demandeId)
                .orElseThrow(() -> new IllegalStateException("Demande not found with id " + demandeId));


        if (demande.getStatus() != DemandeStatus.AWAITING_CLIENT) {
            throw new IllegalStateException("Cannot confirm demande in status: " + demande.getStatus());
        }


        if (selectedDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Selected date cannot be in the past");
        }


        boolean assigned = assignmentService.tryAssignTechnician(demande, selectedDate);

        if (!assigned) {
            throw new IllegalStateException("No technician available for selected date: " + selectedDate);
        }


        return demandeRepository.findByIdWithClient(demandeId)
                .orElseThrow(() -> new IllegalStateException("Demande not found"));
    }

    @Transactional
    @Override
    public void cancelDemande(Long demandeId, AppUser user) {

        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow();

        if (!demande.getClient().getId().equals(user.getId()) && !isAdmin(user)) {

            throw new IllegalStateException("You can only cancel your own demands");
        }

        demande.setStatus(DemandeStatus.CANCELLED);

        demandeRepository.save(demande);
    }

    private boolean isAdmin(AppUser user) {
        return user.getRole().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

    }
    @Transactional
    @Override
    public Demande updateDemandeStatus(Long demandeId, DemandeStatus status) {

        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(()-> new IllegalStateException("Demande not find with id "+demandeId));
        demande.setStatus(status);
        return demandeRepository.save(demande);
    }
    @Transactional(readOnly = true)
    @Override
    public List<Demande> getDemandesByClient(Long clientId) {

        return demandeRepository.findByClientIdOrderByCreatedAtDesc(clientId);
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<Demande> getDemandeById(Long id) {

        return demandeRepository.findById(id);
    }
    @Transactional(readOnly = true)
    @Override
    public List<Demande> getAllDemandes() {

        return demandeRepository.findAll();
    }
    @Transactional(readOnly = true)
    @Override
    public List<Demande> searchDemandes(DemandeStatus status,
                                        String city,
                                        Long clientId) {

        return demandeRepository.searchDemandes(status, city, clientId);
    }

    @Override
    public long countByStatus(DemandeStatus status) {

        return demandeRepository.countByStatus(status);
    }

    @Override
    public long countByCity(String city) {

        return demandeRepository.countByCity(city);
    }

    @Override
    public long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {

        return demandeRepository.countByCreatedAtBetween(start,end);
    }
}
