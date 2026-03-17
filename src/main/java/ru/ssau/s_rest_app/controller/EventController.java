package ru.ssau.s_rest_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

    /**
     * GET /api/events?limit=6
     * Публичный — ближайшие N мероприятий (для главной страницы)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<EventPageResponse> getUpcoming(@RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(eventService.getUpcoming(limit));
    }

    /**
     * GET /api/events?categoryId=&formatId=&dateFrom=&dateTo=&page=0&size=12
     * Публичный — список с фильтрами (для страницы мероприятий)
     */
    @GetMapping
    public ResponseEntity<EventPageResponse> getFiltered(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long formatId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(eventService.getFiltered(categoryId, formatId, dateFrom, dateTo, page, size));
    }

    /**
     * GET /api/events/{id}
     * Публичный — детальная страница мероприятия
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDetailDto> getById(@PathVariable Long id) throws AppException {
        return ResponseEntity.ok(eventService.getById(id));
    }

    /**
     * POST /api/events/{id}/register
     * Авторизованный — запись на мероприятие
     */
    @PostMapping("/{id}/register")
    public ResponseEntity<Void> register(@PathVariable Long id, Authentication auth) throws AppException {
        eventService.register(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/events/{id}/register
     * Авторизованный — отмена записи
     */
    @DeleteMapping("/{id}/register")
    public ResponseEntity<Void> unregister(@PathVariable Long id, Authentication auth) throws AppException {
        eventService.unregister(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
