package com.sanjittech.hms.config;

import com.sanjittech.hms.filter.JwtFilter;
import com.sanjittech.hms.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth

                            // ✅ Public endpoints (keep these first)
                            .requestMatchers(
                                    "/api/users/login",
                                    "/api/users/register",
                                    "/api/users/refresh-token",
                                    "/api/users/otp/**",
                                    "/api/user/**",
                                    "/api/users/role-counts",
                                    "/uploads/**",
                                    "/api/razorpay/**",
                                    "/api/payment/verify",
                                    "/api/users/otp/send",
                                    "/api/users/otp/verify"
                            ).permitAll()

                            // ✅ Allow unauthenticated access to branding and hospital icon upload
                            .requestMatchers(HttpMethod.GET, "/api/hospitals/branding").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/hospitals/upload").permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/hospitals/**").permitAll()

                            // 🔒 Authenticated role-based endpoints below
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")

                            .requestMatchers("/api/doctors/**", "/api/departments/**")
                            .hasAnyRole("DOCTOR", "ADMIN", "RECEPTIONIST", "SURGERY")

                            .requestMatchers("/api/appointments/**", "/api/appointments/upcoming", "/api/appointments/cancel/**")
                            .hasAnyRole("RECEPTIONIST", "DOCTOR", "ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasRole("ADMIN")

                            .requestMatchers("/api/doctor-logs/**").hasRole("DOCTOR")
                            .requestMatchers("/api/surgeries/**", "/api/admin/**")
                            .hasAnyRole("SURGERY", "DOCTOR", "ADMIN")
                            .requestMatchers("/api/surgery-appointments/**", "/api/surgery-logs/**")
                            .hasAnyRole("ADMIN", "SURGERY", "DOCTOR", "RECEPTIONIST")

                            .requestMatchers("/api/surgery-medications/**")
                            .hasAnyRole("SURGERY", "DOCTOR")

                            .requestMatchers("/api/medical-bills/**", "/api/medicines/**")
                            .hasAnyRole("BILLING", "ADMIN", "DOCTOR", "SURGERY")

                            .requestMatchers("/api/patients/**")
                            .hasAnyRole("BILLING", "ADMIN", "DOCTOR", "SURGERY", "RECEPTIONIST")

                            // ✅ These are authenticated, so not allowed publicly
                            .requestMatchers("/api/hospitals/me")
                            .hasAnyRole("BILLING", "ADMIN", "DOCTOR", "SURGERY", "RECEPTIONIST")

                            .anyRequest().authenticated()
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

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
