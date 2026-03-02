package com.cleanarch.domain.exception;

public abstract class DomainException extends RuntimeException {

    protected DomainException(String msg)
    {
        super(msg);
    }
}
