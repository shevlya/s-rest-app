package ru.ssau.s_rest_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserStatusRepository userStatusRepository;
    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final EventFormatRepository formatRepository;
    private final EventStatusRepository eventStatusRepository;
    private final OrganizerRequestRepository organizerRequestRepository;
    private final RequestStatusRepository requestStatusRepository;
    private final EventParticipantRepository participantRepository;
    private final EmailService emailService;

    // ── Статистика дашборда ───────────────────────────────────

    @Transactional(readOnly = true)
    public AdminStatsDto getStats() {
        long users = userRepository.countByRoleName("USER");
        long organizers = userRepository.countByRoleName("ORGANIZER");
        long events = eventRepository.count();
        long pending = eventRepository.countPendingModeration();
        long requests = organizerRequestRepository.countPendingRequests();
        return new AdminStatsDto(users, organizers, events, pending, requests);
    }

    // ── Управление пользователями ─────────────────────────────

    @Transactional(readOnly = true)
    public List<AdminUserDto> getUsers(String search, String role, String status) {
        return userRepository.findForAdmin(search, role, status)
                .stream()
                .map(u -> new AdminUserDto(
                        u.getIdUser(),
                        u.getFio(),
                        u.getEmail(),
                        u.getRole().getRoleName(),
                        u.getUserStatus().getUserStatusName(),
                        u.getHasDisability()
                ))
                .toList();
    }

    @Transactional
    public AdminUserDto updateUserRole(Long userId, UpdateUserRoleRequest req) throws AppException {
        User user = getUserOrThrow(userId);
        Role role = roleRepository.findByRoleName(req.getRoleName())
                .orElseThrow(() -> new AppException("Роль не найдена", HttpStatus.NOT_FOUND));
        user.setRole(role);
        userRepository.save(user);
        return toAdminUserDto(user);
    }

    @Transactional
    public AdminUserDto updateUserStatus(Long userId, UpdateUserStatusRequest req) throws AppException {
        User user = getUserOrThrow(userId);
        UserStatus status = userStatusRepository.findByUserStatusName(req.getStatusName())
                .orElseThrow(() -> new AppException("Статус не найден", HttpStatus.NOT_FOUND));
        user.setUserStatus(status);
        userRepository.save(user);
        return toAdminUserDto(user);
    }

    // ── Заявки на организатора ────────────────────────────────

    @Transactional(readOnly = true)
    public List<OrganizerRequestAdminDto> getPendingOrganizerRequests() {
        return organizerRequestRepository.findByStatusName("PENDING")
                .stream()
                .map(r -> new OrganizerRequestAdminDto(
                        r.getIdOrganizerRequest(),
                        r.getUser().getIdUser(),
                        r.getUser().getFio(),
                        r.getUser().getEmail(),
                        r.getRequestText(),
                        r.getRequestStatus().getRequestStatusName(),
                        r.getSubmittedAt()
                ))
                .toList();
    }

    @Transactional
    public void approveOrganizerRequest(Long requestId) throws AppException {
        OrganizerRequest request = organizerRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException("Заявка не найдена", HttpStatus.NOT_FOUND));

        RequestStatus approved = requestStatusRepository.findByRequestStatusName("APPROVED")
                .orElseThrow(() -> new AppException("Статус не найден", HttpStatus.INTERNAL_SERVER_ERROR));
        Role organizerRole = roleRepository.findByRoleName("ORGANIZER")
                .orElseThrow(() -> new AppException("Роль ORGANIZER не найдена", HttpStatus.INTERNAL_SERVER_ERROR));

        request.setRequestStatus(approved);
        request.setReviewedAt(LocalDateTime.now());
        organizerRequestRepository.save(request);

        // Меняем роль пользователя
        User user = request.getUser();
        user.setRole(organizerRole);
        userRepository.save(user);

        emailService.sendOrganizerRequestApproved(user.getEmail(), user.getFio());
    }

    @Transactional
    public void rejectOrganizerRequest(Long requestId, AdminModerationRequest req) throws AppException {
        OrganizerRequest request = organizerRequestRepository.findById(requestId)
                .orElseThrow(() -> new AppException("Заявка не найдена", HttpStatus.NOT_FOUND));

        RequestStatus rejected = requestStatusRepository.findByRequestStatusName("REJECTED")
                .orElseThrow(() -> new AppException("Статус не найден", HttpStatus.INTERNAL_SERVER_ERROR));

        request.setRequestStatus(rejected);
        request.setReviewComment(req.getComment());
        request.setReviewedAt(LocalDateTime.now());
        organizerRequestRepository.save(request);

        User user = request.getUser();
        emailService.sendOrganizerRequestRejected(user.getEmail(), user.getFio(), req.getComment());
    }

    // ── Модерация мероприятий ─────────────────────────────────

    @Transactional(readOnly = true)
    public List<AdminEventDto> getEventsByTab(String tab) {
        List<Event> events = switch (tab) {
            case "APPROVED" -> eventRepository.findApprovedEvents();
            case "REJECTED" -> eventRepository.findRejectedEvents();
            case "ARCHIVE" -> eventRepository.findArchiveEvents();
            default -> eventRepository.findPendingEvents(); // PENDING
        };
        return events.stream().map(this::toAdminEventDto).toList();
    }

    @Transactional
    public void approveEvent(Long eventId, AdminModerationRequest req) throws AppException {
        Event event = getEventOrThrow(eventId);

        event.setVerified(true);
        event.setVerificationComment(req.getComment());
        eventRepository.save(event);

        emailService.sendEventApproved(
                event.getOrganizer().getEmail(),
                event.getOrganizer().getFio(),
                event.getEventName(),
                req.getComment()
        );
    }

    @Transactional
    public void rejectEvent(Long eventId, AdminModerationRequest req) throws AppException {
        Event event = getEventOrThrow(eventId);

        event.setVerified(false);
        event.setVerificationComment(req.getComment());
        eventRepository.save(event);

        emailService.sendEventRejected(
                event.getOrganizer().getEmail(),
                event.getOrganizer().getFio(),
                event.getEventName(),
                req.getComment()
        );
    }

    @Transactional
    public AdminEventDto editEvent(Long eventId, AdminEditEventRequest req) throws AppException {
        Event event = getEventOrThrow(eventId);

        EventCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new AppException("Категория не найдена", HttpStatus.NOT_FOUND));
        EventFormat format = formatRepository.findById(req.getFormatId())
                .orElseThrow(() -> new AppException("Формат не найден", HttpStatus.NOT_FOUND));

        event.setEventName(req.getEventName());
        event.setEventDescription(req.getEventDescription());
        event.setEventDate(req.getEventDate());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setEventCategory(category);
        event.setEventFormat(format);
        event.setPrice(req.getPrice());
        event.setMaxParticipants(req.getMaxParticipants());
        event.setImageUrl(req.getImageUrl());
        event.setVerified(true); // публикуем сразу
        event.setVerificationComment("Опубликовано администратором после редактирования");
        Event saved = eventRepository.save(event);

        emailService.sendEventEdited(
                event.getOrganizer().getEmail(),
                event.getOrganizer().getFio(),
                event.getEventName()
        );
        return toAdminEventDto(saved);
    }

    @Transactional
    public void deleteEvent(Long eventId) throws AppException {
        Event event = getEventOrThrow(eventId);
        // Удаляем сначала участников
        participantRepository.deleteByEventId(eventId);
        eventRepository.delete(event);
    }

    // ── Справочники: Категории ────────────────────────────────

    @Transactional(readOnly = true)
    public List<EventCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new EventCategoryDto(c.getIdEventCategory(),
                        c.getEventCategoryName(), c.getColorCode()))
                .toList();
    }

    @Transactional
    public EventCategoryDto createCategory(DirectoryItemRequest req) {
        EventCategory c = new EventCategory();
        c.setEventCategoryName(req.getName());
        c.setEventCategoryDescription(req.getDescription());
        c.setColorCode(req.getColorCode());
        EventCategory saved = categoryRepository.save(c);
        return new EventCategoryDto(saved.getIdEventCategory(),
                saved.getEventCategoryName(), saved.getColorCode());
    }

    @Transactional
    public EventCategoryDto updateCategory(Long id, DirectoryItemRequest req) throws AppException {
        EventCategory c = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Категория не найдена", HttpStatus.NOT_FOUND));
        c.setEventCategoryName(req.getName());
        c.setEventCategoryDescription(req.getDescription());
        c.setColorCode(req.getColorCode());
        categoryRepository.save(c);
        return new EventCategoryDto(c.getIdEventCategory(),
                c.getEventCategoryName(), c.getColorCode());
    }

    @Transactional
    public void deleteCategory(Long id) throws AppException {
        if (!categoryRepository.existsById(id))
            throw new AppException("Категория не найдена", HttpStatus.NOT_FOUND);
        categoryRepository.deleteById(id);
    }

    // ── Справочники: Форматы ──────────────────────────────────

    @Transactional(readOnly = true)
    public List<EventFormat> getAllFormats() {
        return formatRepository.findAll();
    }

    @Transactional
    public EventFormat createFormat(DirectoryItemRequest req) {
        EventFormat f = new EventFormat();
        f.setEventFormatName(req.getName());
        f.setEventFormatDescription(req.getDescription());
        return formatRepository.save(f);
    }

    @Transactional
    public EventFormat updateFormat(Long id, DirectoryItemRequest req) throws AppException {
        EventFormat f = formatRepository.findById(id)
                .orElseThrow(() -> new AppException("Формат не найден", HttpStatus.NOT_FOUND));
        f.setEventFormatName(req.getName());
        f.setEventFormatDescription(req.getDescription());
        return formatRepository.save(f);
    }

    @Transactional
    public void deleteFormat(Long id) throws AppException {
        if (!formatRepository.existsById(id))
            throw new AppException("Формат не найден", HttpStatus.NOT_FOUND);
        formatRepository.deleteById(id);
    }

    // ── Справочники: Статусы мероприятий ─────────────────────

    @Transactional(readOnly = true)
    public List<EventStatus> getAllEventStatuses() {
        return eventStatusRepository.findAll();
    }

    @Transactional
    public EventStatus createEventStatus(DirectoryItemRequest req) {
        EventStatus s = new EventStatus();
        s.setEventStatusName(req.getName());
        s.setEventStatusDescription(req.getDescription());
        return eventStatusRepository.save(s);
    }

    @Transactional
    public EventStatus updateEventStatus(Long id, DirectoryItemRequest req) throws AppException {
        EventStatus s = eventStatusRepository.findById(id)
                .orElseThrow(() -> new AppException("Статус не найден", HttpStatus.NOT_FOUND));
        s.setEventStatusName(req.getName());
        s.setEventStatusDescription(req.getDescription());
        return eventStatusRepository.save(s);
    }

    @Transactional
    public void deleteEventStatus(Long id) throws AppException {
        if (!eventStatusRepository.existsById(id))
            throw new AppException("Статус не найден", HttpStatus.NOT_FOUND);
        eventStatusRepository.deleteById(id);
    }

    // ── Справочники: Статусы пользователей ───────────────────

    @Transactional(readOnly = true)
    public List<UserStatus> getAllUserStatuses() {
        return userStatusRepository.findAll();
    }

    @Transactional
    public UserStatus createUserStatus(DirectoryItemRequest req) {
        UserStatus s = new UserStatus();
        s.setUserStatusName(req.getName());
        return userStatusRepository.save(s);
    }

    @Transactional
    public void deleteUserStatus(Long id) throws AppException {
        if (!userStatusRepository.existsById(id))
            throw new AppException("Статус пользователя не найден", HttpStatus.NOT_FOUND);
        userStatusRepository.deleteById(id);
    }

    // ── Вспомогательные ──────────────────────────────────────

    private User getUserOrThrow(Long id) throws AppException {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    private Event getEventOrThrow(Long id) throws AppException {
        return eventRepository.findById(id)
                .orElseThrow(() -> new AppException("Мероприятие не найдено", HttpStatus.NOT_FOUND));
    }

    private AdminUserDto toAdminUserDto(User u) {
        return new AdminUserDto(u.getIdUser(), u.getFio(), u.getEmail(),
                u.getRole().getRoleName(), u.getUserStatus().getUserStatusName(),
                u.getHasDisability());
    }

    private AdminEventDto toAdminEventDto(Event e) {
        Long count = participantRepository.countActiveParticipants(e.getIdEvent());
        boolean isOnline = (e.getPlace() instanceof OnlinePlace);
        String placeName = e.getPlace() != null ? e.getPlace().getPlaceName() : null;
        String address = !isOnline && e.getPlace() instanceof PhysicalPlace
                ? ((PhysicalPlace) e.getPlace()).getAddress() : null;
        String meetingUrl = isOnline ? ((OnlinePlace) e.getPlace()).getMeetingUrl() : null;
        Boolean accessible = !isOnline && e.getPlace() instanceof PhysicalPlace
                ? ((PhysicalPlace) e.getPlace()).getDisabilityAccessible() : null;

        String tab = computeTab(e);

        return new AdminEventDto(
                e.getIdEvent(), e.getEventName(), e.getEventDescription(),
                e.getEventDate(), e.getStartTime(), e.getEndTime(),
                e.getImageUrl(), e.getPrice(), e.getMaxParticipants(), count,
                e.getEventCategory().getIdEventCategory(),
                e.getEventCategory().getEventCategoryName(),
                e.getEventCategory().getColorCode(),
                e.getEventFormat().getIdEventFormat(),
                e.getEventFormat().getEventFormatName(),
                placeName, isOnline, address, meetingUrl, accessible,
                e.getEventStatus() != null ? e.getEventStatus().getEventStatusName() : null,
                e.getVerified(), e.getVerificationComment(),
                e.getOrganizer().getIdUser(),
                e.getOrganizer().getFio(),
                e.getOrganizer().getEmail(),
                tab
        );
    }

    // Вычисляем вкладку для фронта
    private String computeTab(Event e) {
        String status = e.getEventStatus() != null
                ? e.getEventStatus().getEventStatusName() : "";
        if ("CANCELLED".equals(status) || "COMPLETED".equals(status)
                || e.getEventDate().isBefore(LocalDateTime.now())) {
            return "ARCHIVE";
        }
        if (e.getVerified()) return "APPROVED";
        if (e.getVerificationComment() != null && !e.getVerificationComment().isBlank()) {
            return "REJECTED";
        }
        return "PENDING";
    }

    @Transactional
    public void changeEventStatus(Long eventId, ChangeEventStatusRequest req) throws AppException {
        Event event = getEventOrThrow(eventId);

        EventStatus newStatus = eventStatusRepository
                .findByEventStatusName(req.getStatusName())
                .orElseThrow(() -> new AppException(
                        "Статус «" + req.getStatusName() + "» не найден", HttpStatus.NOT_FOUND));

        event.setEventStatus(newStatus);

        // Если возобновляем отменённое мероприятие — сбрасываем верификацию,
        // чтобы оно снова прошло модерацию
        if ("PLANNED".equals(req.getStatusName())) {
            event.setVerified(false);
            event.setVerificationComment(null);
        }
        eventRepository.save(event);
    }
}
