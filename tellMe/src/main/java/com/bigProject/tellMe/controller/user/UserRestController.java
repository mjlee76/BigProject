package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    //태식 : 회원가입할 때 userId랑 phone, email이 중복되는지 체크
    @PostMapping("/user/check/unique")
    public String checkIdDuplicate(@RequestParam("userId") String userId, @RequestParam("password") String password,
                                   @RequestParam("phone") String phoneNum, @RequestParam("email") String eMail) {
        return userService.checkUnique(userId, password, phoneNum, eMail);
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
    public ResponseEntity<String> updateName(Authentication auth, @RequestBody Map<String, String> request) {
        // 클라이언트로부터 받은 새 이름
        String newName = request.get("newName");

        // 사용자 이름 업데이트 처리
        boolean isUpdated = userService.updateUserName(auth.getName(), newName);

        if (isUpdated) {
            return ResponseEntity.ok("이름이 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이름 업데이트 실패");
        }
    }

    @PostMapping("/user/updatePhone")
    public ResponseEntity<String> updatePhone(Authentication auth, @RequestBody Map<String, String> request) {
        // 클라이언트로부터 받은 새 이름
        String newPhone = request.get("newPhone");

        // 사용자 이름 업데이트 처리
        boolean isUpdated = userService.updatePhone(auth.getName(), newPhone);

        if (isUpdated) {
            return ResponseEntity.ok("이름이 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이름 업데이트 실패");
        }
    }

    @PostMapping("/user/updateEmail")
    public ResponseEntity<String> updateEmail(Authentication auth, @RequestBody Map<String, String> request) {
        // 클라이언트로부터 받은 새 이름
        String newEmail = request.get("newEmail");

        // 사용자 이름 업데이트 처리
        boolean isUpdated = userService.updateEmail(auth.getName(), newEmail);

        if (isUpdated) {
            return ResponseEntity.ok("이름이 성공적으로 업데이트되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이름 업데이트 실패");
        }
    }
}
