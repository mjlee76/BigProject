package com.bigProject.tellMe.controller.manager;

import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ReportController {
    private final UserService userService;
    private final QuestionService questionService;

    @GetMapping("/report")
    public String reportBoard() {
        return "manager/report";
    }

    @GetMapping("/statistics")
    public String statisticsBoard() {return "manager/statistics"; }


}
