package com.pup.sis.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Injects shared attributes into the model for EVERY controller automatically.
 *
 * Currently used to expose the current request path as "currentPath" so the
 * sidebar fragment can highlight whichever nav link matches the active page.
 *
 * NOTE: Thymeleaf 3.1 (used by Spring Boot 3) no longer allows templates to
 * access #httpServletRequest directly for security reasons. This is the
 * supported replacement: inject the value via @ModelAttribute instead.
 */
@ControllerAdvice
public class GlobalModelAttributesConfig {

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}