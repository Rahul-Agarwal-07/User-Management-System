package com.cleanarch.domain.exception;

public class InvalidTokenException extends DomainException {
    public InvalidTokenException() { super("Invalid Token"); }
    public InvalidTokenException(String message) {
        super(message);
    }
}
