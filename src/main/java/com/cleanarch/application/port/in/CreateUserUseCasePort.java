package com.cleanarch.application.port.in;

import java.util.UUID;

public interface CreateUserUseCasePort {

    UUID execute(CreateUserCommand command);

}
