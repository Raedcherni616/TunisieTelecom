package com.example.demo.technician;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician , Long> {

    List<Technician> findByRegion(Region region);

    List<Technician> findByAvailableTrue();

    List<Technician> findAll();

    List<Technician> findByAvailableFalse()
            ;
    Optional<Technician> findById(Long id);

    boolean existsByAppUserId(Long userId);

    List<Technician> findByRegionAndAvailableTrue(Region region);

}
