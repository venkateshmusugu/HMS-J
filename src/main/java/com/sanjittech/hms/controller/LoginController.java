package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String role = payload.get("role");

        try {
            User user = userRepository.findByUsername(username).orElseThrow();

            if (!user.getRole().name().equalsIgnoreCase(role)) {
                return ResponseEntity.status(403).body(Map.of("error", "Role mismatch"));
            }

            String accessToken = jwtUtil.generateAccessToken(username, user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(username);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            tokens.put("role", user.getRole().name());
            tokens.put("username", username);

            return ResponseEntity.ok(tokens);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
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
                // ✅ Fetch user to get their role
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // ✅ Pass both username and role to access token generator
                String newAccessToken = jwtUtil.generateAccessToken(username, user.getRole().name());
                String newRefreshToken = jwtUtil.generateRefreshToken(username);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", newAccessToken);
                tokens.put("refreshToken", newRefreshToken);
                tokens.put("username", username);
                tokens.put("role", user.getRole().name());

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
