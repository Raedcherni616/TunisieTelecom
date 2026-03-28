package com.example.demo.AppUser;

import com.example.demo.Registration.token.ConfirmationToken;
import com.example.demo.Registration.token.ConfirmationTokenService;
import com.example.demo.Reposetory.AppUserRepository;
import com.example.demo.role.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND = "user with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND, email)));
    }


    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByEmail(appUser.getEmail())
                .isPresent();
        if (userExists) {
            throw new IllegalStateException("email already taken");
        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }


    @Transactional
    public void enableAppUser(String email) {
        appUserRepository.enableAppUser(email);
    }

    public Optional<AppUser> findUserByRoleName(String role) {
        return appUserRepository.findUserByRoleName(role);

    }


    @Transactional(readOnly = true)
    public Optional<AppUser> findById(long id) {
        return appUserRepository.findById(id);
    }

    @Transactional
    public void addRoleToUser(Long userId, Role role) {

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getRole().contains(role)) {
            user.getRole().add(role);
            appUserRepository.save(user);
        }
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Role role) {

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole().contains(role)) {
            user.getRole().remove(role);
            appUserRepository.save(user);
        }
    }

    @Transactional
    public AppUser updateProfile(Long userId, UpdateProfileRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newFirstName = request.getFirstName() != null ? request.getFirstName() : user.getFirstName();
        String newLastName = request.getLastName() != null ? request.getLastName() : user.getLastName();
        String newPhoneNumber = request.getPhoneNumber() != null ? request.getPhoneNumber() : user.getPhoneNumber();

        if (!newPhoneNumber.equals(user.getPhoneNumber())) {
            if (appUserRepository.findByPhoneNumber(newPhoneNumber).isPresent()) {
                throw new IllegalStateException("Phone number already taken");
            }
        }

        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setPhoneNumber(newPhoneNumber);

        return appUserRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Old password is incorrect");
        }

        if (request.getNewPassword() != null && request.getConfirmPassword() != null) {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new IllegalStateException("New password and confirm password do not match");
            }
        }

        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new IllegalStateException("New password must be different from old password");
        }

        String encodedNewPassword = bCryptPasswordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);

        appUserRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId, String password) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        appUserRepository.delete(user);
    }
}
