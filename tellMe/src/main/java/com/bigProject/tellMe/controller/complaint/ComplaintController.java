package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/complaint")
@RequiredArgsConstructor
public class ComplaintController {
    private final UserService userService;
    private final QuestionService questionService;

    @GetMapping("/new")
    public String newComplaintForm() { return "complaint/new"; }

    @PostMapping("/create")
    public String createComplaint(Authentication auth, QuestionDTO questionDTO) {
        User user = userService.findByUserId(auth.getName());

        questionDTO.setCreateDate(LocalDateTime.now());
        questionDTO.setViews(0);
        questionDTO.setUserId(user.getId());
        questionDTO.setStatus(Status.처리중);

        // 2. Repository에게 Entity를 DB안에 저장하게 함!
        Question saved = questionService.save(questionDTO);

        return "redirect:/complaint/question";
    }

    @GetMapping("/question")
    public String findAll(Model model) {
        // DB에서 전체 게시글 데이터를 가져와서 question.html에 보여준다.
        List<QuestionDTO> questionDTOList = questionService.findAll();
        model.addAttribute("questionList", questionDTOList);
        return "complaint/question";
    }

//    @GetMapping("/question")
//    public String complaintBoard(@PageableDefault(page = 1) Pageable pageable, Model model) {
//        Page<QuestionDTO> questionList = questionService.paging(pageable);
//
//        int blockLimit = 5; // 화면에 보여지는 페이지 갯수
//        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;  // 1, 6, 11, ~
//        int endPage = ((startPage + blockLimit - 1) < questionList.getTotalPages()) ? startPage + blockLimit - 1 : questionList.getTotalPages();
//
//        model.addAttribute("questionList", questionList);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//
//        return "complaint/question";
//    }

    @GetMapping("/question/{id}")
    public String getQuestion(@PathVariable Long id, Model model) {
        QuestionDTO questionDTO = questionService.getQuestion(id);
        model.addAttribute("question", questionDTO);
        return "complaint/question-detail";
    }
}
