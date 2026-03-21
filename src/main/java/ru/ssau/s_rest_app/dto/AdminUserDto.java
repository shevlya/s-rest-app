package ru.ssau.s_rest_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminUserDto {
    private Long   id;
    private String fio;
    private String email;
    private String role;
    private String status;
    private Boolean hasDisability;
}
