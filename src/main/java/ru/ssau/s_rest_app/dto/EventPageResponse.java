package ru.ssau.s_rest_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventPageResponse {
    private List<EventCardDto> events;
    private int  page;
    private int  size;
    private long totalElements;
    private int  totalPages;
}
