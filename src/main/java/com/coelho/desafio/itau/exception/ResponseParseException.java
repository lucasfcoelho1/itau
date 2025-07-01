package com.coelho.desafio.itau.exception;

public class ResponseParseException extends RuntimeException {
    public ResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
