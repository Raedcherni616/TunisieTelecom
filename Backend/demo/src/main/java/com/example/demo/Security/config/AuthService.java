package com.example.demo.Security.config;

import com.example.demo.AppUser.UserRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String login(UserRequest user) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmailAddress(),
                            user.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return jwtService.generateToken(userDetails);
        } catch (BadCredentialsException e) {
            return "Wrong password";
        } catch (DisabledException e) {
            return "Account not activated";
        } catch (LockedException e) {
            return "Account locked";
        } catch (Exception e) {
            return "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }
}