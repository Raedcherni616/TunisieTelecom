package com.example.demo.service;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<ServiceCategory> getAllCategories(); // user + admin

    ServiceCategory createCategory(ServiceCategory category); // admin

    ServiceCategory updateCategory(Long id, String name, String description); // admin

    void deleteCategory(Long id);

    Optional<ServiceCategory> findCategoryByName(String name);

    Optional<ServiceCategory> findCategoryById(Long Id);

}