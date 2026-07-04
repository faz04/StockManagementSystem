package com.stockmanagement.usermanagementsystem.util;

import com.stockmanagement.usermanagementsystem.exception.WeakPasswordException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator {

	private static final int MIN_LENGTH = 8;
	private static final int MAX_LENGTH = 128;

	private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
	private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
	private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
	private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

	public void validatePassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new WeakPasswordException("Password cannot be empty");
		}

		if (password.length() < MIN_LENGTH) {
			throw new WeakPasswordException("Password must be at least " + MIN_LENGTH + " characters long");
		}

		if (password.length() > MAX_LENGTH) {
			throw new WeakPasswordException("Password cannot exceed " + MAX_LENGTH + " characters");
		}

		if (!UPPERCASE_PATTERN.matcher(password).matches()) {
			throw new WeakPasswordException("Password must contain at least one uppercase letter");
		}

		if (!LOWERCASE_PATTERN.matcher(password).matches()) {
			throw new WeakPasswordException("Password must contain at least one lowercase letter");
		}

		if (!DIGIT_PATTERN.matcher(password).matches()) {
			throw new WeakPasswordException("Password must contain at least one digit");
		}

		if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
			throw new WeakPasswordException("Password must contain at least one special character");
		}

		// Check for common weak passwords
		if (isCommonPassword(password)) {
			throw new WeakPasswordException("Password is too common. Please choose a stronger password");
		}
	}

	private boolean isCommonPassword(String password) {
		String lowerPassword = password.toLowerCase();
		String[] commonPasswords = {
				"password", "123456", "password123", "admin", "qwerty",
				"letmein", "welcome", "monkey", "dragon", "master"
		};

		for (String common : commonPasswords) {
			if (lowerPassword.contains(common)) {
				return true;
			}
		}
		return false;
	}

	public String generatePasswordRequirements() {
		return "Password must be " + MIN_LENGTH + "-" + MAX_LENGTH +
				" characters long and contain at least one uppercase letter, " +
				"one lowercase letter, one digit, and one special character.";
	}
}
