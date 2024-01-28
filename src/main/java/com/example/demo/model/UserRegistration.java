package com.example.demo.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Please provide username")
    @Size(max = 10, min = 3,message = "Username must not be more than 10 characters long")
    private String username;

    @NotNull(message = "Please provide a password")
    private String password;

    @NotNull(message = "Please enter your full name")
    private String fullname;


    public AppUser toAppUser(PasswordEncoder passwordEncoder){
        return new AppUser(username,passwordEncoder.encode(password),fullname);
    }
}
