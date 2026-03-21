package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    @NotBlank(message = "Название мероприятия обязательно")
    private String eventName;

    private String eventDescription;

    @NotNull(message = "Дата мероприятия обязательна")
    @Future(message = "Дата должна быть в будущем")
    private LocalDateTime eventDate;

    @NotNull(message = "Время начала обязательно")
    private LocalDateTime startTime;

    @NotNull(message = "Время окончания обязательно")
    private LocalDateTime endTime;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    private BigDecimal price;

    private Integer maxParticipants;
    private String imageUrl;
    private String eventDescription2; // повторяет description для ясности
}
