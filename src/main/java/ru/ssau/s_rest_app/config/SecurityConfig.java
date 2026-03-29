package ru.ssau.s_rest_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // ← Отключить CSRF для API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()  // ← Открыть API (для разработки)
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
