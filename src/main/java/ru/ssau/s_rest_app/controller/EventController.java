package ru.ssau.s_rest_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.EventDetailDto;
import ru.ssau.s_rest_app.dto.EventPageResponse;
import ru.ssau.s_rest_app.exception.AppException;
import ru.ssau.s_rest_app.service.EventService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/upcoming")
    public EventPageResponse getUpcoming(@RequestParam(defaultValue = "6") int limit) {
        return eventService.getUpcoming(limit);
    }

    @GetMapping
    public EventPageResponse getFiltered(@RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) Long formatId,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "12") int size) {
        return eventService.getFiltered(categoryId, formatId, dateFrom, dateTo, page, size);
    }

    @GetMapping("/{id}")
    public EventDetailDto getById(@PathVariable Long id) throws AppException {
        return eventService.getById(id);
    }

    @PostMapping("/{id}/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void register(@PathVariable Long id, Authentication auth) throws AppException {
        eventService.register(id, auth.getName());
    }

    @DeleteMapping("/{id}/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(@PathVariable Long id, Authentication auth) throws AppException {
        eventService.unregister(id, auth.getName());
    }
}
