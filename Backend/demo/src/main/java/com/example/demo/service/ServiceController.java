package com.example.demo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;
    private final ImageService imageService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Services>> getAllServices() {
        return ResponseEntity.ok(serviceService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Services>> getActiveServices() {
        return ResponseEntity.ok(serviceService.getAllActiveServices());
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Services>> getInactiveServices() {
        return ResponseEntity.ok(serviceService.getAllNotActiveServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Services> getServiceById(@PathVariable Long id) {
        return serviceService.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Services> createService(
            @RequestPart("service") RequestService request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.saveImage(image);
            request.setImageUrl(imageUrl);
        }

        Services service = serviceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }



    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Services> updateService(
            @PathVariable Long id,
            @RequestPart("service") RequestService request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        request.setId(id);

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.saveImage(image);
            request.setImageUrl(imageUrl);
        }

        try {
            Services service = serviceService.updateService(request);
            return ResponseEntity.ok(service);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}