package com.stockmanagement.usermanagementsystem.repository;

import com.stockmanagement.usermanagementsystem.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
	Optional<UserSession> findByTokenAndActiveTrue(String token);

	@Modifying
	@Query("UPDATE UserSession s SET s.active = false WHERE s.token = :token")
	void deactivateSession(@Param("token") String token);

	@Modifying
	@Query("UPDATE UserSession s SET s.active = false WHERE s.expiresAt < :now")
	void deactivateExpiredSessions(@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE UserSession s SET s.active = false WHERE s.userId = :userId AND s.active = true")
	void deactivateAllUserSessions(@Param("userId") Long userId);
}