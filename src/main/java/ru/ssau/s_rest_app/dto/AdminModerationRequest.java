package ru.ssau.s_rest_app.dto;

import lombok.Data;

@Data
public class AdminModerationRequest {
    private String comment; // опциональный комментарий
}
