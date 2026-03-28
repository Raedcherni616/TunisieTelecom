package com.example.demo.role;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface RoleService {

     Role createRole(String name);

    List<Role> getAllRoles();

    Optional<Role>findRoleById(Long id);

    Optional<Role>findRoleByName(String Name);

}
