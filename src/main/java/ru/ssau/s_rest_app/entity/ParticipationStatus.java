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
public class ParticipationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParticipationStatus;

    @Column(nullable = false, unique = true)
    private String participationStatusName;

    @Column(columnDefinition = "TEXT")
    private String participationStatusDescription;
}
