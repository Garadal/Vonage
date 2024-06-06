package com.example.demo.repositories;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, IUserRepository {
    Optional<User> findByUsername(String username);
    void deleteByIsVerifiedFalseAndRegistrationTimeBefore(LocalDateTime time);
}