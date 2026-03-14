package com.cleanarch.adapters.in.web;

import com.cleanarch.adapters.in.web.dto.ApiError;
import com.cleanarch.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException exp)
    {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ApiError> handleInvalidUserDataException(InvalidUserDataException exp)
    {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException exp)
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshTokenException(InvalidRefreshTokenException exp)
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(SecurityBreachException.class)
    public ResponseEntity<ApiError> handleSecurityBreachException(SecurityBreachException exp)
    {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ApiError> handleSessionExpiredException(SessionExpiredException exp)
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException exp)
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordException(InvalidPasswordException exp)
    {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(PasswordPolicyViolationException.class)
    public ResponseEntity<ApiError> handlePasswordPolicyViolationException(PasswordPolicyViolationException exp)
    {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(exp.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("Internal Server Error"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Validation failed",
                        "fields", errors
                ));
    }


}
