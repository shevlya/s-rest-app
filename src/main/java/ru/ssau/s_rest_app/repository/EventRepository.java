package ru.ssau.s_rest_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.s_rest_app.entity.Event;
import ru.ssau.s_rest_app.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
                SELECT e FROM Event e
                WHERE e.verified = true
                  AND e.eventStatus.eventStatusName = 'PLANNED'
                  AND e.startTime > :now
                  AND e.eventCategory.idEventCategory = coalesce(:categoryId, e.eventCategory.idEventCategory)
                  AND e.eventFormat.idEventFormat = coalesce(:formatId, e.eventFormat.idEventFormat)
                  AND e.eventDate >= coalesce(:dateFrom, e.eventDate)
                  AND e.eventDate <= coalesce(:dateTo, e.eventDate)
                ORDER BY e.eventDate ASC, e.startTime ASC
            """)
    Page<Event> findPublicEvents(
            @Param("now") LocalDateTime now,
            @Param("categoryId") Long categoryId,
            @Param("formatId") Long formatId,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable);

    @Query("""
                SELECT COUNT(ep) FROM EventParticipant ep
                WHERE ep.idEvent.idEvent = :eventId
                  AND ep.participationStatus.participationStatusName IN ('REGISTERED', 'WAITLISTED')
            """)
    Long countParticipants(@Param("eventId") Long eventId);

    List<Event> findByOrganizerOrderByEventDateDesc(User organizer);

    //Для администратора
    @Query("""
                SELECT e FROM Event e
                WHERE e.verified = false
                  AND (e.verificationComment IS NULL OR e.verificationComment = '')
                  AND e.eventStatus.eventStatusName NOT IN ('CANCELLED', 'COMPLETED')
                  AND e.eventDate > CURRENT_TIMESTAMP
                ORDER BY e.eventDate DESC
            """)
    List<Event> findPendingEvents();

    @Query("""
                SELECT e FROM Event e
                WHERE e.verified = true
                  AND e.eventStatus.eventStatusName NOT IN ('CANCELLED', 'COMPLETED')
                  AND e.eventDate > CURRENT_TIMESTAMP
                ORDER BY e.eventDate DESC
            """)
    List<Event> findApprovedEvents();

    @Query("""
                SELECT e FROM Event e
                WHERE e.verified = false
                  AND e.verificationComment IS NOT NULL
                  AND e.verificationComment <> ''
                ORDER BY e.eventDate DESC
            """)
    List<Event> findRejectedEvents();

    @Query("""
                SELECT e FROM Event e
                WHERE e.eventDate < CURRENT_TIMESTAMP
                   OR e.eventStatus.eventStatusName IN ('CANCELLED', 'COMPLETED')
                ORDER BY e.eventDate DESC
            """)
    List<Event> findArchiveEvents();

    @Query("""
                SELECT COUNT(e) FROM Event e
                WHERE e.verified = false
                  AND (e.verificationComment IS NULL OR e.verificationComment = '')
                  AND e.eventStatus.eventStatusName NOT IN ('CANCELLED', 'COMPLETED')
            """)
    long countPendingModeration();
}