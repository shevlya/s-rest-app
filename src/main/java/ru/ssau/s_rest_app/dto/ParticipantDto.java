package ru.ssau.s_rest_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParticipantDto {
    private Long   userId;
    private String fio;
    private String email;
    private String participationStatus;  // REGISTERED / WAITLISTED
    private Boolean hasDisability;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registrationDate;
}
