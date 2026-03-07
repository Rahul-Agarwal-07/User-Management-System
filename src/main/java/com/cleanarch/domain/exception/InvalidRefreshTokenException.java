package com.cleanarch.domain.exception;

public class InvalidRefreshTokenException extends DomainException {
    public InvalidRefreshTokenException() { super("Invalid Refresh Token"); }
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
