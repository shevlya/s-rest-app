package ru.ssau.s_rest_app.exception;

import org.springframework.http.HttpStatus;

public class AppException extends Exception {

    private final HttpStatus status;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
