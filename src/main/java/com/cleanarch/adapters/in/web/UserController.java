package com.cleanarch.adapters.in.web;

import com.cleanarch.adapters.in.web.dto.ChangePasswordRequest;
import com.cleanarch.adapters.in.web.dto.CreateUserRequest;
import com.cleanarch.adapters.in.web.dto.CreateUserResponse;
import com.cleanarch.adapters.out.security.principal.AuthUser;
import com.cleanarch.application.port.in.ChangePasswordCommand;
import com.cleanarch.application.port.in.ChangePasswordUseCasePort;
import com.cleanarch.application.port.in.CreateUserCommand;
import com.cleanarch.application.port.in.CreateUserUseCasePort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final ChangePasswordUseCasePort changePasswordUseCase;

    public UserController(CreateUserUseCasePort createUserUseCase, ChangePasswordUseCasePort changePasswordUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
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

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid ChangePasswordRequest request
    ){
        ChangePasswordCommand command = new ChangePasswordCommand(
                authUser.userId(),
                request.currentPassword(),
                request.newPassword()
        );

        changePasswordUseCase.execute(command);

        return ResponseEntity.noContent().build();

    }

}
