package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.model.AppUserDetails;
import com.example.demo.model.AuthRequest;
import com.example.demo.service.AppUserDetailsService;
import com.example.demo.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Slf4j
@RequestMapping("/auth")
@RestController
public class AppUserController {

    private AppUserDetailsService appUserDetailsService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;

    public AppUserController(AppUserDetailsService appUserDetailsService,
                             AuthenticationManager authenticationManager,
                             JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.appUserDetailsService = appUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome,this end point is not secured";
    }

    @GetMapping("/all")
    public List<AppUser> all(){
        return appUserDetailsService.getAllUsers();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<AppUser> getUser(@PathVariable("id") Long id){
       Optional<AppUser> user = appUserDetailsService.getById(id);
        return user.map(value->new ResponseEntity<>(value,HttpStatus.OK))
                .orElseGet(()->new ResponseEntity<>(null,HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile(){
        return "Welcome to user profile";
    }

    @GetMapping("/user/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile(){
        return "Welcome to admin profile";
    }

    @PostMapping("/generateToken")
    public String authenticateAndGenerateToken(@RequestBody AuthRequest authRequest){
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(authRequest.getUsername());
        }else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }


    @PostMapping("/addUser")
    public String addUser(@RequestBody AppUser newUser){
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        log.info(newUser.getFullname());
     appUserDetailsService.addNewUser(newUser);
     return "User created successfully";
    }

    @GetMapping("/me")
    public ResponseEntity<AppUserDetails> authenticatedUser(){
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
     AppUserDetails user = (AppUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

}
