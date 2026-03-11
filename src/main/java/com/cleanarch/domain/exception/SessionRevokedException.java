package com.cleanarch.domain.exception;

public class SessionRevokedException extends DomainException {
    public SessionRevokedException() { super("Session is Revoked");}
    public SessionRevokedException(String message) {
        super(message);
    }
}
