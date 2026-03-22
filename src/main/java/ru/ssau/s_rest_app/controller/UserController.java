package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.*;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserProfileResponse getProfile() throws AppException {
        return userService.getProfile();
    }

    @PatchMapping("/me/avatar")
    public UserProfileResponse updateAvatar(@Valid @RequestBody AvatarUpdateRequest request) throws AppException {
        return userService.updateAvatar(request);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) throws AppException {
        userService.changePassword(request);
    }

    @PatchMapping("/me/birthday")
    public UserProfileResponse updateBirthday(@Valid @RequestBody UpdateBirthdayRequest request) throws AppException {
        return userService.updateBirthday(request);
    }

    @PutMapping("/me/categories")
    public UserProfileResponse updateCategories(@Valid @RequestBody UpdateCategoriesRequest request) throws AppException {
        return userService.updateCategories(request);
    }

    @PatchMapping("/me/disability")
    public UserProfileResponse updateDisability(@RequestBody UpdateDisabilityRequest request) throws AppException {
        return userService.updateDisability(request);
    }

    @PostMapping("/me/organizer-request")
    public OrganizerRequestDto submitOrganizerRequest(@RequestBody RegisterRequest request) throws AppException {
        return userService.submitOrganizerRequest(request);
    }
}
