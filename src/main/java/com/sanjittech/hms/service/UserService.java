package com.sanjittech.hms.service;

import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.DoctorRepository;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private com.sanjittech.hms.repository.HospitalRepository hospitalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createAdmin() {
        String plainPassword = "Admin@123";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("Encoded password: " + encodedPassword);
    }

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (user.getHospital() == null && user.getHospitalId() != null) {
            hospitalRepository.findById(user.getHospitalId()).ifPresent(user::setHospital);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        if (user.getRole() == UserRole.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUser(savedUser);
            doctor.setDoctorName(user.getUsername());
            doctorRepository.save(doctor);
        }

        return savedUser;
    }

    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username"));

        // ✅ DEBUG LOGGING START
        System.out.println("🔑 Raw password from frontend: " + rawPassword);
        System.out.println("🔐 Encoded password from DB: " + user.getPassword());
        boolean match = passwordEncoder.matches(rawPassword, user.getPassword());
        System.out.println("✅ Password match result: " + match);
        // ✅ DEBUG LOGGING END

        if (!match) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public void updatePasswordByEmail(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public User getLoggedInUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No Authorization header found.");
            return null;
        }
        String token = authHeader.substring(7);
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                return userRepository.findByUsername(username).orElse(null);
            }
        } catch (Exception e) {
            System.out.println("❌ Error extracting user from token: " + e.getMessage());
        }

        return null;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
