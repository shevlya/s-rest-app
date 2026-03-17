package ru.ssau.s_rest_app.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.s_rest_app.repository.EventFormatRepository;

import java.util.List;

@RestController
@RequestMapping("/api/formats")
@RequiredArgsConstructor
public class EventFormatController {

    private final EventFormatRepository formatRepository;

    @GetMapping
    public List<FormatDto> getAll() {
        return formatRepository.findAll().stream()
                .map(f -> new FormatDto(f.getIdEventFormat(), f.getEventFormatName()))
                .toList();
    }

    @Data
    @AllArgsConstructor
    static class FormatDto {
        private Long id;
        private String name;
    }
}
