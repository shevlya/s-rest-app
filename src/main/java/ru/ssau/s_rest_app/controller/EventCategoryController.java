package ru.ssau.s_rest_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ssau.s_rest_app.dto.eventCategory.EventCategoryRequestDto;
import ru.ssau.s_rest_app.dto.eventCategory.EventCategoryResponseDto;
import ru.ssau.s_rest_app.exception.DuplicateEntityException;
import ru.ssau.s_rest_app.exception.EntityNotFoundException;
import ru.ssau.s_rest_app.service.EventCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryService service;

    @GetMapping
    public List<EventCategoryResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EventCategoryResponseDto getById(@PathVariable Long id) throws EntityNotFoundException {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventCategoryResponseDto create(@Valid @RequestBody EventCategoryRequestDto dto) throws DuplicateEntityException {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EventCategoryResponseDto update(@PathVariable Long id, @Valid @RequestBody EventCategoryRequestDto dto) throws DuplicateEntityException, EntityNotFoundException {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        service.delete(id);
    }
}
