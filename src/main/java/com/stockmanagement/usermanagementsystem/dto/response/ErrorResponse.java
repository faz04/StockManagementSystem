package com.stockmanagement.usermanagementsystem.dto.response;



import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
	private String errorCode;
	private String message;
	private List<String> details;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	// Default constructor
	public ErrorResponse() {
		this.timestamp = LocalDateTime.now();
	}

	// Parameterized constructors
	public ErrorResponse(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}

	public ErrorResponse(String errorCode, String message, List<String> details) {
		this.errorCode = errorCode;
		this.message = message;
		this.details = details;
		this.timestamp = LocalDateTime.now();
	}

	// Getters and Setters
	public String getErrorCode() { return errorCode; }
	public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }

	public List<String> getDetails() { return details; }
	public void setDetails(List<String> details) { this.details = details; }

	public LocalDateTime getTimestamp() { return timestamp; }
	public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}