package com.pup.sis.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Central security configuration.
 * Defines which URLs are protected, the login page, and
 * where each role is redirected after successful login.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // BCrypt is the standard for password hashing. Never store plain text.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Wires our custom UserDetailsService and the password encoder together.
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
        return provider;
}

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authenticationProvider(authenticationProvider())
        .authorizeHttpRequests(auth -> auth
            // Public pages - no authentication needed
            .requestMatchers(
                "/", "/welcome",
                "/login/student", "/login/faculty", "/login/admin",
                "/student/Change-password", "/faculty/Change-Password-Faculty/**",
                "/api/**",
                "/css/**", "/js/**", "/images/**"
            ).permitAll()
            // Role-locked sections
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/faculty/**").hasRole("FACULTY")
            .requestMatchers("/student/**").hasRole("STUDENT")
            // Everything else requires login
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            // Unauthenticated users land on welcome, not a login page
            .loginPage("/welcome")
            .loginProcessingUrl("/login")
            .successHandler(roleBasedSuccessHandler())
            // On failure, go back to whichever role login page the user came from
            .failureUrl("/login/student?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/welcome")
            .permitAll()
        );

    return http.build();
}

@Bean
public AuthenticationSuccessHandler roleBasedSuccessHandler() {
    return (HttpServletRequest req, HttpServletResponse res, Authentication auth) -> {
        String role = auth.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority())
            .orElse("");

        switch (role) {
            case "ROLE_ADMIN"   -> res.sendRedirect("/admin/dashboard");
            case "ROLE_FACULTY" -> res.sendRedirect("/faculty/dashboard");
            case "ROLE_STUDENT" -> res.sendRedirect("/student/dashboard");
            default             -> res.sendRedirect("/welcome");
        }
    };
}
}