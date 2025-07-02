package com.sanjittech.hms.filter;

import com.sanjittech.hms.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println("🟡 Incoming Authorization header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("🔐 Extracted JWT Token: " + token);

            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.extractAllClaims(token);
                    String username = claims.getSubject();
                    String role = claims.get("role", String.class);

                    System.out.println("✅ Parsed JWT - Username: " + username + ", Role: " + role);

                    List<GrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ SecurityContext set for user: " + username);
                }
            } catch (ExpiredJwtException e) {
                System.out.println("⛔ JWT expired: " + e.getMessage());
            } catch (JwtException e) {
                System.out.println("❌ JWT is invalid: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("⚠️ Error parsing JWT: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No Bearer token present in Authorization header.");
        }

        filterChain.doFilter(request, response);
    }
}
