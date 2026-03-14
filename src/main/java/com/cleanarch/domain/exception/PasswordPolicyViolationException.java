package com.cleanarch.domain.exception;

public class PasswordPolicyViolationException extends DomainException {
    public PasswordPolicyViolationException() { super("Password Policy Violated");}
    public PasswordPolicyViolationException(String message) {
        super(message);
    }
}
