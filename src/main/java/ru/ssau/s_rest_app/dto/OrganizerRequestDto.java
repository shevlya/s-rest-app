package ru.ssau.s_rest_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganizerRequestDto {
    private Long id;
    private String requestText;
    private String status;      // PENDING / APPROVED / REJECTED
    private String reviewComment;
}
