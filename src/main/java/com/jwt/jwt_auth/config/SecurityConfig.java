package com.jwt.jwt_auth.config;

import com.jwt.jwt_auth.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 🔴 JWT API → CSRF OFF
            .csrf(csrf -> csrf.disable())

            // 🔴 Enable CORS (CrosConfig se lega)
            .cors(cors -> {})

            // 🔴 PUBLIC vs PROTECTED APIs
            .authorizeHttpRequests(auth -> auth
                // ✅ PUBLIC (NO TOKEN REQUIRED)
                .requestMatchers(
                        "/api/users/register",
                        "/api/users/login",
                        "/api/users/forgot-password",
                        "/api/users/reset-password"
                ).permitAll()

                // 🔒 Baaki sab JWT protected
                .anyRequest().authenticated()
            )

            // 🔴 Stateless JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔴 JWT Filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}