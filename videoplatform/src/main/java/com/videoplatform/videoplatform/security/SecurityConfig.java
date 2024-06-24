package com.videoplatform.videoplatform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @SuppressWarnings("removal")
    @Bean

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/api/user/password/reset", "/api/users/password/reset/confirm").permitAll()
                        .requestMatchers("/api/user/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin()
                .and()
                .logout()
                .permitAll();
        return http.build();
    }


}
