package ru.ssau.s_rest_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.s_rest_app.dto.EventCardDto;
import ru.ssau.s_rest_app.dto.EventDetailDto;
import ru.ssau.s_rest_app.dto.EventPageResponse;
import ru.ssau.s_rest_app.entity.*;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.repository.EventParticipantRepository;
import ru.ssau.s_rest_app.repository.EventRepository;
import ru.ssau.s_rest_app.repository.ParticipationStatusRepository;
import ru.ssau.s_rest_app.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ParticipationStatusRepository participationStatusRepository;

    // ── Получить ближайшие мероприятия (для главной) ──────────

    @Transactional(readOnly = true)
    public EventPageResponse getUpcoming(int limit) {
        Page<Event> page = eventRepository.findPublicEvents(LocalDateTime.now(),
                null, null, null, null,
                PageRequest.of(0, limit, Sort.by("eventDate").ascending())
        );
        return mapToPageResponse(page, 0, limit);
    }

    // ── Получить список с фильтрами (для страницы мероприятий) ─
    @Transactional(readOnly = true)
    public EventPageResponse getFiltered(
            Long categoryId, Long formatId,
            LocalDateTime dateFrom, LocalDateTime dateTo,
            int page, int size) {

        Page<Event> result = eventRepository.findPublicEvents(
                LocalDateTime.now(),
                categoryId, formatId, dateFrom, dateTo,
                PageRequest.of(page, size, Sort.by("eventDate").ascending())
        );
        return mapToPageResponse(result, page, size);
    }

    // ── Детальная страница мероприятия ────────────────────────

    @Transactional(readOnly = true)
    public EventDetailDto getById(Long eventId) throws AppException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AppException(
                        "Мероприятие не найдено", HttpStatus.NOT_FOUND));

        Long count = participantRepository.countActiveParticipants(eventId);
        boolean registrationOpen = event.getMaxParticipants() == null
                || count < event.getMaxParticipants();

        // Статус записи текущего пользователя (если авторизован)
        Boolean   isRegistered        = null;
        String    participationStatus = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            Optional<User> user = userRepository.findByEmail(auth.getName());
            if (user.isPresent()) {
                Optional<EventParticipant> ep = participantRepository
                        .findByUserIdAndEventId(user.get().getIdUser(), eventId);
                isRegistered        = ep.isPresent();
                participationStatus = ep.map(p ->
                        p.getParticipationStatus().getParticipationStatusName()
                ).orElse(null);
            }
        }

        // Площадка
        Place place = event.getPlace();
        boolean isOnline = (place instanceof OnlinePlace);
        String  placeName   = place != null ? place.getPlaceName()   : null;
        String  meetingUrl  = isOnline ? ((OnlinePlace) place).getMeetingUrl() : null;
        String  address     = !isOnline && place instanceof PhysicalPlace
                ? ((PhysicalPlace) place).getAddress() : null;
        Boolean accessible  = !isOnline && place instanceof PhysicalPlace
                ? ((PhysicalPlace) place).getDisabilityAccessible() : null;

        return new EventDetailDto(
                event.getIdEvent(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getImageUrl(),
                event.getPrice(),
                event.getMaxParticipants(),
                count,
                registrationOpen,
                event.getEventCategory().getIdEventCategory(),
                event.getEventCategory().getEventCategoryName(),
                event.getEventCategory().getColorCode(),
                event.getEventFormat().getIdEventFormat(),
                event.getEventFormat().getEventFormatName(),
                placeName, isOnline, meetingUrl, address, accessible,
                event.getOrganizer().getIdUser(),
                event.getOrganizer().getFio(),
                isRegistered,
                participationStatus
        );
    }

    // ── Записаться на мероприятие ─────────────────────────────

    @Transactional
    public void register(Long eventId, String userEmail) throws AppException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AppException(
                        "Мероприятие не найдено", HttpStatus.NOT_FOUND));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(
                        "Пользователь не найден", HttpStatus.NOT_FOUND));

        // Проверяем что ещё не записан
        if (participantRepository.findByUserIdAndEventId(
                user.getIdUser(), eventId).isPresent()) {
            throw new AppException("Вы уже записаны на это мероприятие",
                    HttpStatus.CONFLICT);
        }

        Long count = participantRepository.countActiveParticipants(eventId);

        // Определяем статус записи
        String statusName = (event.getMaxParticipants() != null
                && count >= event.getMaxParticipants())
                ? "WAITLISTED" : "REGISTERED";

        ParticipationStatus status = participationStatusRepository
                .findByParticipationStatusName(statusName)
                .orElseThrow(() -> new AppException(
                        "Статус участия не найден", HttpStatus.INTERNAL_SERVER_ERROR));

        EventParticipant ep = new EventParticipant();
        ep.setIdUser(user);
        ep.setIdEvent(event);
        ep.setParticipationStatus(status);
        ep.setRegistrationDate(LocalDateTime.now());
        participantRepository.save(ep);
    }

    // ── Отменить запись ───────────────────────────────────────

    @Transactional
    public void unregister(Long eventId, String userEmail) throws AppException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(
                        "Пользователь не найден", HttpStatus.NOT_FOUND));

        EventParticipant ep = participantRepository
                .findByUserIdAndEventId(user.getIdUser(), eventId)
                .orElseThrow(() -> new AppException(
                        "Запись не найдена", HttpStatus.NOT_FOUND));

        participantRepository.delete(ep);
    }

    // ── Маппинг ───────────────────────────────────────────────

    private EventPageResponse mapToPageResponse(Page<Event> page, int pageNum, int size) {
        var cards = page.getContent().stream()
                .map(this::toCard)
                .toList();
        return new EventPageResponse(
                cards, pageNum, size,
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private EventCardDto toCard(Event e) {
        Long count = participantRepository.countActiveParticipants(e.getIdEvent());
        boolean isOnline = (e.getPlace() instanceof OnlinePlace);
        String placeName = e.getPlace() != null ? e.getPlace().getPlaceName() : null;

        return new EventCardDto(
                e.getIdEvent(),
                e.getEventName(),
                e.getEventDescription(),
                e.getEventDate(),
                e.getStartTime(),
                e.getEndTime(),
                e.getImageUrl(),
                e.getPrice(),
                e.getMaxParticipants(),
                count,
                e.getEventCategory().getEventCategoryName(),
                e.getEventCategory().getColorCode(),
                e.getEventFormat().getEventFormatName(),
                placeName,
                isOnline
        );
    }
}
