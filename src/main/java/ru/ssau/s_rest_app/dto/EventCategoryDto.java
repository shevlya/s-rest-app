package ru.ssau.s_rest_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventCategoryDto {
    private Long id;
    private String name;
    private String description;
    private String colorCode;
}
