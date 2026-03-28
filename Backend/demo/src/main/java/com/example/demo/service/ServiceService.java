package com.example.demo.service;

import java.util.List;
import java.util.Optional;

public interface ServiceService {

    List<Services> getServicesCategoryById(Long id);

    Optional<Services> getServiceById(Long id);

    Services createService(RequestService requestService);

    Services updateService(RequestService requestService);

    void deleteService(Long id);

    List<Services> findAll();

    List<Services> getAllActiveServices();

    List<Services> getAllNotActiveServices();
}