package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.s_rest_app.entity.EventCategory;

import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    // Проверка уникальности названия при создании
    boolean existsByEventCategoryName(String eventCategoryName);

    // Поиск по имени (пригодится позже для фильтрации)
    Optional<EventCategory> findByEventCategoryName(String eventCategoryName);
}