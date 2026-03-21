package ru.ssau.s_rest_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.s_rest_app.dto.CreateEventRequest;
import ru.ssau.s_rest_app.dto.OrganizerEventDto;
import ru.ssau.s_rest_app.dto.ParticipantDto;
import ru.ssau.s_rest_app.dto.UpdateEventRequest;
import ru.ssau.s_rest_app.entity.*;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerEventService {
    private final UserRepository             userRepository;
    private final EventRepository            eventRepository;
    private final EventCategoryRepository    categoryRepository;
    private final EventFormatRepository      formatRepository;
    private final EventStatusRepository      eventStatusRepository;
    private final PlaceRepository            placeRepository;
    private final EventParticipantRepository participantRepository;

    // ── Получить текущего организатора ───────────────────────

    private User getCurrentOrganizer() throws AppException {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    // ── Список мероприятий организатора ──────────────────────

    @Transactional(readOnly = true)
    public List<OrganizerEventDto> getMyEvents() throws AppException {
        User organizer = getCurrentOrganizer();
        return eventRepository.findByOrganizerOrderByEventDateDesc(organizer)
                .stream()
                .map(this::toOrganizerDto)
                .toList();
    }

    // ── Создать мероприятие ───────────────────────────────────

    @Transactional
    public OrganizerEventDto createEvent(CreateEventRequest req) throws AppException {
        User organizer = getCurrentOrganizer();

        EventCategory category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new AppException(
                        "Категория не найдена", HttpStatus.NOT_FOUND));

        EventFormat format = formatRepository.findById(req.getFormatId())
                .orElseThrow(() -> new AppException(
                        "Формат не найден", HttpStatus.NOT_FOUND));

        // ⚠ В БД нет статуса ACTIVE — используем PLANNED
        EventStatus status = eventStatusRepository
                .findByEventStatusName("PLANNED")
                .orElseThrow(() -> new AppException(
                        "Статус PLANNED не найден в БД", HttpStatus.INTERNAL_SERVER_ERROR));

        // Создаём площадку
        Place place = buildPlace(req);

        // Собираем дату+время для eventDate, startTime, endTime
        // Фронт присылает дату и время отдельно:
        // eventDate = "2025-06-15" (строка date)
        // startTime = "14:00"      (строка time)
        // endTime   = "16:00"
        // Уже собраны в LocalDateTime через @JsonFormat на фронте
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setAdmin(null);
        event.setEventName(req.getEventName());
        event.setEventDescription(req.getEventDescription());
        event.setEventDate(req.getEventDate());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setEventCategory(category);
        event.setEventFormat(format);
        event.setEventStatus(status);
        event.setPrice(req.getPrice());
        event.setMaxParticipants(req.getMaxParticipants());
        event.setImageUrl(req.getImageUrl());
        event.setPlace(place);
        event.setVerified(false);

        Event saved = eventRepository.save(event);
        return toOrganizerDto(saved);
    }

    // ── Редактировать мероприятие ─────────────────────────────

    @Transactional
    public OrganizerEventDto updateEvent(Long eventId, UpdateEventRequest req)
            throws AppException {
        Event event = getOwnEvent(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new AppException(
                    "Нельзя редактировать прошедшее мероприятие", HttpStatus.BAD_REQUEST);
        }

        event.setEventName(req.getEventName());
        event.setEventDescription(req.getEventDescription());
        event.setEventDate(req.getEventDate());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setPrice(req.getPrice());
        event.setMaxParticipants(req.getMaxParticipants());
        event.setImageUrl(req.getImageUrl());
        event.setVerified(false); // снова требует проверки

        return toOrganizerDto(eventRepository.save(event));
    }

    // ── Отменить мероприятие ──────────────────────────────────

    @Transactional
    public void cancelEvent(Long eventId) throws AppException {
        Event event = getOwnEvent(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new AppException(
                    "Нельзя отменить прошедшее мероприятие", HttpStatus.BAD_REQUEST);
        }

        EventStatus cancelled = eventStatusRepository
                .findByEventStatusName("CANCELLED")
                .orElseThrow(() -> new AppException(
                        "Статус CANCELLED не найден в БД", HttpStatus.INTERNAL_SERVER_ERROR));

        event.setEventStatus(cancelled);
        eventRepository.save(event);
    }

    // ── Участники мероприятия ─────────────────────────────────

    @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipants(Long eventId) throws AppException {
        getOwnEvent(eventId);
        return participantRepository.findByEventId(eventId)
                .stream()
                .map(ep -> new ParticipantDto(
                        ep.getIdUser().getIdUser(),
                        ep.getIdUser().getFio(),
                        ep.getIdUser().getEmail(),
                        ep.getParticipationStatus().getParticipationStatusName(),
                        ep.getIdUser().getHasDisability(),
                        ep.getRegistrationDate()
                ))
                .toList();
    }

    // ── Вспомогательные ──────────────────────────────────────

    private Event getOwnEvent(Long eventId) throws AppException {
        User organizer = getCurrentOrganizer();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new AppException(
                        "Мероприятие не найдено", HttpStatus.NOT_FOUND));
        if (!event.getOrganizer().getIdUser().equals(organizer.getIdUser())) {
            throw new AppException(
                    "Нет доступа к этому мероприятию", HttpStatus.FORBIDDEN);
        }
        return event;
    }

    private Place buildPlace(CreateEventRequest req) throws AppException {
        if ("ONLINE".equals(req.getPlaceType())) {
            OnlinePlace p = new OnlinePlace();
            p.setPlaceName(req.getPlaceName() != null && !req.getPlaceName().isBlank()
                    ? req.getPlaceName() : "Онлайн");
            p.setMeetingUrl(req.getMeetingUrl() != null ? req.getMeetingUrl() : "");
            p.setSpecialNotes(req.getSpecialNotes());
            p.setRecording(false);
            return placeRepository.save(p);
        } else {
            PhysicalPlace p = new PhysicalPlace();
            // address уже содержит полный адрес, city убрали
            p.setPlaceName(req.getPlaceName() != null && !req.getPlaceName().isBlank()
                    ? req.getPlaceName() : "Офлайн");
            p.setAddress(req.getAddress() != null ? req.getAddress() : "");
            p.setDisabilityAccessible(
                    req.getDisabilityAccessible() != null && req.getDisabilityAccessible());
            return placeRepository.save(p);
        }
    }

    private OrganizerEventDto toOrganizerDto(Event e) {
        Long count = participantRepository.countActiveParticipants(e.getIdEvent());
        boolean isOnline = (e.getPlace() instanceof OnlinePlace);
        String placeName = e.getPlace() != null ? e.getPlace().getPlaceName() : null;
        boolean future = e.getEventDate().isAfter(LocalDateTime.now());
        String statusName = e.getEventStatus() != null
                ? e.getEventStatus().getEventStatusName() : "";

        return new OrganizerEventDto(
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
                isOnline,
                statusName,
                e.getVerified(),
                future && !"CANCELLED".equals(statusName),
                future && !"CANCELLED".equals(statusName)
        );
    }
}
