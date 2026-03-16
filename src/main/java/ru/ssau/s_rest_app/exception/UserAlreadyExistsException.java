package ru.ssau.s_rest_app.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends AppException {
    public UserAlreadyExistsException(String email) {
        super("Пользователь с email " + " уже существует", HttpStatus.CONFLICT);
    }
}
