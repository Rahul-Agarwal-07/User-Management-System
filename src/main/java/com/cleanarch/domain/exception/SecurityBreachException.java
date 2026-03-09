package com.cleanarch.domain.exception;

public class SecurityBreachException extends DomainException {
    public SecurityBreachException() { super("Account Security Compromised. All access revoked.");}
    public SecurityBreachException(String message) {
        super(message);
    }
}
