package com.stockmanagement.usermanagementsystem.repository;


import com.stockmanagement.usermanagementsystem.entity.UserAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAuditLogRepository extends JpaRepository<UserAuditLog, Long> {
	List<UserAuditLog> findByTargetUserOrderByTimestampDesc(String targetUser);

	List<UserAuditLog> findByPerformedByOrderByTimestampDesc(String performedBy);

	List<UserAuditLog> findByActionOrderByTimestampDesc(String action);

	@Query("SELECT ual FROM UserAuditLog ual WHERE ual.timestamp BETWEEN :startDate AND :endDate ORDER BY ual.timestamp DESC")
	List<UserAuditLog> findByTimestampBetween(@Param("startDate") LocalDateTime startDate,
											  @Param("endDate") LocalDateTime endDate);

	Page<UserAuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

	@Query("SELECT COUNT(ual) FROM UserAuditLog ual WHERE ual.action = :action AND ual.timestamp >= :since")
	long countActionsSince(@Param("action") String action, @Param("since") LocalDateTime since);
}