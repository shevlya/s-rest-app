package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminEditEventRequest {
    @NotBlank
    private String eventName;
    private String eventDescription;

    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Long categoryId;
    @NotNull
    private Long formatId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    private Integer maxParticipants;
    private String imageUrl;
}
