package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Фамилия и имя обязательны для заполнения")
    private String fio;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Минимум 6 символов")
    private String password;

    private boolean hasDisability = false;

    //для создания заявки на организатора
    private String organizerRequestText;
}
