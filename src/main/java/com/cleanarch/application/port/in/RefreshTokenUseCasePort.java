package com.cleanarch.application.port.in;

public interface RefreshTokenUseCasePort {

    RefreshTokenResult execute(RefreshTokenCommand command);

}
