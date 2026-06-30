package com.pup.sis.security;

import com.pup.sis.entity.User;
import com.pup.sis.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security calls this during login to load the user by username.
 * We translate our User entity into Spring's UserDetails format.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Resolves to the ENABLED account only. If a deactivated account
        // shares the same username (e.g. an old mis-encoded student number
        // reused by a new student), it is ignored here, so login always
        // succeeds against the active record.
        User user = userRepository.findEnabledByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Spring Security expects authorities in "ROLE_X" format.
        // Our enum is ADMIN, so we produce "ROLE_ADMIN".
        SimpleGrantedAuthority authority =
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true,  // accountNonExpired
            true,  // credentialsNonExpired
            true,  // accountNonLocked
            List.of(authority)
        );
    }
}