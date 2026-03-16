package ru.ssau.s_rest_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.s_rest_app.entity.UserInterest;
import ru.ssau.s_rest_app.entity.UserInterestId;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {
    @Query("SELECT ui FROM UserInterest ui WHERE ui.idUser.idUser = :userId")
    List<UserInterest> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserInterest ui WHERE ui.idUser.idUser = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
