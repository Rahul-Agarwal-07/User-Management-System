package com.cleanarch.config;

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
}