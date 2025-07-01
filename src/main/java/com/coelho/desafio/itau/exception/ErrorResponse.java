package com.coelho.desafio.itau.exception;

public class ErrorResponse {
    private final int status;
    private final boolean success;
    private final String reason;

    public ErrorResponse(int status, boolean success, String reason) {
        this.status = status;
        this.success = success;
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getReason() {
        return reason;
    }
}