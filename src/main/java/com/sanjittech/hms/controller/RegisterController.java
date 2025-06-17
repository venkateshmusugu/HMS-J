package com.sanjittech.hms.controller;

import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.dto.RegisterRequest;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.service.UserService;
import com.sanjittech.hms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;  // ✅ ADD THIS

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest dto) {
        System.out.println("Received registration: " + dto.getUsername() + ", role: " + dto.getRole());

        try {
            UserRole roleEnum = UserRole.valueOf(dto.getRole().toUpperCase());
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setRole(roleEnum);

            userService.register(user);

            // ✅ Generate token
            String token = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());


            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "User registered successfully");

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", user.getRole().name(),
                    "username", user.getUsername()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
