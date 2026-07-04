package com.stockmanagement.usermanagementsystem.service;


import com.stockmanagement.usermanagementsystem.entity.UserAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {
	void logActivity(String action, String performedBy, String targetUser, String details);

	void logActivity(String action, String performedBy, String targetUser, String details, String ipAddress);

	List<UserAuditLog> getAuditLogsByTargetUser(String targetUser);

	List<UserAuditLog> getAuditLogsByPerformedBy(String performedBy);

	List<UserAuditLog> getAuditLogsByAction(String action);

	List<UserAuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

	Page<UserAuditLog> getAllAuditLogs(Pageable pageable);

	long getActionCountSince(String action, LocalDateTime since);
}