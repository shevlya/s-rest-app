package ru.ssau.s_rest_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEventStatus;

    @Column(nullable = false, length = 50, unique = true)
    private String eventStatusName;

    private String eventStatusDescription;
}
