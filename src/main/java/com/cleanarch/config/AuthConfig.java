package com.cleanarch.config;

import com.cleanarch.adapters.out.security.JwtGeneratorAdapter;
import com.cleanarch.adapters.out.security.JwtTokenParser;
import com.cleanarch.adapters.out.security.jwt.JwtAdapter;
import com.cleanarch.application.usecases.*;
import com.cleanarch.application.port.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public LoginUserUseCase loginUserUseCase(
            UserRepositoryPort userRepository,
            SessionRepositoryPort sessionRepository,
            TokenGeneratorPort tokenGenerator,
            PasswordHasherPort passwordHasher,
            TokenHasherPort tokenHasher
    ){
        return new LoginUserUseCase(
                userRepository,
                sessionRepository,
                tokenGenerator,
                passwordHasher,
                tokenHasher
        );
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            SessionRepositoryPort sessionRepository,
            TokenParserPort tokenParser,
            TokenGeneratorPort tokenGenerator,
            TokenHasherPort tokenHasher
    ){
        return new RefreshTokenUseCase(
                sessionRepository,
                tokenParser,
                tokenGenerator,
                tokenHasher
        );
    }

    @Bean
    public LogoutUseCase logoutUseCase(
            SessionRepositoryPort sessionRepository
    ){
        return new LogoutUseCase(sessionRepository);
    }

    @Bean
    public JwtAdapter jwtAdapter(JwtProperties jwtProperties)
    {
        return new JwtAdapter(jwtProperties.getSecret());
    }

    @Bean
    public TokenParserPort tokenParser(JwtProperties jwtProperties)
    {
        return new JwtTokenParser(jwtProperties.getSecret());
    }

    @Bean
    public TokenGeneratorPort tokenGenerator(JwtProperties jwtProperties)
    {
        return new JwtGeneratorAdapter(jwtProperties.getSecret());
    }
}