package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DirectoryItemRequest {
    @NotBlank
    private String name;
    private String description;
    private String colorCode; // только для категорий
}
