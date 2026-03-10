package com.cleanarch.adapters.in.web;

import com.cleanarch.adapters.in.web.dto.LoginUserRequest;
import com.cleanarch.adapters.in.web.dto.LoginUserResponse;
import com.cleanarch.adapters.in.web.dto.RefreshTokenRequest;
import com.cleanarch.adapters.in.web.dto.RefreshTokenResponse;
import com.cleanarch.application.port.in.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCasePort loginUseCase;
    private final RefreshTokenUseCasePort refreshTokenUseCase;


    public AuthController(LoginUseCasePort loginUseCase, RefreshTokenUseCasePort refreshTokenUseCase) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    @PostMapping("/login")
    ResponseEntity<LoginUserResponse> loginUser(
            @RequestBody @Valid LoginUserRequest request,
            HttpServletRequest httpRequest
    )
    {
        String ipAddress = httpRequest.getRemoteAddr();

        LoginUserCommand command = new LoginUserCommand(
                request.email().trim().toLowerCase(),
                request.password().trim(),
                request.deviceId().trim(),
                request.userAgent().trim(),
                ipAddress.trim()
        );

        LoginUserResult result = loginUseCase.execute(command);

        return ResponseEntity.ok(
                new LoginUserResponse(
                        result.accessToken(),
                        result.refreshToken(),
                        result.userId(),
                        result.role()
                )
        );
    }

    @PostMapping("/refresh")
    ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request
    ){

        RefreshTokenCommand command = new RefreshTokenCommand(
                request.refreshToken()
        );

        RefreshTokenResult result = refreshTokenUseCase.execute(command);

        return ResponseEntity.ok(
                new RefreshTokenResponse(
                        result.refreshToken(),
                        result.accessToken()
                )
        );
    }
}
