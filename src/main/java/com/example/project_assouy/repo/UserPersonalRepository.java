package com.example.project_assouy.repo;

import com.example.project_assouy.entity.User;
import com.example.project_assouy.entity.UserPersonal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPersonalRepository extends JpaRepository<UserPersonal, UUID> {
    Optional<UserPersonal> findByUserId(User user);
}