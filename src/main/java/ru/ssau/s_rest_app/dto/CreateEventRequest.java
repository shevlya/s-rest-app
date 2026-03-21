package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Название мероприятия обязательно")
    private String eventName;

    private String eventDescription;

    @NotNull(message = "Дата мероприятия обязательна")
    private LocalDateTime eventDate;

    @NotNull(message = "Время начала обязательно")
    private LocalDateTime startTime;

    @NotNull(message = "Время окончания обязательно")
    private LocalDateTime endTime;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    @NotNull(message = "Формат обязателен")
    private Long formatId;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    private BigDecimal price = BigDecimal.ZERO;

    private Integer maxParticipants;
    private String imageUrl;

    @NotBlank(message = "Тип площадки обязателен")
    private String placeType; // "ONLINE" | "PHYSICAL" | "HYBRID"

    // Онлайн
    private String meetingUrl;
    private String specialNotes;

    // Офлайн (city убрали — пишут сразу в address)
    private String placeName;
    private String address;        // "ул. Примерная, 1, Самара"
    private Boolean disabilityAccessible = false;
}
