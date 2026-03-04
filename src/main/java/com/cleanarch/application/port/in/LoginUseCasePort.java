package com.cleanarch.application.port.in;

import com.cleanarch.application.port.out.PasswordHasherPort;
import com.cleanarch.application.port.out.UserRepositoryPort;

public interface LoginUseCasePort {

    String execute(LoginUserCommand command);

}
