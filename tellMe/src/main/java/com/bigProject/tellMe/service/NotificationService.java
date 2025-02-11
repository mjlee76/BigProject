package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.NotificationDTO;
import com.bigProject.tellMe.entity.Notification;
import com.bigProject.tellMe.mapper.NotificationMapper;
import com.bigProject.tellMe.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    // 특정 유저의 알림 목록 조회
    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreateDateDesc(userId)
                .stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
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

        // 2️⃣ SSE로 실시간 전송
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(userId);
            }
        }
    }

    // ✅ SSE 알림 구독 (클라이언트가 실시간 알림 받기)
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 연결 유지
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    // ✅ 알림을 읽음 상태로 변경하는 기능
    @Transactional
    public void markAsRead(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);

        optionalNotification.ifPresent(notification -> {
            // ✅ 읽음 상태 변경 (Setter 없이 Dirty Checking 활용)
            notification.markAsRead();
        });
//        optionalNotification.ifPresent(notification -> {
//            // ✅ DTO에서 isRead 값을 가져와 엔티티를 업데이트
//            Notification updatedNotification = Notification.builder()
//                    .id(notification.getId())
//                    .user(notification.getUser())
//                    .message(notification.getMessage())
//                    .isRead(notificationDTO.isRead()) // ✅ DTO 값을 적용
//                    .createDate(notification.getCreateDate())
//                    .build();
//
//            notificationRepository.save(updatedNotification); // ✅ 변경된 엔티티 저장
//        });
    }
}
