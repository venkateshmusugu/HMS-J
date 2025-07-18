package com.sanjittech.hms.config;

import com.sanjittech.hms.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(@Lazy JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register", "/api/users/refresh-token", "/api/patients/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/role-counts").permitAll()
                        .requestMatchers("/api/doctors/**","/api/departments/**").hasAnyRole("DOCTOR", "ADMIN","RECEPTIONIST","SURGERY")
                        .requestMatchers("/api/users/otp/**","api/user/**").permitAll()
                        .requestMatchers("/api/appointments/**", "/api/appointments/upcoming", "/api/appointments/cancel/**")
                        .hasAnyRole("RECEPTIONIST", "DOCTOR", "ADMIN")
                        .requestMatchers("/api/doctor-logs/**", "/api/surgeries/**", "/api/appointments/cancel/**")
                        .hasAnyRole("DOCTOR", "SURGERY")
                        .requestMatchers("/api/surgeries/**").hasAnyRole("SURGERY", "DOCTOR")
                        .requestMatchers("/api/surgery-logs/**", "/api/surgery-appointments/**")
                        .hasAnyRole("ADMIN", "SURGERY", "DOCTOR", "RECEPTIONIST")
                        .requestMatchers("/api/surgery-medications/**")  // ✅ ← ADD THIS LINE
                        .hasAnyRole("SURGERY","DOCTOR")
                        .requestMatchers("/api/medical-bills/**", "/api/patients/**", "/api/medicines/**")
                        .hasAnyRole("BILLING", "ADMIN", "DOCTOR", "SURGERY")
                        .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasRole("ADMIN")
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/hospital-config/**").hasAnyRole("BILLING", "ADMIN", "DOCTOR", "SURGERY","RECEPTIONIST")
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3002"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
