package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreateDateDesc(Long userId);
}
