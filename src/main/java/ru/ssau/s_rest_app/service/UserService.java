package ru.ssau.s_rest_app.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.s_rest_app.dto.*;
import ru.ssau.s_rest_app.entity.*;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventCategoryRepository categoryRepository;
    private final UserInterestRepository userInterestRepository;
    private final OrganizerRequestRepository organizerRequestRepository;
    private final RequestStatusRepository  requestStatusRepository;

    // Получить текущего пользователя по email из JWT

    public User getCurrentUser() throws AppException {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    // Профиль текущего пользователя
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile() throws AppException {
        User user = getCurrentUser();
        String avatarUrl = user.getAvatar() != null
                ? user.getAvatar().getAvatarUrl()
                : null;

        List<EventCategoryDto> categories = userInterestRepository
                .findByUserId(user.getIdUser())
                .stream()
                .map(ui -> {
                    EventCategory c = ui.getIdEventCategory();
                    return new EventCategoryDto(
                            c.getIdEventCategory(),
                            c.getEventCategoryName(),
                            c.getColorCode());
                })
                .toList();

        // Последняя заявка на организатора
        OrganizerRequestDto organizerRequestDto = organizerRequestRepository
                .findLatestByUserId(user.getIdUser())
                .map(r -> new OrganizerRequestDto(
                        r.getIdOrganizerRequest(),
                        r.getRequestText(),
                        r.getRequestStatus().getRequestStatusName(),
                        r.getReviewComment()))
                .orElse(null);

        return new UserProfileResponse(
                user.getIdUser(),
                user.getFio(),
                user.getEmail(),
                user.getRole().getRoleName(),
                avatarUrl,
                user.getBirthDate(),
                categories,
                user.getHasDisability(),
                organizerRequestDto
        );
    }

    // Обновить аватар

    @Transactional
    public UserProfileResponse updateAvatar(AvatarUpdateRequest request) throws AppException {
        User user = getCurrentUser();

        Avatar avatar = user.getAvatar();

        if (avatar == null) {
            // Создаём новую запись в таблице avatar
            avatar = new Avatar();
        }
        // Обновляем URL (или создаём новый)
        avatar.setAvatarUrl(request.getAvatarUrl());
        Avatar savedAvatar = avatarRepository.save(avatar);

        // Связываем с пользователем
        user.setAvatar(savedAvatar);
        userRepository.save(user);

        return getProfile();
    }

    // Сменить пароль

    @Transactional
    public void changePassword(ChangePasswordRequest request) throws AppException {
        User user = getCurrentUser();

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new AppException("Текущий пароль введён неверно", HttpStatus.BAD_REQUEST);
        }

        // Новый пароль не должен совпадать со старым
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new AppException("Новый пароль должен отличаться от текущего", HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ── Обновить дату рождения ────────────────────────────────

    @Transactional
    public UserProfileResponse updateBirthday(@Valid UpdateBirthdayRequest request) throws AppException {
        User user = getCurrentUser();
        user.setBirthDate(request.getBirthDate());
        userRepository.save(user);
        return getProfile();
    }

    // ── Обновить любимые категории ────────────────────────────

    @Transactional
    public UserProfileResponse updateCategories(UpdateCategoriesRequest request) throws AppException {
        User user = getCurrentUser();

        // Удаляем старые интересы и записываем новые
        userInterestRepository.deleteAllByUserId(user.getIdUser());

        for (Long categoryId : request.getCategoryIds()) {
            EventCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(
                            "Категория не найдена: " + categoryId, HttpStatus.NOT_FOUND));

            UserInterest interest = new UserInterest();
            interest.setIdUser(user);
            interest.setIdEventCategory(category);
            userInterestRepository.save(interest);
        }

        return getProfile();
    }

    // ── Обновить статус ОВЗ ───────────────────────────────────

    @Transactional
    public UserProfileResponse updateDisability(UpdateDisabilityRequest request) throws AppException {
        User user = getCurrentUser();
        user.setHasDisability(request.isHasDisability());
        userRepository.save(user);
        return getProfile();
    }

    // ── Подать заявку на организатора ─────────────────────────

    @Transactional
    public OrganizerRequestDto submitOrganizerRequest(RegisterRequest request) throws AppException {
        User user = getCurrentUser();

        // Нельзя подать повторную заявку если уже есть PENDING
        organizerRequestRepository.findLatestByUserId(user.getIdUser())
                .ifPresent(existing -> {
                    String status = existing.getRequestStatus().getRequestStatusName();
                    if ("PENDING".equals(status)) {
                        throw new RuntimeException(
                                "У вас уже есть заявка на рассмотрении");
                    }
                });

        RequestStatus pendingStatus = requestStatusRepository
                .findByRequestStatusName("PENDING")
                .orElseThrow(() -> new AppException("Статус PENDING не найден", HttpStatus.INTERNAL_SERVER_ERROR));

        OrganizerRequest organizerRequest = new OrganizerRequest();
        organizerRequest.setUser(user);
        organizerRequest.setRequestStatus(pendingStatus);
        organizerRequest.setRequestText(request.getOrganizerRequestText());
        organizerRequest.setSubmittedAt(LocalDateTime.now());

        OrganizerRequest saved = organizerRequestRepository.save(organizerRequest);

        return new OrganizerRequestDto(
                saved.getIdOrganizerRequest(),
                saved.getRequestText(),
                saved.getRequestStatus().getRequestStatusName(),
                saved.getReviewComment()
        );
    }
}
