package ru.ssau.s_rest_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String fio;
    private String email;
    private String role;
    private String avatarUrl; //null если не выбран аватар

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private List<EventCategoryDto> categories;
    private boolean hasDisability;
    private OrganizerRequestDto organizerRequest; //null если нет заявки
}
