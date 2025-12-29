package com.example.difyintegration.repository;

import com.example.difyintegration.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    // Find the most recently updated API key (assuming we only have one)
    ApiKey findFirstByOrderByUpdatedAtDesc();
}