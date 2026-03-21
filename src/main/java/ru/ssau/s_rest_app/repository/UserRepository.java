package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.s_rest_app.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
                SELECT u FROM User u
                WHERE (:search IS NULL OR :search = ''
                       OR LOWER(u.fio)   LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
                  AND (:role   IS NULL OR :role   = '' OR u.role.roleName = :role)
                  AND (:status IS NULL OR :status = '' OR u.userStatus.userStatusName = :status)
                ORDER BY u.fio ASC
            """)
    List<User> findForAdmin(
            @Param("search") String search,
            @Param("role") String role,
            @Param("status") String status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
}
