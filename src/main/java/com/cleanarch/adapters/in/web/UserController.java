package com.cleanarch.adapters.in.web;

import com.cleanarch.adapters.in.web.dto.CreateUserRequest;
import com.cleanarch.adapters.in.web.dto.CreateUserResponse;
import com.cleanarch.application.port.in.CreateUserCommand;
import com.cleanarch.application.port.in.CreateUserUseCasePort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCasePort createUserUseCase;

    public UserController(CreateUserUseCasePort createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
    {
        UUID id = createUserUseCase.execute(
                new CreateUserCommand(
                        request.name(),
                        request.email(),
                        request.phone(),
                        request.password()
                )
        );

        return ResponseEntity.ok(
                new CreateUserResponse(id)
        );
    }

}
