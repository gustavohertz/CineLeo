package com.leocine.repository;

import com.leocine.entity.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationMessage, String> {
}