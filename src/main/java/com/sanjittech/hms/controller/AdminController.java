package com.sanjittech.hms.controller;

import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.HospitalRepository;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Utility to extract hospitalId from JWT
    private Long getHospitalIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Claims claims = jwtUtil.extractAllClaims(token);
        return claims.get("hospitalId", Long.class);
    }

    // ✅ GET all users in the same hospital
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        Long hospitalId = getHospitalIdFromToken(request);
        List<User> users = userRepository.findByHospital_Id(hospitalId);
        return ResponseEntity.ok(users);
    }

    // ✅ Get a single user by ID (only if from same hospital)
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest request) {
        Long hospitalId = getHospitalIdFromToken(request);
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if (!user.getHospital().getId().equals(hospitalId)) {
            return ResponseEntity.status(403).body("❌ Unauthorized to access user from another hospital.");
        }

        return ResponseEntity.ok(user);
    }

    // ✅ Update user role (only if same hospital)
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> payload, HttpServletRequest request) {
        Long hospitalId = getHospitalIdFromToken(request);
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        if (!user.getHospital().getId().equals(hospitalId)) {
            return ResponseEntity.status(403).body("❌ Unauthorized to update user from another hospital.");
        }

        String newRole = payload.get("role");
        if (newRole == null || newRole.isBlank()) {
            return ResponseEntity.badRequest().body("Role is required");
        }

        try {
            user.setRole(UserRole.valueOf(newRole.toUpperCase()));
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role: " + newRole);
        }
    }

    // ✅ Delete user (only if from same hospital)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        Long hospitalId = getHospitalIdFromToken(request);
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if (!user.getHospital().getId().equals(hospitalId)) {
            return ResponseEntity.status(403).body("❌ Unauthorized to delete user from another hospital.");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("✅ User deleted successfully");
    }

//    // ✅ Create default admin on app startup (optional)
//    @PostConstruct
//    public void createDefaultAdmin() {
//        Optional<Hospital> defaultHospital = hospitalRepository.findById(1L);
//        if (defaultHospital.isPresent() && userRepository.findByUsername("admin").isEmpty()) {
//            User admin = new User();
//            admin.setUsername("admin");
//            admin.setPassword(passwordEncoder.encode("Admin@123"));
//            admin.setRole(UserRole.ADMIN);
//            admin.setHospital(defaultHospital.get());
//            userRepository.save(admin);
//        }
//    }

    // ✅ Create a new admin for a specific hospital
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminForHospital(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        Long hospitalId = Long.parseLong(request.get("hospitalId"));

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("❌ Username already exists.");
        }

        Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
        if (hospitalOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invalid hospital ID.");
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(UserRole.ADMIN);
        admin.setHospital(hospitalOpt.get());

        userRepository.save(admin);
        return ResponseEntity.ok("✅ Admin user created for hospital ID " + hospitalId);
    }
}
