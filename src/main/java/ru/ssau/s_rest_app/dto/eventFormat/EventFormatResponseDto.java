package ru.ssau.s_rest_app.dto.eventFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventFormatResponseDto {
    private Long   idEventFormat;
    private String eventFormatName;
    private String eventFormatDescription;
}
