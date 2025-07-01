package com.coelho.desafio.itau.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(ExternalServiceException ex) {
        var body = new ErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                false,
                "Problemas com API externa. Tente novamente mais tarde."
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(ResponseParseException.class)
    public ResponseEntity<ErrorResponse> handleParseError(ResponseParseException ex) {
        var body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                "Problemas internos. Tente novamente mais tarde."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        var body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                "Erro inesperado. Por favor, tente mais tarde."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(ExhaustedRetryException.class)
    public ResponseEntity<ErrorResponse> handleRetryError(ExhaustedRetryException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof ExternalServiceException) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse(502, false, "Problemas com API externa. Tente novamente mais tarde."));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, false, "Erro inesperado após múltiplas tentativas."));
    }
}