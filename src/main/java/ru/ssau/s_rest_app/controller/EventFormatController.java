package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.eventFormat.EventFormatRequestDto;
import ru.ssau.s_rest_app.dto.eventFormat.EventFormatResponseDto;
import ru.ssau.s_rest_app.exception.DuplicateEntityException;
import ru.ssau.s_rest_app.exception.EntityNotFoundException;
import ru.ssau.s_rest_app.service.EventFormatService;

import java.util.List;

@RestController
@RequestMapping("/api/formats")
@RequiredArgsConstructor
public class EventFormatController {

    private final EventFormatService service;

    @GetMapping
    public List<EventFormatResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EventFormatResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFormatResponseDto create(@Valid @RequestBody EventFormatRequestDto dto) throws DuplicateEntityException {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EventFormatResponseDto update(@PathVariable Long id, @Valid @RequestBody EventFormatRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        service.delete(id);
    }
}