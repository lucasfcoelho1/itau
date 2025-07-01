package com.coelho.desafio.itau.exception;

public class RateLimitExceededException extends ExternalServiceException {
    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}