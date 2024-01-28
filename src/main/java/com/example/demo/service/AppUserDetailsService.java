package com.example.demo.service;

import com.example.demo.model.AppUser;
import com.example.demo.model.AppUserDetails;
import com.example.demo.repository.AppUserDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AppUserDetailsService implements UserDetailsService {
    private AppUserDetailsRepository appUserDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserDetailsRepository.findByUsername(username).map(AppUserDetails::new)
                .orElseThrow(()->new UsernameNotFoundException("Could not find "+username));
    }

    public List<AppUser> getAllUsers(){
        return appUserDetailsRepository.findAll();
    }

    public Optional<AppUser> getById(Long id){
      return appUserDetailsRepository.findById(id);
    }

    public AppUser addNewUser(AppUser appUser){
      return  appUserDetailsRepository.save(appUser);
    }
}
