package ru.ssau.s_rest_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatsDto {
    private long totalUsers;
    private long totalOrganizers;
    private long totalEvents;
    private long pendingEvents;    // ждут модерации
    private long pendingRequests;  // заявки организаторов
}
