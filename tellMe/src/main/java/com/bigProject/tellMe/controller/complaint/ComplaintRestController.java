package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ComplaintRestController {
    private final QuestionService questionService;

    @PostMapping("/api/check")
    public String checkApi(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String content = request.get("content");
        return questionService.checkApi(title, content);
    }
}
