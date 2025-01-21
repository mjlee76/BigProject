package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.dto.ComplaintFormDTO;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/complaint")
public class ComplaintController {

    @Autowired // 스프링 부트가 미리 생성해놓은 객체를 가져다가 자동 연결
    private QuestionRepository questionRepository;

    @GetMapping("/board")
    public String complaintBoard() {
        return "complaint/board";
    }

    @GetMapping("/new")
    public String newComplaintForm() {
        return "complaint/new";
    }

    @PostMapping("/create")
    public String createComplaint(ComplaintFormDTO complaintFormDTO) {
        System.out.println(complaintFormDTO.toString());

        // 1. Dto를 Entity로 변환!
        Question question = complaintFormDTO.toEntity();

        // 2. Repository에게 Entity를 DB안에 저장하게 함!
        Question saved = questionRepository.save(question);

        return "";
    }
}
