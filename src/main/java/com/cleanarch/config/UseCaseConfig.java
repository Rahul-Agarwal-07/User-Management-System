package com.cleanarch.config;

import com.cleanarch.application.port.in.CreateUserUseCasePort;
import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.UserRepositoryPort;
import com.cleanarch.application.usecases.CreateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateUserUseCasePort createUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort
    ){
        return new CreateUserUseCase(userRepositoryPort, passwordHasherPort);
    }

}
