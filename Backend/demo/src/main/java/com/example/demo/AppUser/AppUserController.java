package com.example.demo.AppUser;

import com.example.demo.Security.config.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class AppUserController {

    private final AuthService authService;
    private final AppUserService appUserService;


    @PostMapping("/login")
    public String login(@RequestBody UserRequest user){
        return authService.login(user);
    }


    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<AppUser> getProfile(@AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<AppUser> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal AppUser currentUser) {

        AppUser updatedUser = appUserService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }


    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal AppUser currentUser) {

        appUserService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok("Password changed successfully");
    }


    @DeleteMapping("/delete-account")
    @PreAuthorize("hasAnyRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<String> deleteAccount(
            @RequestBody DeleteAccountRequest request,
            @AuthenticationPrincipal AppUser currentUser) {

        appUserService.deleteAccount(currentUser.getId(), request.getPassword());
        return ResponseEntity.ok("Account deleted successfully");
    }
}