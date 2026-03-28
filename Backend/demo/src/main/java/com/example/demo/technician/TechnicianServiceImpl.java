package com.example.demo.technician;

import com.example.demo.AppUser.AppUser;
import com.example.demo.AppUser.AppUserService;
import com.example.demo.role.Role;
import com.example.demo.role.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class TechnicianServiceImpl implements TechnicianService{

    private final TechnicianRepository technicianRepository;
    private final AppUserService appUserService;
    private final RoleService roleService;

    @Override
    @Transactional
    public Technician createTechnician(TechnicianRequest technicianRequest) {

        if (technicianRequest.getAppUser() == null || technicianRequest.getAppUser().getId() == null) {
            throw new IllegalArgumentException("Technician must be linked to an existing user");
        }

        AppUser appUser = appUserService.findById(technicianRequest.getAppUser().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User with id " + technicianRequest.getAppUser().getId() + " not found"));

        if (technicianRepository.existsByAppUserId(appUser.getId())) {
            throw new IllegalStateException("This user is already a technician");
        }

        if (technicianRequest.getRegion() == null) {
            throw new IllegalArgumentException("Region must be provided");
        }

        if (technicianRequest.getMaxTasksPerDay() == null || technicianRequest.getMaxTasksPerDay() <= 0) {
            technicianRequest.setMaxTasksPerDay(5);
        }

        if (technicianRequest.getAvailable() == null) {
            technicianRequest.setAvailable(true);
        }

        if (technicianRequest.getHireDate() != null && technicianRequest.getHireDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }

        if (technicianRequest.getProfilePicture() != null && technicianRequest.getProfilePicture().isBlank()) {
            technicianRequest.setProfilePicture(null);
        }

        Role technicianRole = roleService.createRole("TECHNICIAN");
        if (!appUser.getRole().contains(technicianRole)) {
            appUser.getRole().add(technicianRole);
            appUserService.addRoleToUser(appUser.getId(), technicianRole);
        }

        technicianRequest.setAppUser(appUser);

        Technician technician = Technician.builder()
                .appUser(technicianRequest.getAppUser())
                .region(technicianRequest.getRegion())
                .hireDate(technicianRequest.getHireDate())
                .profilePicture(technicianRequest.getProfilePicture())
                .available(technicianRequest.getAvailable())
                .maxTasksPerDay(technicianRequest.getMaxTasksPerDay())
                .build();


        return technicianRepository.save(technician);
    }
    @Override
    @Transactional
    public Technician updateTechnician(TechnicianRequest technicianRequest) {

        Technician existingTechnician = technicianRepository.findById(technicianRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("Technician with id " +technicianRequest.getId()+ " not found"));

        if (technicianRequest.getRegion() != null) existingTechnician.setRegion(technicianRequest.getRegion());
        if (technicianRequest.getMaxTasksPerDay() != null) existingTechnician.setMaxTasksPerDay(technicianRequest.getMaxTasksPerDay());
        if (technicianRequest.getAvailable() != null) existingTechnician.setAvailable(technicianRequest.getAvailable());
        if (technicianRequest.getHireDate() != null) existingTechnician.setHireDate(technicianRequest.getHireDate());
        if (technicianRequest.getProfilePicture() != null) existingTechnician.setProfilePicture(technicianRequest.getProfilePicture());

        return technicianRepository.save(existingTechnician);
    }

    @Override
    public void deleteTechnician(Long id) {

        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

        Role technicianRole = roleService.findRoleByName("TECHNICIAN")
                .orElseThrow(() -> new IllegalStateException("Role not found"));





        appUserService.removeRoleFromUser(technician.getAppUser().getId(), technicianRole);
        technicianRepository.delete(technician);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Technician> findTechnicianById(Long id) {
        return technicianRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Technician> getTechniciansByRegion(Region region) {
        return technicianRepository.findByRegion(region);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Technician> getAvailableTechnicians() {

        return technicianRepository.findByAvailableTrue();
    }

    @Override
    public List<Technician> getAvailableTechniciansByRegion(String city) {
        return technicianRepository.findByRegionAndAvailableTrue(Region.valueOf(city));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Technician> getNotAvailableTechnicians() {

        return technicianRepository.findByAvailableFalse();
    }
}
