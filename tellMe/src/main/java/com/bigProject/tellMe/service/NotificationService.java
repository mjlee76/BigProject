package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.NotificationDTO;
import com.bigProject.tellMe.entity.Notification;
import com.bigProject.tellMe.mapper.NotificationMapper;
import com.bigProject.tellMe.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    // 특정 유저의 알림 목록 조회
    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        return notificationRepository.findTop5ByUserIdOrderByCreateDateDesc(userId)
                .stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getUser().getId(),
                        notification.getMessage(),
                        notification.isRead()
                ))
                .toList();
    }

    // 새로운 알림 저장
    @Transactional
    public void createNotification(Long userId, String message) {
        // 1️⃣ DB에 알림 저장
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .userId(userId)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notificationMapper.nocaDTOToNoca(notificationDTO));
    }

    // ✅ 알림을 읽음 상태로 변경하는 기능
    @Transactional
    public void markAsRead(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);

        optionalNotification.ifPresent(notification -> {
            // ✅ 읽음 상태 변경 (Setter 없이 Dirty Checking 활용)
            notification.markAsRead();
        });
    }
}
