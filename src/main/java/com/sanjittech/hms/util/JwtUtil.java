package com.sanjittech.hms.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // 🔐 Access Token valid for 1 day
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 1 day

    // 🔁 Refresh Token valid for 7 days
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    // ✅ Generate access token (with username + role)
    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // ✅ Generate refresh token (username only)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // ✅ Extract username from token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ Extract all claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Validate token (true if valid, false if expired or invalid)
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
}
