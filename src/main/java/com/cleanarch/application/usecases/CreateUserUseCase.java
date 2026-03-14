package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.CreateUserCommand;
import com.cleanarch.application.port.in.CreateUserUseCasePort;
import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.UserRepositoryPort;
import com.cleanarch.domain.exception.InvalidUserDataException;
import com.cleanarch.domain.exception.UserAlreadyExistsException;
import com.cleanarch.domain.model.User;
import jakarta.transaction.Transactional;

import java.util.UUID;

@Transactional
public class CreateUserUseCase implements CreateUserUseCasePort {


    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    public CreateUserUseCase(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher)
    {
        this.passwordHasher = passwordHasher;
        this.userRepository = userRepository;
    }

    @Override
    public UUID execute(CreateUserCommand command) {

        //first step is to check email uniqueness
        userRepository.findByEmail(command.email())
                .ifPresent(u -> {
                    throw new UserAlreadyExistsException("Email Already Exists");
                });

        //next check phone uniqueness if present
        if(command.phone() != null)
        {
            userRepository.findByPhone(command.phone())
                    .ifPresent(u -> {
                        throw new UserAlreadyExistsException("Phone Already Exists");
                    });
        }

        String passHash = passwordHasher.hash(command.password());

        User user = User.create(
                command.name(),
                command.email(),
                passHash,
                command.phone()
        );

        return userRepository.save(user);
    }
}
