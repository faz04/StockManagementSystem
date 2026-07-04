package com.stockmanagement.usermanagementsystem.util;



import com.stockmanagement.usermanagementsystem.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

	public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
		return ResponseEntity.ok(ApiResponse.success(data));
	}

	public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
		return ResponseEntity.ok(ApiResponse.success(message, data));
	}

	public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(message, data));
	}

	public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
		return ResponseEntity.badRequest().body(ApiResponse.error(message));
	}

	public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(ApiResponse.error(message));
	}

	public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(message));
	}
}
