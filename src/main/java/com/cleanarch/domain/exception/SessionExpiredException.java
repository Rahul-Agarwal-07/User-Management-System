package com.cleanarch.domain.exception;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException() { super("Session Expired"); }
    public SessionExpiredException(String message) {
        super(message);
    }
}
