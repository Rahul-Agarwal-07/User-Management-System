package com.cleanarch.domain.exception;

public class RefreshTokenReuseDetectionException extends DomainException {
    public RefreshTokenReuseDetectionException() { super("Token Reuse Detected"); }
    public RefreshTokenReuseDetectionException(String message) {
        super(message);
    }
}
