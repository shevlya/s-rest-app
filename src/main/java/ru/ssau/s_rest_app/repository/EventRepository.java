package ru.ssau.s_rest_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.s_rest_app.entity.Event;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {
    // Публичные: только верифицированные и активные мероприятия
    @Query("""
        SELECT e FROM Event e
        WHERE e.verified = true
          AND e.eventStatus.eventStatusName = 'ACTIVE'
          AND e.eventDate >= :now
          AND (:categoryId IS NULL OR e.eventCategory.idEventCategory = :categoryId)
          AND (:formatId   IS NULL OR e.eventFormat.idEventFormat = :formatId)
          AND (:dateFrom   IS NULL OR e.eventDate >= :dateFrom)
          AND (:dateTo     IS NULL OR e.eventDate <= :dateTo)
        ORDER BY e.eventDate ASC
    """)
    Page<Event> findPublicEvents(@Param("now") LocalDateTime now, @Param("categoryId") Long categoryId, @Param("formatId")   Long formatId, @Param("dateFrom")   LocalDateTime dateFrom, @Param("dateTo")     LocalDateTime dateTo, Pageable pageable);

    // Количество подтверждённых участников мероприятия
    @Query("""
        SELECT COUNT(ep) FROM EventParticipant ep
        WHERE ep.idEvent.idEvent = :eventId
          AND ep.participationStatus.participationStatusName IN ('REGISTERED', 'WAITLISTED')
    """)
    Long countParticipants(@Param("eventId") Long eventId);
}
