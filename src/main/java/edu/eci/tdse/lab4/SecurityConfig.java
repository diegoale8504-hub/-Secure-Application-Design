package edu.eci.tdse.lab4;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitado para APIs REST simples
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/", "/index.html").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}