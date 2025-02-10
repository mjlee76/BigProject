package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ComplaintRestController {
    private final List<SseEmitter> emittersList = new CopyOnWriteArrayList<>();
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final QuestionService questionService;
    private final UserService userService;

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

//    @PostMapping("/api/check")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> checkApi(@RequestBody Map<String, String> request) {
//        try{
//            Long id = Long.parseLong(request.get("userId"));
//            UserDTO userDTO = userService.findById(id);
//            Map<String, Object> response = questionService.checkApi(request, userDTO);
//
//            return ResponseEntity.ok(response);
//        }catch (NumberFormatException e) {
//            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "잘못된 userId 형식입니다."));
//        }catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("valid", false, "message", "사용자를 찾을 수 없습니다."));
//        }catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("valid", false, "message", "서버 오류 발생: " + e.getMessage()));
//        }
//
//    }

//    @GetMapping("/notiBell/{userId}")
//    public SseEmitter subscribe(@PathVariable String userId) {
//        System.out.println("==========================userId : "+userId);
//        SseEmitter emitter = new SseEmitter(60 * 1000L); // 타임아웃 없음
//        emitters.put(userId, emitter);
//
//        emitter.onCompletion(() -> emitters.remove(userId)); // 연결 종료 시 제거
//        emitter.onTimeout(() -> emitters.remove(userId)); // 타임아웃 시 제거
//
//        return emitter;
//    }
//
//    // 🚀 서버에서 필터링 완료 후 알림 전송
//    @PostMapping("/send")
//    public void sendNotification(@RequestParam String userId, @RequestParam String message) {
//        SseEmitter emitter = emitters.get(userId);
//        if (emitter == null) {
//            System.err.println("🚨 [SSE 오류] userId: " + userId + "의 SSE 연결이 없음.");
//            return;
//        }
//        try {
//            emitter.send(SseEmitter.event().data(message));
//            System.out.println("✅ [SSE 전송 완료] userId: " + userId);
//        } catch (IOException e) {
//            System.err.println("🚨 [SSE 오류] 메시지 전송 실패 (연결 끊어짐): " + e.getMessage());
//            emitter.complete();
//            emitters.remove(userId);
//        }
//    }

    @GetMapping("/events")
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emittersList.add(emitter);

        emitter.onCompletion(() -> emittersList.remove(emitter));
        emitter.onTimeout(() -> emittersList.remove(emitter));

        return emitter;
    }

    public void sendRefreshEvent() {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        for (SseEmitter emitter : emittersList) {
            try {
                emitter.send(SseEmitter.event().name("refresh").data("reload"));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emittersList.removeAll(deadEmitters);
    }
}
