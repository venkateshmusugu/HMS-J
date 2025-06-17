package com.sanjittech.hms.service;

import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.config.SecurityUser; // Import your new SecurityUser
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

// No need for GrantedAuthority list here if SecurityUser generates it
// import java.util.List; // Remove if not directly used

@Primary
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("LOADING USER: " + username + " | stack depth: " + Thread.currentThread().getStackTrace().length);
        User userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SecurityUser(userEntity);
    }
}