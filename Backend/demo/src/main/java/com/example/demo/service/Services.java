package com.example.demo.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Services {

    @Id
    @SequenceGenerator(
            name = "service_sequence",
            sequenceName = "service_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "service_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer estimatedDuration;

    private BigDecimal price;

    private Boolean active = true;

    @Column(name = "image_url")
    private String imageUrl;

    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private ServiceCategory category;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Services(ServiceCategory category,
                    Boolean active,
                    BigDecimal price,
                    Integer estimatedDuration,
                    String description,
                    String name,
                    String imageUrl) {
        this.category = category;
        this.active = active;
        this.price = price;
        this.estimatedDuration = estimatedDuration;
        this.description = description;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}