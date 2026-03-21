package ru.ssau.s_rest_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotBlank
    private String roleName; // USER | ORGANIZER | ADMIN
}
