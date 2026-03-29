package ru.ssau.s_rest_app.dto.eventFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventFormatRequestDto {

    @NotBlank(message = "Название формата обязательно")
    @Size(max = 50, message = "Название не должно превышать 50 символов")
    private String eventFormatName;

    private String eventFormatDescription;
}
