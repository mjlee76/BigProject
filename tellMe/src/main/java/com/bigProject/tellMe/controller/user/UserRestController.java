package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.repository.UserRepository;
import com.bigProject.tellMe.service.EmailService;
import com.bigProject.tellMe.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.Objects;

import jakarta.mail.MessagingException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;

    //태식 : 회원가입할 때 userId랑 phone, email이 중복되는지 체크
    @PostMapping("/user/check/unique")
    public String checkIdDuplicate(@RequestParam("userId") String userId, @RequestParam("phone") String phoneNum, @RequestParam("email") String eMail) {
        return userService.checkUnique(userId, phoneNum, eMail);
    }

    //태식 : 아이디 찾기
    @PostMapping("/user/check_name/find_id")
    public String checkFindId(@RequestParam("userName") String userName, @RequestParam("phone") String phoneNum) {
        return userService.checkNameAndFindId(userName, phoneNum);
    }

    //태식 : 비밀번호 찾기 전 : 아이디 체크
    @PostMapping("/user/check_id")
    public String checkId(@RequestParam("userId") String userId) {
        UserDTO user = userService.findByUserId(userId);
        if(user == null) {
            return "실패";
        }else {
            return "성공";
        }
    }

    //태식 : 비밀번호 찾기 : 아이디 체크 성공 : 임시비밀번호 발급
    @PostMapping("/user/check_name/find_password")
    public String findPassword(@RequestParam("userId") String userId, @RequestParam("userName") String userName, @RequestParam("phone") String phoneNum) {
        String checkUserId = userService.checkNameAndFindId(userName, phoneNum);

        if(userId.equals(checkUserId)) {
            return userService.FindPassword(userId);
        } else {
            return checkUserId;
        }
    }

    @PostMapping("/user/checkPassword")
    public ResponseEntity<String> checkPassword(Authentication auth, @RequestBody Map<String, String> request) {
        System.out.println("==============================");
        String password = request.get("password");

        // 비밀번호 검증 로직 (현재 로그인한 사용자와 비밀번호 비교)
        boolean isPasswordValid = userService.checkPassword(auth.getName(), password);
        System.out.println("************"+isPasswordValid);
        if (isPasswordValid) {
            return ResponseEntity.ok("Password is valid");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid password");
        }
    }

    @PostMapping("/user/updateName")
    public ResponseEntity<Map<String, String>> updateUserName(Authentication auth, @RequestBody UserDTO userDTO) {
        String userId = auth.getName();  // 현재 로그인한 사용자 ID

        boolean isUpdated = userService.updateUserName(userId, userDTO.getPassword(), userDTO.getUserName());

        Map<String, String> response = new HashMap<>();
        if (isUpdated) {
            response.put("success", "true");
            response.put("message", "이름이 성공적으로 변경되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", "false");
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/user/updatePhone")
    public ResponseEntity<Map<String, String>> updatePhone(Authentication auth, @RequestBody UserDTO userDTO) {
        String userId = auth.getName();  // 현재 로그인한 사용자 ID

        boolean isUpdated = userService.updatePhone(userId, userDTO.getPassword(), userDTO.getPhone());

        Map<String, String> response = new HashMap<>();
        if (isUpdated) {
            response.put("success", "true");
            response.put("message", "핸드폰 번호가 성공적으로 변경되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", "false");
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/user/updateEmail")
    public ResponseEntity<Map<String, String>> updateEmail(Authentication auth, @RequestBody UserDTO userDTO) {
        String userId = auth.getName();  // 현재 로그인한 사용자 ID

        boolean isUpdated = userService.updateEmail(userId, userDTO.getPassword(), userDTO.getEmail());

        Map<String, String> response = new HashMap<>();
        if (isUpdated) {
            response.put("success", "true");
            response.put("message", "이메일이 성공적으로 변경되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", "false");
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/user/updateAddress")
    public ResponseEntity<Map<String, String>> updateAddress(Authentication auth, @RequestBody UserDTO userDTO) {
        String userId = auth.getName();  // 현재 로그인한 사용자 ID

        boolean isUpdated = userService.updateAddress(userId, userDTO.getPassword(), userDTO.getAddress());

        Map<String, String> response = new HashMap<>();
        if (isUpdated) {
            response.put("success", "true");
            response.put("message", "주소가 성공적으로 변경되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", "false");
            response.put("message", "비밀번호가 일치하지 않거나 주소 변경에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 1️⃣ 이메일 인증 요청 (인증 코드 전송)
    @PostMapping("/request-verification")
    public ResponseEntity<Map<String, String>> requestVerification(Authentication auth) {
        String userId = auth.getName();  // ✅ 현재 로그인한 사용자의 ID 가져오기

        // ✅ 데이터베이스에서 사용자 정보 가져오기
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserId(userId));
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("success", "false", "message", "사용자를 찾을 수 없습니다."));
        }

        User user = userOptional.get();
        String originalEmail = user.getEmail();  // ✅ 원래 이메일 가져오기

        try {
            emailService.sendVerificationEmail(originalEmail);  // ✅ 원래 이메일로 인증 코드 전송
            return ResponseEntity.ok(Map.of("success", "true", "message", "인증 코드가 전송되었습니다."));
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(Map.of("success", "false", "message", "이메일 전송 실패"));
        }
    }

    // 2️⃣ 인증 코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(Authentication auth, @RequestBody Map<String, String> request) {
        String userId = auth.getName();
        String code = request.get("code");

        // ✅ 사용자 이메일 가져오기 (이메일을 따로 받지 않으므로 DB에서 조회)
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserId(userId));
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("success", "false", "message", "사용자를 찾을 수 없습니다."));
        }

        User user = userOptional.get();
        String email = user.getEmail();  // ✅ 사용자 원래 이메일 가져오기

        // ✅ 이메일에 해당하는 인증 코드 검증
        boolean isValid = emailService.verifyCode(email, code);
        if (isValid) {
            return ResponseEntity.ok(Map.of("success", "true", "message", "이메일 인증 성공!"));
        } else {
            return ResponseEntity.status(400).body(Map.of("success", "false", "message", "인증 코드가 올바르지 않습니다."));
        }
    }

    // 3️⃣ 이름 변경 요청 (이메일 인증 후)
    @PostMapping("/update-name")
    public ResponseEntity<Map<String, String>> updateUserName(
            Authentication auth,
            @RequestBody UserDTO userDTO,  // ✅ userName만 DTO로 받음
            @RequestHeader("Verification-Code") String verificationCode) {  // ✅ 인증 코드는 헤더에서 받음

        String userId = auth.getName();
        String newName = userDTO.getUserName();  // ✅ DTO에서 이름 가져오기

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserId(userId));
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("success", "false", "message", "사용자를 찾을 수 없습니다."));
        }

        User user = userOptional.get();
        String email = user.getEmail();

        // ✅ 이메일 인증 코드 검증
        boolean isValid = emailService.verifyCode(email, verificationCode);
        if (!isValid) {
            return ResponseEntity.status(400).body(Map.of("success", "false", "message", "인증 코드가 올바르지 않습니다."));
        }

        // ✅ 이름 변경
        user.setUserName(newName);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", "true", "message", "이름이 성공적으로 변경되었습니다."));
    }

}
