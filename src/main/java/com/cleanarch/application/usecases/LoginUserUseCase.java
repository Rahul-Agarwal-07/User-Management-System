package com.cleanarch.application.usecases;

import com.cleanarch.adapters.in.web.dto.LoginUserResponse;
import com.cleanarch.application.port.in.LoginUseCasePort;
import com.cleanarch.application.port.in.LoginUserCommand;
import com.cleanarch.application.port.in.LoginUserResult;
import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.TokenGeneratorPort;
import com.cleanarch.application.port.out.UserRepositoryPort;
import com.cleanarch.domain.exception.InvalidCredentialsException;
import com.cleanarch.domain.model.User;

public class LoginUserUseCase implements LoginUseCasePort {


    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final PasswordHasherPort passwordHasher;

    public LoginUserUseCase(UserRepositoryPort userRepository,TokenGeneratorPort tokenGenerator, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public LoginUserResult execute(LoginUserCommand command) {

        //check if email exists
        User user = userRepository.findByEmail(command.email())
                .orElseThrow( () -> new InvalidCredentialsException("Invalid Email or Password"));

        //check if password matches
        boolean valid = passwordHasher.matches(command.password(), user.getPasswordHash());

        if(!valid) throw new InvalidCredentialsException("Invalid Email or Password");

        //generate tokens (access, refresh)
        String accessToken = tokenGenerator.generateAccessToken(user.getId(), user.getStatus().name());
        String refreshToken = tokenGenerator.generateRefreshToken(user.getId());

        return new LoginUserResult(
                accessToken,
                refreshToken,
                user.getId(),
                user.getStatus().name()
        );
    }
}
