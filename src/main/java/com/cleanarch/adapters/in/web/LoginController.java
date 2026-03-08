package com.cleanarch.adapters.in.web;

import com.cleanarch.adapters.in.web.dto.LoginUserRequest;
import com.cleanarch.adapters.in.web.dto.LoginUserResponse;
import com.cleanarch.application.port.in.LoginUseCasePort;
import com.cleanarch.application.port.in.LoginUserCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginUseCasePort loginUseCase;


    public LoginController(LoginUseCasePort loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping
    ResponseEntity<LoginUserResponse> loginUser(@Valid LoginUserRequest request)
    {
        LoginUserCommand command = new LoginUserCommand(
                request.email().trim().toLowerCase(),
                request.password().trim()
        );
    }
}
