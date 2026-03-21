package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotBlank
    private String statusName; // ACTIVE | BLOCKED | PENDING
}
