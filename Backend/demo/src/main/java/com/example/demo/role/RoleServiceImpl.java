package com.example.demo.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role createRole(String name) {
        return roleRepository.findRoleByName(name)
                .orElseGet(() -> roleRepository.save(new Role(name)));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findRoleById(Long id) {

        return roleRepository.findById(id);

    }

    @Override
    public Optional<Role> findRoleByName( String name) {

        return roleRepository.findRoleByName(name);
    }
}
