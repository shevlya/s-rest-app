package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.CreateEventRequest;
import ru.ssau.s_rest_app.dto.OrganizerEventDto;
import ru.ssau.s_rest_app.dto.ParticipantDto;
import ru.ssau.s_rest_app.dto.UpdateEventRequest;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.service.OrganizerEventService;

import java.util.List;

@RestController
@RequestMapping("/api/organizer/events")
@RequiredArgsConstructor
public class OrganizerEventController {
    private final OrganizerEventService organizerEventService;

    /**
     * GET /api/organizer/events — мои мероприятия
     */
    @GetMapping
    public ResponseEntity<List<OrganizerEventDto>> getMyEvents() throws AppException {
        return ResponseEntity.ok(organizerEventService.getMyEvents());
    }

    /**
     * POST /api/organizer/events — создать мероприятие
     */
    @PostMapping
    public ResponseEntity<OrganizerEventDto> createEvent(
            @Valid @RequestBody CreateEventRequest request) throws AppException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizerEventService.createEvent(request));
    }

    /**
     * PUT /api/organizer/events/{id} — редактировать
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrganizerEventDto> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request) throws AppException {
        return ResponseEntity.ok(organizerEventService.updateEvent(id, request));
    }

    /**
     * DELETE /api/organizer/events/{id} — отменить
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEvent(@PathVariable Long id) throws AppException {
        organizerEventService.cancelEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/organizer/events/{id}/participants — список участников
     */
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDto>> getParticipants(
            @PathVariable Long id) throws AppException {
        return ResponseEntity.ok(organizerEventService.getParticipants(id));
    }
}
