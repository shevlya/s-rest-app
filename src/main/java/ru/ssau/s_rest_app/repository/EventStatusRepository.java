package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.s_rest_app.entity.EventStatus;

import java.util.Optional;

public interface EventStatusRepository extends JpaRepository<EventStatus,Long> {
    Optional<EventStatus> findByEventStatusName(String name);
}
