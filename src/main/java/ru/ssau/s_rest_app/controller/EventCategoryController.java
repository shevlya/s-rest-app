package ru.ssau.s_rest_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.s_rest_app.dto.EventCategoryDto;
import ru.ssau.s_rest_app.repository.EventCategoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class EventCategoryController {

    private final EventCategoryRepository categoryRepository;

    @GetMapping
    public List<EventCategoryDto> getAll() {
        return categoryRepository.findAll().stream()
                .map(c -> new EventCategoryDto(
                        c.getIdEventCategory(),
                        c.getEventCategoryName(),
                        c.getEventCategoryDescription(),
                        c.getColorCode()))
                .toList();
    }
}
