package com.pup.sis.repository;

import com.pup.sis.entity.Role;
import com.pup.sis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // NOTE: usernames are no longer guaranteed unique (a deactivated account
    // may share its old username with a newly created active account). Do
    // NOT use this for login — use findEnabledByUsername instead, or this
    // can throw NonUniqueResultException if a duplicate exists.
    Optional<User> findByUsername(String username);

    // Used for login: resolves to the ENABLED account for a given username,
    // ignoring any deactivated accounts that may share the same username.
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.enabled = true")
    Optional<User> findEnabledByUsername(@Param("username") String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}