package com.example.demo.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {
    private final String EmailAddress;
    private final String Password;

}
