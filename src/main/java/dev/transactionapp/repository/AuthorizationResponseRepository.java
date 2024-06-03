package dev.transactionapp.repository;

import dev.transactionapp.entity.AuthorizationResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationResponseRepository extends JpaRepository<AuthorizationResponse, String> {
}
