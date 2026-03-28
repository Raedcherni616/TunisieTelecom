package com.example.demo.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

        Optional<ServiceCategory> findByName(String name);

        boolean existsByName(String name);

        Optional<ServiceCategory>findById(Long id);

        List<ServiceCategory> findAll();

}
