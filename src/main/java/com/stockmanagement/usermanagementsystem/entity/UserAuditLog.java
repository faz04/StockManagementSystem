package com.stockmanagement.usermanagementsystem.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_audit_logs")
@EntityListeners(AuditingEntityListener.class)
public class UserAuditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String action;

	@Column(name = "performed_by", nullable = false, length = 50)
	private String performedBy;

	@Column(name = "target_user", nullable = false, length = 50)
	private String targetUser;

	@CreatedDate
	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Column(columnDefinition = "TEXT")
	private String details;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	// Default constructor
	public UserAuditLog() {}

	// Parameterized constructor
	public UserAuditLog(String action, String performedBy, String targetUser, String details) {
		this.action = action;
		this.performedBy = performedBy;
		this.targetUser = targetUser;
		this.details = details;
	}

	// Getters and Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getAction() { return action; }
	public void setAction(String action) { this.action = action; }

	public String getPerformedBy() { return performedBy; }
	public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

	public String getTargetUser() { return targetUser; }
	public void setTargetUser(String targetUser) { this.targetUser = targetUser; }

	public LocalDateTime getTimestamp() { return timestamp; }
	public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

	public String getDetails() { return details; }
	public void setDetails(String details) { this.details = details; }

	public String getIpAddress() { return ipAddress; }
	public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}