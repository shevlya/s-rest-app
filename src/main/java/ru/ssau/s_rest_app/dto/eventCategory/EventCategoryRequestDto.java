package ru.ssau.s_rest_app.dto.eventCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventCategoryRequestDto {

    @NotBlank(message = "Название категории обязательно")
    @Size(max = 100, message = "Название не должно превышать 100 символов")
    private String eventCategoryName;

    private String eventCategoryDescription;

    // Цвет в hex-формате, например #A3C4F5 (необязательный)
    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6})$",
            message = "Цвет должен быть в формате HEX: #RRGGBB"
    )
    private String colorCode;
}
