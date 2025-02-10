package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.config.FileUpLoadUtil;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ComplaintRestController {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;
    private final QuestionService questionService;
    private final UserService userService;

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") List<MultipartFile> file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        try {
            if(!file.isEmpty()) {
                String uploadDir = "tellMe/apiCheck-uploadFile/question";
                List<String> savedFiles = FileUpLoadUtil.saveFiles(uploadDir, file);

                String fileName = savedFiles.get(0);

                questionService.uploadFileApi(uploadDir, fileName);
            }
            return ResponseEntity.ok().body("업로드 성공: " + fileName);
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

    @GetMapping("/notiBell/{userId}")
    public SseEmitter subscribe(@PathVariable String userId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃

        emitter.onCompletion(() -> emitters.remove(userId, emitter));
        emitter.onTimeout(() -> emitters.remove(userId, emitter));

        emitters.put(userId, emitter);
        return emitter;
    }

    // 🚀 서버에서 필터링 완료 후 알림 전송
    @PostMapping("/send")
    public void sendNotification(@RequestParam String userId, @RequestParam String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(userId);
            }
        }
    }



//    @PostMapping("/sendRefresh")
//    public SseEmitter streamEvents() {
//        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
//        emittersList.add(emitter);
//
//        emitter.onCompletion(() -> emittersList.remove(emitter));
//        emitter.onTimeout(() -> emittersList.remove(emitter));
//
//        return emitter;
//    }

    @PostMapping("/sendRefresh")
    public void sendRefreshEvent() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("refresh").data("reload"));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(userId);
            }
        });
    }
}
