package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.*;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * GET /api/users/me
     * Возвращает профиль текущего авторизованного пользователя
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile() throws AppException {
        return ResponseEntity.ok(userService.getProfile());
    }

    /**
     * PATCH /api/users/me/avatar
     * Сохраняет выбранный аватар в БД
     */
    @PatchMapping("/me/avatar")
    public ResponseEntity<UserProfileResponse> updateAvatar(@Valid @RequestBody AvatarUpdateRequest request) throws AppException {
        return ResponseEntity.ok(userService.updateAvatar(request));
    }

    /**
     * PATCH /api/users/me/password
     */
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) throws AppException {
        userService.changePassword(request);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * PATCH /api/users/me/birthday
     */
    @PatchMapping("/me/birthday")
    public ResponseEntity<UserProfileResponse> updateBirthday(@Valid @RequestBody UpdateBirthdayRequest request) throws AppException {
        return ResponseEntity.ok(userService.updateBirthday(request));
    }

    /**
     * PUT /api/users/me/categories
     */
    @PutMapping("/me/categories")
    public ResponseEntity<UserProfileResponse> updateCategories(@Valid @RequestBody UpdateCategoriesRequest request) throws AppException {
        return ResponseEntity.ok(userService.updateCategories(request));
    }

    /**
     * PATCH /api/users/me/disability
     */
    @PatchMapping("/me/disability")
    public ResponseEntity<UserProfileResponse> updateDisability(@RequestBody UpdateDisabilityRequest request) throws AppException {
        return ResponseEntity.ok(userService.updateDisability(request));
    }

    /**
     * POST /api/users/me/organizer-request
     */
    @PostMapping("/me/organizer-request")
    public ResponseEntity<OrganizerRequestDto> submitOrganizerRequest(@RequestBody RegisterRequest request) throws AppException {
        return ResponseEntity.ok(userService.submitOrganizerRequest(request));
    }
}
