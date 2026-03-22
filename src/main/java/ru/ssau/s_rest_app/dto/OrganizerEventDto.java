package ru.ssau.s_rest_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrganizerEventDto {
    private Long id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    private String imageUrl;
    private BigDecimal price;
    private Integer maxParticipants;
    private Long currentParticipants;

    private Long categoryId;
    private String categoryName;
    private String categoryColor;

    private Long formatId;
    private String formatName;

    private String placeName;
    private Boolean isOnline;
    private String status;
    private Boolean verified;
    private Boolean canEdit;
    private Boolean canCancel;
}
