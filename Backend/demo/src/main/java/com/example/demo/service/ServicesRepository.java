package com.example.demo.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {

    Optional<Services> findByName(String name);

    boolean existsByName(String name);

    Optional<Services> findById(Long id);

    List<Services> findByCategoryId(Long categoryId);

    List<Services> findByActiveTrue();

    List<Services> findByActiveFalse();
}