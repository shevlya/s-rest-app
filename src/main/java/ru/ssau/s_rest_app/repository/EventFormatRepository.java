package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.s_rest_app.entity.EventFormat;

public interface EventFormatRepository extends JpaRepository<EventFormat, Long> {

    // Проверка уникальности названия
    boolean existsByEventFormatName(String eventFormatName);
}