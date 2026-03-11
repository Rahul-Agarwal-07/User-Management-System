package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.LoginUserCommand;
import com.cleanarch.application.port.in.LoginUserResult;
import com.cleanarch.application.port.out.*;
import com.cleanarch.domain.exception.InvalidCredentialsException;
import com.cleanarch.domain.model.Session;
import com.cleanarch.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LoginUseCaseTest {

    private UserRepositoryPort userRepository;
    private SessionRepositoryPort sessionRepository;
    private TokenGeneratorPort tokenGenerator;
    private PasswordHasherPort passwordHasher;
    private TokenHasherPort tokenHasher;
    private LoginUserUseCase loginUserUseCase;

    @BeforeEach
    void beforeEach()
    {
        userRepository = mock(UserRepositoryPort.class);
        sessionRepository = mock(SessionRepositoryPort.class);
        tokenGenerator = mock(TokenGeneratorPort.class);
        passwordHasher = mock(PasswordHasherPort.class);
        tokenHasher = mock(TokenHasherPort.class);

        loginUserUseCase = new LoginUserUseCase(
                userRepository,
                sessionRepository,
                tokenGenerator,
                passwordHasher,
                tokenHasher
        );
    }

    @Test
    public void should_generate_tokens_when_credentials_are_valid()
    {
        User user = User.create(
                "Rahul Agarwal",
                "rahul@example.com",
                "hashed-password",
                "999999999"
        );

        LoginUserCommand command = new LoginUserCommand(
                "rahul@example.com",
                "password",
                "chrome",
                "chrome windows",
                "0.0.0.1"
        );

        when(userRepository.findByEmail(command.email()))
                .thenReturn(Optional.of(user));

         when(passwordHasher.matches(command.password(), user.getPasswordHash()))
                 .thenReturn(true);

         when(tokenGenerator.generateAccessToken(
                 eq(user.getId()),
                 any(UUID.class),
                 eq("USER")
         )).thenReturn("access-token");

        when(tokenGenerator.generateRefreshToken(
                eq(user.getId()),
                any(UUID.class)
        )).thenReturn("refresh-token");


        LoginUserResult result = loginUserUseCase.execute(command);

        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals(user.getId(), result.userId());
        assertEquals("USER", result.role());

        verify(passwordHasher).matches(any(), any());

        verify(tokenGenerator).generateRefreshToken(any(), any());
        verify(tokenGenerator).generateAccessToken(any(), any(), any());

        verify(tokenHasher).hash(any());

        verify(sessionRepository).save(any(Session.class));

    }

    @Test
    public void should_throw_invalid_credentials_when_email_not_found()
    {

        when(userRepository.findByEmail("rahul@example.com"))
                .thenReturn(Optional.empty());

        LoginUserCommand command = new LoginUserCommand(
                "rahul@example.com",
                "secret123",
                "chrome",
                "chrome windows",
                "0.0.0.1"
        );

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUserUseCase.execute(command)
        );
    }

    @Test
    public void should_throw_invalid_credentials_when_password_mismatch()
    {

        User user = User.create(
                "Rahul Agarwal",
                "rahul@example.com",
                "hashed-password",
                "999999999"
        );

        LoginUserCommand command = new LoginUserCommand(
                "rahul@example.com",
                "secret123",
                "chrome",
                "chrome windows",
                "0.0.0.1"
        );

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordHasher.matches(command.password(), user.getPasswordHash()))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUserUseCase.execute(command)
        );

        verify(passwordHasher).matches(any(), any());
    }

}
