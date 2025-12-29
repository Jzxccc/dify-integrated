package com.example.difyintegration.repository;

import com.example.difyintegration.entity.AppInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppInteractionRepository extends JpaRepository<AppInteraction, Long> {

    List<AppInteraction> findByAppId(String appId);

    Page<AppInteraction> findByAppId(String appId, Pageable pageable);

    List<AppInteraction> findByUserId(String userId);

    Page<AppInteraction> findByUserId(String userId, Pageable pageable);

    List<AppInteraction> findByConversationId(String conversationId);

    @Query("SELECT ai FROM AppInteraction ai WHERE ai.appId = :appId AND ai.userId = :userId")
    List<AppInteraction> findByAppIdAndUserId(@Param("appId") String appId, @Param("userId") String userId);

    @Query("SELECT ai FROM AppInteraction ai WHERE ai.appId = :appId AND ai.userId = :userId")
    Page<AppInteraction> findByAppIdAndUserId(@Param("appId") String appId, @Param("userId") String userId, Pageable pageable);
}