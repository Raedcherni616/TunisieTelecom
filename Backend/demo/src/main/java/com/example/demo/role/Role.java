package com.example.demo.role;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@Entity
public class Role {
    @SequenceGenerator(
            name = "role_sequence",
            sequenceName = "role_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "role_sequence"
    )

    @Id
    private  Long id;
    @Column( nullable = false , unique = true)
    private  String name;

    public Role (String name ) {

        this.name = name;
    }

}
