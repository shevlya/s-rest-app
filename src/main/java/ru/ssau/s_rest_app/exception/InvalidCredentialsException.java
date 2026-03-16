package ru.ssau.s_rest_app.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException() {
        super("Неверный email или пароль", HttpStatus.UNAUTHORIZED);
    }
}
