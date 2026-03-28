package com.example.demo.AppUser;

import com.example.demo.Reposetory.AppUserRepository;
import com.example.demo.role.Role;
import com.example.demo.role.RoleServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class AdminInintializer {
    private  final AppUserService appUserService;
    private final RoleServiceImpl roleServiceImpl;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppUserRepository appUserRepository;


    @PostConstruct
    public void createAdminIfNotExists () {

        Set<Role> roles = new HashSet<>();
        roles.add(roleServiceImpl.createRole("ADMIN"));
        roles.add(roleServiceImpl.createRole("USER"));
        boolean adminExists = appUserService.findUserByRoleName("ADMIN").isPresent();


        if (!adminExists) {
            AppUser admin = new AppUser(
                    "System",
                    "Admin",
                    "admin123",
                    "admin@system.com",
                    "00000000",
                    roles
            );
            String encodedPassword = bCryptPasswordEncoder
                    .encode(admin.getPassword());
            admin.setPassword(encodedPassword);

            appUserRepository.save(admin);
            for (Role role : roles) {
                appUserService.addRoleToUser(admin.getId(), role);
            }


        }




    }

}
