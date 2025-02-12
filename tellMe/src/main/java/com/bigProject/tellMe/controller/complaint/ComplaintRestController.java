package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.config.FileUpLoadUtil;
import com.bigProject.tellMe.dto.NotificationDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.service.NotificationService;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ComplaintRestController {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    //private final CopyOnWriteArrayList<SseEmitter> refreshEmitters = new CopyOnWriteArrayList<>();
    private final QuestionService questionService;
    private final UserService userService;
    private final NotificationService notificationService;

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("===========uploadFile" + file);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);
        try {
            String uploadDir = System.getProperty("user.dir") + "/apiCheck-uploadFile";
            FileUpLoadUtil.saveFiles(uploadDir, files);
            uploadDir = System.getProperty("user.dir") + "/apiCheck-uploadFile";
            String response = questionService.uploadFileApi(uploadDir);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }

    @PostMapping("/spam")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> spamCheck(@RequestBody Map<String, String> request) {
        try{
            Map<String, Object> response = questionService.spamCheck(request);

            return ResponseEntity.ok(response);
        }catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "잘못된 userId 형식입니다."));
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("valid", false, "message", "사용자를 찾을 수 없습니다."));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("valid", false, "message", "서버 오류 발생: " + e.getMessage()));
        }

    }

    // ✅ SSE 구독 (알림 + 새로고침 통합)
    @GetMapping("/sse/{userId}")
    public SseEmitter subscribe(@PathVariable String userId) {
        UserDTO user = userService.findByUserId(userId);
        Long id = user.getId();
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃
        emitters.put(id, emitter);

        // 연결 종료 처리
        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));

        return emitter;
    }

    // ✅ 특정 사용자에게 이벤트 전송 (알림 또는 새로고침)
    public void triggerEvent(@PathVariable Long userId, @RequestParam String type, @RequestBody(required = false) String message) {
        SseEmitter emitter = emitters.get(userId);
        if(emitter == null) {
            emitter = new SseEmitter(60 * 1000L);
            emitters.put(userId, emitter);
        }
        try {
            if ("notification".equals(type)) {
                emitter.send(SseEmitter.event().name(type).data(message));
            } else if ("refresh".equals(type)) {
                emitter.send(SseEmitter.event().name(type).data("reload"));
            }
            //emitter.send(SseEmitter.event().data(message));
        } catch (IOException e) {
            emitter.complete();
            emitters.remove(userId);
        }
    }

    //알림내역 불러오기 5개만
    @GetMapping("/notifiList/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable String userId) {
        System.out.println("======================notifiList : " + userId);
        UserDTO user = userService.findByUserId(userId);
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser(user.getId());
        System.out.println("======================notifiList" + notifications);
        return ResponseEntity.ok(notifications);
    }

    // ✅ 알림 클릭 시 isRead 값을 true로 변경
    @PostMapping("/markAsRead")
    public ResponseEntity<Void> markAsRead(@RequestBody Map<String, Long> requestBody) {
        Long notificationId = requestBody.get("id");
        System.out.println("===========markAsRead : "+notificationId);
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
