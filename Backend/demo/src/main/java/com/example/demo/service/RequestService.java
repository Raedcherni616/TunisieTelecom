package com.example.demo.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data

    public class RequestService {
        private Long id;
        private Long idCategory;
        private Boolean active;
        private BigDecimal price;
        private Integer estimatedDuration;
        private String description;
        private String name;
        private String imageUrl;

    public RequestService(
                          Boolean active,
                          Long idCategory,
                          BigDecimal price,
                          Integer estimatedDuration,
                          String name,
                          String description
                          ) {
        this.active = active;
        this.idCategory = idCategory;
        this.price = price;
        this.estimatedDuration = estimatedDuration;
        this.name = name;
        this.description = description;

    }
    public RequestService(Long id){
        this.id = id;
    }
}

