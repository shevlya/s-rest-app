package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.AuthResponse;
import ru.ssau.s_rest_app.dto.LoginRequest;
import ru.ssau.s_rest_app.dto.RegisterRequest;
import ru.ssau.s_rest_app.exception.UserAlreadyExistsException;
import ru.ssau.s_rest_app.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) throws UserAlreadyExistsException {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
