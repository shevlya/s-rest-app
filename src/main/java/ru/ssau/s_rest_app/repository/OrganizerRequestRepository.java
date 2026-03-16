package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.s_rest_app.entity.OrganizerRequest;

import java.util.Optional;

public interface OrganizerRequestRepository extends JpaRepository<OrganizerRequest, Long> {
    // Последняя заявка пользователя (сортируем по дате подачи)
    @Query("SELECT r FROM OrganizerRequest r WHERE r.user.idUser = :userId " +
            "ORDER BY r.submittedAt DESC LIMIT 1")
    Optional<OrganizerRequest> findLatestByUserId(@Param("userId") Long userId);
}
