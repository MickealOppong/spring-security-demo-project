package com.example.demo.config;

import com.example.demo.enums.Role;
import com.example.demo.model.AppUser;
import com.example.demo.model.JwtAuthFilter;
import com.example.demo.repository.AppUserDetailsRepository;
import com.example.demo.service.AppUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private AppUserDetailsRepository appUserDetailsRepository;
    private JwtAuthFilter authFilter;

    @Bean
    public UserDetailsService userDetailsService(){
        return new AppUserDetailsService(appUserDetailsRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{
        return httpSecurity.csrf( x->x.disable())
                .authorizeHttpRequests(request->request.requestMatchers("/auth/user/**").authenticated()
                        .requestMatchers("/auth/admin/**").authenticated()
                        .requestMatchers("/auth/all","/auth/welcome","/auth/addUser","/auth/generateToken","/auth/me").permitAll())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception{
        return config.getAuthenticationManager();
    }

/*
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET,POST"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type"));
        UrlBasedCorsConfigurationSource  source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);
        return source;
    }

 */



    @Bean
    public CommandLineRunner load(){
       return args -> {
           appUserDetailsRepository.save(new AppUser("woody", passwordEncoder().encode("password"),
                   "woody woods", "ROLE_"+Role.ADMIN.name()));
           appUserDetailsRepository.save(new AppUser("buzz", passwordEncoder().encode("password"),
                   "buzz woods","ROLE_"+ Role.USER.name()));
           appUserDetailsRepository.save(new AppUser("mike", passwordEncoder().encode("password"),
                   "mike epps", "ROLE_"+Role.USER.name()));
       };
    }


}
