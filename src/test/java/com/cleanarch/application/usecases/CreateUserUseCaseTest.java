package com.cleanarch.application.usecases;


import com.cleanarch.application.port.in.CreateUserCommand;
import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.UserRepositoryPort;
import com.cleanarch.domain.exception.UserAlreadyExistsException;
import com.cleanarch.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateUserUseCaseTest {

    private UserRepositoryPort userRepository;
    private PasswordHasherPort passwordHasher;
    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setup()
    {
        userRepository = mock(UserRepositoryPort.class);
        passwordHasher = mock(PasswordHasherPort.class);
        createUserUseCase = new CreateUserUseCase(userRepository, passwordHasher);
    }

    @Test
    void should_user_create_successfully()
    {
        CreateUserCommand command = new CreateUserCommand(
                "Rahul",
                "Rahul@example.com",
                "999999999",
                "mypassword"
        );

        when(userRepository.findByEmail(command.email()))
                .thenReturn(Optional.empty());

        when(userRepository.findByPhone(command.phone()))
                .thenReturn(Optional.empty());

        when(passwordHasher.hash(command.password()))
                .thenReturn("Hashed Password");

        UUID fakeId = UUID.randomUUID();

        when(userRepository.save(any(User.class)))
                .thenReturn(fakeId);


        UUID result = createUserUseCase.execute(command);
        assertEquals(fakeId, result);

        verify(passwordHasher).hash(command.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void should_throw_email_already_exists()
    {
        CreateUserCommand command = new CreateUserCommand(
                "Rahul",
                "Rahul@example.com",
                "999999999",
                "mypassword"
        );

        when(userRepository.findByEmail(command.email()))
                .thenReturn(Optional.of(mock(User.class)));

        assertThrows(
                UserAlreadyExistsException.class,
                () -> createUserUseCase.execute(command)
        );

        verify(userRepository, never()).save(any());
    }

}
