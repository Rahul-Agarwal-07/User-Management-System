package com.cleanarch.domain.exception;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException() { super("Invalid Password");}
    public InvalidPasswordException(String message) {
        super(message);
    }
}
