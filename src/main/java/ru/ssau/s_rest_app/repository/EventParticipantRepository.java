package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.s_rest_app.entity.EventParticipant;
import ru.ssau.s_rest_app.entity.EventParticipantId;

import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    @Query("""
        SELECT ep FROM EventParticipant ep
        WHERE ep.idUser.idUser  = :userId
          AND ep.idEvent.idEvent = :eventId
    """)
    Optional<EventParticipant> findByUserIdAndEventId(@Param("userId")  Long userId, @Param("eventId") Long eventId);

    @Query("""
        SELECT COUNT(ep) FROM EventParticipant ep
        WHERE ep.idEvent.idEvent = :eventId
          AND ep.participationStatus.participationStatusName IN ('REGISTERED', 'WAITLISTED')
    """)
    Long countActiveParticipants(@Param("eventId") Long eventId);
}
