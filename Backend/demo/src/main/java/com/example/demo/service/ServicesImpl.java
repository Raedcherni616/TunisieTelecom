package com.example.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ServicesImpl implements ServiceService {

    private final ServicesRepository servicesRepository;
    private final CategoryService categoryService;

    @Override
    @Transactional(readOnly = true)
    public List<Services> getServicesCategoryById(Long categoryId) {
        ServiceCategory existingCategory = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found"));
        return servicesRepository.findByCategoryId(existingCategory.getId());
    }

    @Override
    public Optional<Services> getServiceById(Long id) {
        return servicesRepository.findById(id);
    }

    @Override
    @Transactional
    public Services createService(RequestService requestService) {
        if (requestService.getName() == null || requestService.getName().isBlank()) {
            throw new IllegalArgumentException("Service must have a valid name");
        }
        if (servicesRepository.existsByName(requestService.getName())) {
            throw new IllegalStateException("Service with name '" + requestService.getName() + "' already exists");
        }
        ServiceCategory existingCategory = categoryService.findCategoryById(requestService.getIdCategory())
                .orElseThrow(() -> new IllegalStateException("Category with id " + requestService.getIdCategory() + " not found"));

        Services service = Services.builder()
                .name(requestService.getName())
                .description(requestService.getDescription())
                .price(requestService.getPrice())
                .estimatedDuration(requestService.getEstimatedDuration())
                .category(existingCategory)
                .imageUrl(requestService.getImageUrl())
                .active(true)
                .build();
        return servicesRepository.save(service);
    }

    @Override
    @Transactional
    public Services updateService(RequestService requestService) {
        Services service = servicesRepository.findById(requestService.getId())
                .orElseThrow(() -> new IllegalStateException("Service with id " + requestService.getId() + " not found"));

        if (requestService.getName() != null && !requestService.getName().isBlank()) {
            service.setName(requestService.getName());
        }
        if (requestService.getDescription() != null) {
            service.setDescription(requestService.getDescription());
        }
        if (requestService.getPrice() != null) {
            service.setPrice(requestService.getPrice());
        }
        if (requestService.getEstimatedDuration() != null) {
            service.setEstimatedDuration(requestService.getEstimatedDuration());
        }
        if (requestService.getImageUrl() != null) {
            service.setImageUrl(requestService.getImageUrl());
        }
        if (requestService.getActive() != null) {
            service.setActive(requestService.getActive());
        }
        if (requestService.getIdCategory() != null) {
            ServiceCategory existingCategory = categoryService.findCategoryById(requestService.getIdCategory())
                    .orElseThrow(() -> new IllegalStateException("Category with id " + requestService.getIdCategory() + " not found"));
            service.setCategory(existingCategory);
        }
        return servicesRepository.save(service);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        Services service = servicesRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Service with id " + id + " not found"));
        servicesRepository.delete(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Services> getAllActiveServices() {
        return servicesRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Services> getAllNotActiveServices() {
        return servicesRepository.findByActiveFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Services> findAll() {
        return servicesRepository.findAll();
    }
}