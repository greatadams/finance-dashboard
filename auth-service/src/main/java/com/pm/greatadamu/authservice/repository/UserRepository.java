package com.pm.greatadamu.authservice.repository;

import com.pm.greatadamu.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email); //-> used to prevent duplicated email in registry
    Optional<User> findByEmail(String email); //->used for login




}
