package dev.codescreen.repository;

import dev.codescreen.entity.AuthorizationResponse;
import dev.codescreen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorizationResponseRepository extends JpaRepository<AuthorizationResponse, String> {
}
