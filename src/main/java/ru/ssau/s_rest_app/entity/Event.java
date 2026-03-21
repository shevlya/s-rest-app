package ru.ssau.s_rest_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "id_admin", nullable = false)
    @JoinColumn(name = "id_admin")
    private User admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizer", nullable = false)
    private User organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_event_format", nullable = false)
    private EventFormat eventFormat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_event_status", nullable = false)
    private EventStatus eventStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_place")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_event_category", nullable = false)
    private EventCategory eventCategory;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String eventName;

    @Column(columnDefinition = "TEXT")
    private String eventDescription;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private Integer maxParticipants;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(columnDefinition = "TEXT")
    private String verificationComment;
}
