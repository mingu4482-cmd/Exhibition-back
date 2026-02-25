package com.example.muse.config;

import com.example.muse.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // REST API는 CSRF 끄는 게 일반적
                .csrf(csrf -> csrf.disable())

                // 세션 안 쓰고 JWT만 쓰니까 STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // 로그인/회원가입만 허용
                        .requestMatchers("/api/auth/**").permitAll()

                        // 나머지 API는 전부 로그인 필요
                        .requestMatchers("/api/**").authenticated()

                        // 그 외는 허용 (예: 정적 리소스)
                        .anyRequest().permitAll()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}