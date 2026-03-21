package ru.ssau.s_rest_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrganizerRequestAdminDto {
    private Long   id;
    private Long   userId;
    private String userFio;
    private String userEmail;
    private String requestText;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;
}
