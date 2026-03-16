package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCategoriesRequest {
    @NotNull
    private List<Long> categoryIds;
}
