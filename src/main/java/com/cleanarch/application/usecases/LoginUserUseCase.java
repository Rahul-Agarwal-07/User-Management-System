package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.LoginUseCasePort;
import com.cleanarch.application.port.in.LoginUserCommand;
import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.UserRepositoryPort;

public class LoginUserUseCase implements LoginUseCasePort {


    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;

    public LoginUserUseCase(UserRepositoryPort userRepository, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }


    @Override
    public String execute(LoginUserCommand command) {

        //check if email exists
        if(userRepository.findByEmail(command.email()))

        return "";
    }
}
