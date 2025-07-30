package com.sanjittech.hms.controller;

import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.dto.RegisterRequest;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.HospitalRepository;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.service.UserService;
import com.sanjittech.hms.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private HospitalRepository hospitalRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest dto, @RequestHeader(value = "Authorization", required = true) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Missing Authorization token.");
            }

            String token = authHeader.substring(7);
            String adminUsername = jwtUtil.extractUsername(token);
            Long hospitalId = jwtUtil.extractHospitalId(token);

            System.out.println("Admin registering user under hospital ID: " + hospitalId);

            if (hospitalId == null) {
                return ResponseEntity.badRequest().body("Hospital ID missing from token.");
            }

            Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
            if (hospitalOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid hospital ID.");
            }

            UserRole roleEnum = UserRole.valueOf(dto.getRole().toUpperCase());

            long count = userRepository.countByRoleAndHospital_Id(roleEnum, hospitalId);

            if (roleEnum == UserRole.DOCTOR && count >= 5) {
                return ResponseEntity.badRequest().body("Maximum 5 doctors allowed per hospital.");
            } else if (roleEnum != UserRole.DOCTOR && count >= 1) {
                return ResponseEntity.badRequest().body("Only one " + roleEnum + " allowed per hospital.");
            }

            // Create user
            User user = new User();
            user.setUsername(dto.getUsername());
//            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setPassword(dto.getPassword());


            user.setEmail(dto.getEmail());
            user.setRole(roleEnum);
            user.setHospital(hospitalOpt.get());

            userService.register(user);

            // Create JWT for new user
            String tokenNew = jwtUtil.generateAccessToken(user.getUsername(), roleEnum.name(), hospitalId);
            System.out.println("🔑 Token: " + authHeader);
            System.out.println("👤 Username: " + adminUsername);
            System.out.println("🏥 Hospital ID: " + hospitalId);

            return ResponseEntity.ok(Map.of(
                    "token", tokenNew,
                    "role", roleEnum.name(),
                    "username", user.getUsername(),
                    "hospitalId", hospitalId
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @GetMapping("/role-counts")
    public ResponseEntity<?> getRoleCounts(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("Authorization token missing or malformed.");
        }

        try {
            String token = authHeader.substring(7);
            Long hospitalId = jwtUtil.extractHospitalId(token);

            Map<String, Long> roleCounts = new HashMap<>();
            for (UserRole role : UserRole.values()) {
                long count = userRepository.countByRoleAndHospital_Id(role, hospitalId);
                roleCounts.put(role.name(), count);
            }

            return ResponseEntity.ok(roleCounts);
        } catch (JwtException ex) {
            return ResponseEntity.status(403).body("Invalid JWT: " + ex.getMessage());
        }
    }

}
