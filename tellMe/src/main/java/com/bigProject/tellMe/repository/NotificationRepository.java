package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop5ByUserIdOrderByCreateDateDesc(Long userId);

    List<Notification> findTop5ByUserIdOrderByCreateDateAsc(Long userId);
}
