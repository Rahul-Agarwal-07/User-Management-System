package com.cleanarch.config;

import com.cleanarch.adapters.out.security.JwtTokenAdapter;
import com.cleanarch.adapters.out.security.JwtTokenParser;
import com.cleanarch.application.port.out.TokenGeneratorPort;
import com.cleanarch.application.port.out.TokenParserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public TokenParserPort tokenParser(JwtProperties jwtProperties)
    {
        return new JwtTokenParser(jwtProperties.getSecret());
    }

    @Bean
    public TokenGeneratorPort tokenGenerator(JwtProperties jwtProperties)
    {
        return new JwtTokenAdapter(jwtProperties.getSecret());
    }

}
