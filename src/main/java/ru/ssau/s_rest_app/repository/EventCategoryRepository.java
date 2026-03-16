package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.s_rest_app.entity.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
}
