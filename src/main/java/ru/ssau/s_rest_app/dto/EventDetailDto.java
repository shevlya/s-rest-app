package ru.ssau.s_rest_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventDetailDto {
    private Long   id;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private String     imageUrl;
    private BigDecimal price;
    private Integer    maxParticipants;
    private Long       currentParticipants;
    private Boolean    registrationOpen; // можно ли ещё записаться

    // Категория
    private Long   categoryId;
    private String categoryName;
    private String categoryColor;

    // Формат
    private Long   formatId;
    private String formatName;

    // Площадка
    private String  placeName;
    private Boolean isOnline;
    private String  meetingUrl;   // если онлайн
    private String  address;      // если офлайн
    private Boolean disabilityAccessible;

    // Организатор
    private Long   organizerId;
    private String organizerName;

    // Текущий пользователь уже записан?
    private Boolean isRegistered;
    private String  participationStatus; // REGISTERED / WAITLISTED / CANCELLED / null
}
