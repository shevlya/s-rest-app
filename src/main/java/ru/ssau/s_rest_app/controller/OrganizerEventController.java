package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<OrganizerEventDto> getMyEvents() throws AppException {
        return organizerEventService.getMyEvents();
    }

    @GetMapping("/{id}")
    public OrganizerEventDto getEvent(@PathVariable Long id) throws AppException {
        return organizerEventService.getEventById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizerEventDto createEvent(@Valid @RequestBody CreateEventRequest request) throws AppException {
        return organizerEventService.createEvent(request);
    }

    @PutMapping("/{id}")
    public OrganizerEventDto updateEvent(@PathVariable Long id, @Valid @RequestBody UpdateEventRequest request) throws AppException {
        return organizerEventService.updateEvent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelEvent(@PathVariable Long id) throws AppException {
        organizerEventService.cancelEvent(id);
    }

    @GetMapping("/{id}/participants")
    public List<ParticipantDto> getParticipants(@PathVariable Long id) throws AppException {
        return organizerEventService.getParticipants(id);
    }

    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restoreEvent(@PathVariable Long id) throws AppException {
        organizerEventService.restoreEvent(id);
    }
}
