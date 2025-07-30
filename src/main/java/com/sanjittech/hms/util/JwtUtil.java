package com.sanjittech.hms.util;

import com.sanjittech.hms.model.License;
import com.sanjittech.hms.repository.LicenseRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private LicenseRepository licenseRepository;

    @Value("${jwt.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 1 day
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days


    public String generateAccessToken(String username, String role, Long hospitalId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("hospitalId", hospitalId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // ✅ Generate Refresh Token
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // ✅ Extract all claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractHospitalId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("hospitalId", Long.class);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("⛔ Token expired: " + e.getMessage());
        } catch (JwtException e) {
            System.out.println("❌ Invalid token: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Unexpected token error: " + e.getMessage());
        }
        return false;
    }

    public boolean isLicenseValid(Long hospitalId) {
        License license = licenseRepository.findByHospital_Id(hospitalId);
        return license != null && license.isActive() && LocalDate.now().isBefore(license.getEndDate());
    }

    public String generateToken(String username, String role, Long hospitalId) {
        return generateAccessToken(username, role, hospitalId);
    }
    public Claims extractClaims(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return Jwts.parser()
                    .setSigningKey(secret) // Make sure this matches your JWT secret
                    .parseClaimsJws(token)
                    .getBody();
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }
}
