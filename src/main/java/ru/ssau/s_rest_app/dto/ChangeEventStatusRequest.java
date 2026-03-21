package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeEventStatusRequest {
    @NotBlank
    private String statusName; // PLANNED, ONGOING, COMPLETED, CANCELLED
}
