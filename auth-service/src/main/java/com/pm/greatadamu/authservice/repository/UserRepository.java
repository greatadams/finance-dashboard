package com.pm.greatadamu.authservice.repository;

import com.pm.greatadamu.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailNormalized(String email); //-> used to prevent duplicated email in registry
    Optional<User> findByEmailNormalized(String email); //->used for login




}
