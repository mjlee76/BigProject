package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController  // ✅ 반드시 @RestController 사용!
@RequestMapping("/password")  // ✅ 이 경로가 정확한지 확인!
@RequiredArgsConstructor
public class PasswordController {

    private final UserService userService;

    @PostMapping("/update")  // ✅ "/password/update" 경로가 존재하는지 확인!
    public ResponseEntity<Map<String, String>> updatePassword(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        System.out.println("🔹 비밀번호 변경 요청 받음!"); // ✅ 디버깅 로그 추가

        boolean isUpdated = userService.updatePassword(userDetails.getUsername(), currentPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        if (isUpdated) {
            response.put("success", "true");
            System.out.println("✅ 비밀번호 변경 성공!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", "false");
            response.put("message", "현재 비밀번호가 올바르지 않습니다.");
            System.out.println("❌ 비밀번호 변경 실패: 현재 비밀번호 불일치");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
