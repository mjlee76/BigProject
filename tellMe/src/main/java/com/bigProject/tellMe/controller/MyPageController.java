package com.bigProject.tellMe.controller;

import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
public class MyPageController {
    private final UserService userService;
    private final QuestionService questionService;

    @GetMapping("/editInfo")
    public String myPage(Authentication auth, Model model) {
        UserDTO userDTO = userService.findByUserId(auth.getName());

        userDTO.setPhone(maskPhNum(userDTO.getPhone()));
        userDTO.setEmail(maskEmail(userDTO.getEmail()));

        User user = userService.findUserById(userDTO.getId());
        List<QuestionDTO> questions = questionService.findQuestionsByUser(user);

        model.addAttribute("user", userDTO);
        model.addAttribute("questions", questions);

        return "mypage/main";
    }
    private String maskPhNum(String phone) {
        if (phone != null && phone.length() == 11) {
            return phone.substring(0, 3) + "-" + phone.charAt(3) + "***-" + phone.charAt(7) + "***";
        }
        return phone;
    }
    private String maskEmail(String email) {
        if (email != null && email.contains("@")) {
            // 이메일에서 @ 위치 찾기
            int atIndex = email.indexOf("@");
            int lengthToMask = atIndex - 2;
            // StringBuilder를 사용하여 * 반복 생성

            // 첫 두 글자는 그대로 추가 // * 문자를 lengthToMask만큼 반복하여 추가 // @ 이후의 도메인 부분은 그대로 추가
            return email.substring(0, 2) + "*".repeat(Math.max(0, lengthToMask)) + email.substring(atIndex);
        }
        return email; // 유효하지 않은 이메일은 그대로 반환
    }

    @GetMapping("/editPassword")
    public String editInfo(Authentication auth, Model model) {

        UserDTO userDTO = userService.findByUserId(auth.getName());
        userDTO.setPhone(maskPhNum(userDTO.getPhone()));
        userDTO.setEmail(maskEmail(userDTO.getEmail()));

        User user = userService.findUserById(userDTO.getId());
        List<QuestionDTO> questions = questionService.findQuestionsByUser(user);

        model.addAttribute("user", userDTO);
        model.addAttribute("questions", questions);


        return "mypage/edit_password";
    }

    @GetMapping("/MyComplaint")
    public String mycomplaint(Authentication auth, Model model) {

        UserDTO userDTO = userService.findByUserId(auth.getName());
        userDTO.setPhone(maskPhNum(userDTO.getPhone()));
        userDTO.setEmail(maskEmail(userDTO.getEmail()));

        User user = userService.findUserById(userDTO.getId());
        List<QuestionDTO> questions = questionService.findQuestionsByUser(user);

        model.addAttribute("user", userDTO);
        model.addAttribute("questions", questions);

        return "mypage/my_complaint.html";
    }

}
