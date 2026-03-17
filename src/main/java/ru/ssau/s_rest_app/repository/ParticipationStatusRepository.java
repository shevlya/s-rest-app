package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.s_rest_app.entity.ParticipationStatus;

import java.util.Optional;

public interface ParticipationStatusRepository extends JpaRepository<ParticipationStatus, Long> {
    Optional<ParticipationStatus> findByParticipationStatusName(String name);
}
