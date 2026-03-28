package com.example.demo.role;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RoleSeeder {

    private final RoleService roleService;

    @PostConstruct
    public void seedRoles() {
        roleService.createRole("ADMIN");
        roleService.createRole("TECHNICIAN");
        roleService.createRole("USER");
    }
}
