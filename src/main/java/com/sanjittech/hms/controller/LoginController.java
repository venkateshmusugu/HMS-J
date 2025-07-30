package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.service.UserService;
import com.sanjittech.hms.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class LoginController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String role = payload.get("role");
        System.out.println("🔍 Incoming login payload: " + payload);
        try {
            Optional<User> optionalUser = userRepository.findByUsernameOrEmail(username,username);


            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            User user = optionalUser.get();

            if (!user.getRole().name().equalsIgnoreCase(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Role mismatch"));
            }

            // ✅ Load UserDetails from our defined UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ✅ Manually verify password to avoid triggering recursion
            // 🧪 DEBUG: Check if raw and hashed passwords match
            System.out.println("🔑 Raw password: " + password);
            System.out.println("🔐 Encoded from DB: " + user.getPassword());
            boolean match = passwordEncoder.matches(password, user.getPassword());
            System.out.println("✅ Password match result: " + match);
// ✅ DEBUG END

            if (!match) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }

            Long hospitalId = user.getHospital() != null ? user.getHospital().getId() : null;

            String accessToken = jwtUtil.generateAccessToken(username, user.getRole().name(), hospitalId);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("role", user.getRole().name());
            response.put("username", username);
            response.put("hospitalId", hospitalId);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body("Missing refresh token");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);

            if (jwtUtil.validateToken(refreshToken)) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Long hospitalId = user.getHospital() != null ? user.getHospital().getId() : null;

                String newAccessToken = jwtUtil.generateAccessToken(username, user.getRole().name(), hospitalId);
                String newRefreshToken = jwtUtil.generateRefreshToken(username);

                Map<String, Object> tokens = new HashMap<>();
                tokens.put("accessToken", newAccessToken);
                tokens.put("refreshToken", newRefreshToken);
                tokens.put("username", username);
                tokens.put("role", user.getRole().name());
                tokens.put("hospitalId", hospitalId);

                return ResponseEntity.ok(tokens);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, null);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/debug-role")
    public ResponseEntity<?> debugRole() {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }
}
