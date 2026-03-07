package com.cleanarch.application.port.in;

public interface LoginUseCasePort {

    LoginUserResult execute(LoginUserCommand command);

}
