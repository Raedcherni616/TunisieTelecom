package com.example.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    @Transactional( readOnly = true)
    public List<ServiceCategory> getAllCategories() {
        return serviceCategoryRepository.findAll();
    }

    @Override
    @Transactional
    public ServiceCategory createCategory(ServiceCategory category) {
        if (category == null || category.getName() == null || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category must have a valid name");
        }

        boolean exists = serviceCategoryRepository.existsByName(category.getName());
        if (exists) {
            throw new IllegalStateException("Category with name '" + category.getName() + "' already exists");
        }
        return serviceCategoryRepository.save(category);
    }


    @Override
    @Transactional
    public ServiceCategory updateCategory(Long id, String name, String description) {

        ServiceCategory serviceCategory = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Category with id " + id + " not found"));

        serviceCategory.setName(name);
        serviceCategory.setDescription(description);

        return serviceCategoryRepository.save(serviceCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        if (serviceCategoryRepository.existsById(id)){
            serviceCategoryRepository.deleteById(id);
        }
        else {
            throw new IllegalStateException("Category with id " + id + " not found");
        }
    }

    @Override
    @Transactional( readOnly = true)
    public Optional<ServiceCategory> findCategoryByName(String name) {
        return serviceCategoryRepository.findByName(name);
    }

    @Override
    @Transactional( readOnly = true)

    public Optional<ServiceCategory> findCategoryById(Long id) {

        return serviceCategoryRepository.findById(id);

    }
}
