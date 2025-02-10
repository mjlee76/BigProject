package com.bigProject.tellMe.controller;

import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
public class MyPageController {
    private final UserService userService;
    private final QuestionService questionService;

    @GetMapping("/myComplaint")
    public String myPage(@RequestParam(required = false) String query,
                         @RequestParam(required = false) Status status,
                         @RequestParam(required = false) String category, // 카테고리 추가
                         @RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "5") int size,
                         Authentication auth,
                         Model model) {
        UserDTO userDTO = userService.findByUserId(auth.getName());
        User user = userService.findUserById(userDTO.getId());

        userDTO.setPhone(maskPhNum(userDTO.getPhone()));
        userDTO.setEmail(maskEmail(userDTO.getEmail()));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<QuestionDTO> questions = questionService.searchUserQuestions(user, query, status, category, pageable);

        int blockLimit = 5;
        int startPage = Math.max(1, ((page - 1) / blockLimit) * blockLimit + 1);
        int endPage = Math.min(startPage + blockLimit - 1, questions.getTotalPages());

        model.addAttribute("user", userDTO);
        model.addAttribute("questions", questions);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questions.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("status", status);
        model.addAttribute("category", category); // 카테고리 추가

        return "mypage/my_complaint";
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


    @GetMapping("/editInfo")
    public String mycomplaint(Authentication auth, Model model) {

        UserDTO userDTO = userService.findByUserId(auth.getName());
        userDTO.setPhone(maskPhNum(userDTO.getPhone()));
        userDTO.setEmail(maskEmail(userDTO.getEmail()));

        User user = userService.findUserById(userDTO.getId());
        List<QuestionDTO> questions = questionService.findQuestionsByUser(user);

        model.addAttribute("user", userDTO);
        model.addAttribute("questions", questions);

        return "mypage/main";
    }

}
