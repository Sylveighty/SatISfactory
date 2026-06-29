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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.AuthenticationException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                .requestMatchers(
                    "/", "/welcome",
                    "/login/student", "/login/faculty", "/login/admin",
                    "/forgot-password",
                    "/reset-password",
                    "/reset-password-success",
                    "/api/**",
                    "/css/**", "/js/**", "/images/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/faculty/**").hasRole("FACULTY")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/welcome")
                .loginProcessingUrl("/login")
                .successHandler(roleBasedSuccessHandler())
                .failureHandler(roleBasedFailureHandler())
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/reset-password") || uri.startsWith("/forgot-password")) {
                        response.sendRedirect(uri + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
                    } else {
                        response.sendRedirect("/welcome");
                    }
                })
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

            // Check if the user logged in from the correct portal
            String loginPage = req.getParameter("loginPage");

            boolean wrongPortal = switch (loginPage) {
                case "admin"   -> !role.equals("ROLE_ADMIN");
                case "faculty" -> !role.equals("ROLE_FACULTY");
                case "student" -> !role.equals("ROLE_STUDENT");
                default -> false;
            };

            if (wrongPortal) {
                // Immediately log them out and redirect back with error
                req.getSession().invalidate();
                res.sendRedirect("/login/" + loginPage + "?error=wrong_role");
                return;
            }

            switch (role) {
                case "ROLE_ADMIN"   -> res.sendRedirect("/admin/dashboard");
                case "ROLE_FACULTY" -> res.sendRedirect("/faculty/dashboard");
                case "ROLE_STUDENT" -> res.sendRedirect("/student/dashboard");
                default             -> res.sendRedirect("/welcome");
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler roleBasedFailureHandler() {
        return (HttpServletRequest req, HttpServletResponse res, AuthenticationException exception) -> {
            // Redirect back to whichever login page they came from
            String loginPage = req.getParameter("loginPage");
            String redirect = switch (loginPage != null ? loginPage : "") {
                case "admin"   -> "/login/admin?error=true";
                case "faculty" -> "/login/faculty?error=true";
                default        -> "/login/student?error=true";
            };
            res.sendRedirect(redirect);
        };
    }
}