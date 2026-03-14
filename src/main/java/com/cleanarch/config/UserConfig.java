package com.cleanarch.config;

import com.cleanarch.application.port.in.ChangePasswordUseCasePort;
import com.cleanarch.application.port.in.CreateUserUseCasePort;
import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.application.port.out.UserRepositoryPort;
import com.cleanarch.application.usecases.ChangePasswordUseCase;
import com.cleanarch.application.usecases.CreateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public CreateUserUseCasePort createUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort
    ){
        return new CreateUserUseCase(userRepositoryPort, passwordHasherPort);
    }

    @Bean
    public ChangePasswordUseCasePort changePasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            SessionRepositoryPort sessionRepositoryPort,
            PasswordHasherPort passwordHasherPort
    ){
        return new ChangePasswordUseCase(userRepositoryPort, sessionRepositoryPort, passwordHasherPort);
    }

}
