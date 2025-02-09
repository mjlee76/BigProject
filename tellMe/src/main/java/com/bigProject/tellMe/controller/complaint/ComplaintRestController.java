package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ComplaintRestController {
    private final QuestionService questionService;
    private final UserService userService;

    @PostMapping("/api/spam")
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
}
