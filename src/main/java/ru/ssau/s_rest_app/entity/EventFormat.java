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
public class EventFormat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEventFormat;

    @Column(nullable = false, length = 50, unique = true)
    private String eventFormatName;

    @Column(columnDefinition = "TEXT")
    private String eventFormatDescription;
}
