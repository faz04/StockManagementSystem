package com.stockmanagement.usermanagementsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
	 * Configure static resource handling for frontend files
	 * Serves HTML, CSS, JS files from static directory
	 */
	@Override
	public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		// Handle all static resources
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/static/")
				.setCachePeriod(0); // Disable caching for development
		
		// Explicitly handle pages directory
		registry.addResourceHandler("/pages/**")
				.addResourceLocations("classpath:/static/pages/")
				.setCachePeriod(0);
		
		// Handle favicon requests to prevent 403 errors
		registry.addResourceHandler("/favicon.ico")
				.addResourceLocations("classpath:/static/")
				.setCachePeriod(3600);
	}

	/**
	 * Configure CORS for API endpoints
	 * This allows frontend to access the REST API that connects to SQL database
	 */
	@Override
	public void addCorsMappings(@NonNull CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins("*")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(false);
	}
}