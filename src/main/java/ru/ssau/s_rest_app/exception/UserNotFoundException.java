package ru.ssau.s_rest_app.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String email) {
        super("Пользователь не найден: " + email, HttpStatus.NOT_FOUND);
    }
}
