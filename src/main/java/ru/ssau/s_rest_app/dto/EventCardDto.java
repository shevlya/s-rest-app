package ru.ssau.s_rest_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventCardDto {
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
    private Long       currentParticipants; // сколько записалось

    private String categoryName;
    private String categoryColor;
    private String formatName;
    private String placeName;     // null для онлайн без указания площадки
    private Boolean isOnline;
}
