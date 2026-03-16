package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.s_rest_app.entity.RequestStatus;

import java.util.Optional;

public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long> {
    Optional<RequestStatus> findByRequestStatusName(String requestStatusName);
}
