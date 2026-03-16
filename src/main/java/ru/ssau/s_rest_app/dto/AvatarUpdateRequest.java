package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AvatarUpdateRequest {
    @NotBlank(message = "Путь к аватару обязателен")
    private String avatarUrl;
}
