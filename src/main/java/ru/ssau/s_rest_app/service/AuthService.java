package ru.ssau.s_rest_app.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ssau.s_rest_app.dto.AuthResponse;
import ru.ssau.s_rest_app.dto.LoginRequest;
import ru.ssau.s_rest_app.dto.RegisterRequest;
import ru.ssau.s_rest_app.entity.*;
import ru.ssau.s_rest_app.exception.UserAlreadyExistsException;
import ru.ssau.s_rest_app.repository.*;
import ru.ssau.s_rest_app.security.JwtUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserStatusRepository userStatusRepository;
    private final OrganizerRequestRepository organizerRequestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    //Регистрация

    @Transactional
    public AuthResponse register(RegisterRequest request) throws UserAlreadyExistsException {

        // Проверка на дубликат email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Роль по умолчанию — USER
        Role role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("Роль USER не найдена в БД"));

        // Статус — ACTIVE
        UserStatus status = userStatusRepository.findByUserStatusName("ACTIVE")
                .orElseThrow(() -> new IllegalStateException("Статус ACTIVE не найден в БД"));

        // Создаём пользователя
        User user = new User();
        user.setFio(request.getFio());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setHasDisability(request.isHasDisability());
        user.setRole(role);
        user.setUserStatus(status);

        User savedUser = userRepository.save(user);

        // Если пользователь хочет стать организатором — создаём заявку
        if (request.getOrganizerRequestText() != null
                && !request.getOrganizerRequestText().isBlank()) {

            RequestStatus pendingStatus = requestStatusRepository
                    .findByRequestStatusName("PENDING")
                    .orElseThrow(() -> new IllegalStateException("Статус PENDING не найден в БД"));

            OrganizerRequest organizerRequest = new OrganizerRequest();
            organizerRequest.setUser(savedUser);
            organizerRequest.setRequestStatus(pendingStatus);
            organizerRequest.setRequestText(request.getOrganizerRequestText());
            organizerRequest.setSubmittedAt(LocalDateTime.now());

            organizerRequestRepository.save(organizerRequest);
        }

        String token = jwtUtils.generateToken(savedUser.getEmail());

        return new AuthResponse(
                token,
                savedUser.getFio(),
                savedUser.getEmail(),
                role.getRoleName(),
                savedUser.getIdUser()
        );
    }

    //Вход

    public AuthResponse login(LoginRequest request) {
        // Spring Security проверяет email + пароль
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        String token = jwtUtils.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getFio(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getIdUser()
        );
    }
}
