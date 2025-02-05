package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.dto.AnswerDTO;
import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Role;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.service.AnswerService;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AnswerService answerService;

    @GetMapping("/new")
    public String newComplaintForm() { return "complaint/new"; }

    @PostMapping("/create")
    public String createComplaint(Authentication auth, QuestionDTO questionDTO) {
        UserDTO user = userService.findByUserId(auth.getName());

        questionDTO.setCreateDate(LocalDateTime.now());
        questionDTO.setViews(0);
        questionDTO.setUserId(user.getId());
        questionDTO.setStatus(Status.접수중);

        // 2. Repository에게 Entity를 DB안에 저장하게 함!
        Question saved = questionService.save(questionDTO);

        return "redirect:/complaint/question";
    }

//    // 모든 공지사항 데이터를 조회하여 뷰에 전달하는 메서드.
//    @GetMapping("/question")
//    public String findAll(Model model) {
//        // DB에서 전체 게시글 데이터를 가져와서 question.html에 보여준다.
//        List<QuestionDTO> questionDTOList = questionService.findAll();
//        model.addAttribute("questionList", questionDTOList);
//        return "complaint/question";
//    }

    // 모든 문의 데이터를 조회하여 뷰에 전달하는 메서드.
    @GetMapping("/question")
    public String complaintBoard(@PageableDefault(page = 1) Pageable pageable,
                                 Authentication auth,
                                 Model model) {

        String role = "ROLE_USER";
        // 완벽필터링 추가
        UserDTO user = null;

        if (auth != null && auth.isAuthenticated()) {
            user = userService.findByUserId(auth.getName());
            role = String.valueOf(user.getRole());
        }

        Page<QuestionDTO> questionList = questionService.paging(pageable, role);

        // 모든 데이터를 모델에 추가
        List<QuestionDTO> allQuestions = questionService.findAll();
        model.addAttribute("allQuestions", allQuestions);

        int blockLimit = 5; // 화면에 보여지는 페이지 갯수
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;  // 1, 6, 11, ~
        int endPage = ((startPage + blockLimit - 1) < questionList.getTotalPages()) ? startPage + blockLimit - 1 : questionList.getTotalPages();

        model.addAttribute("questionList", questionList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "complaint/question";
    }

    // 문의 제목을 클릭하여 상세페이지 표출 메서드
    @GetMapping("/question/{id}")
    public String getQuestion(@PathVariable Long id,
                              @RequestParam(required = false, defaultValue = "1")int page,
                              Model model) {
        QuestionDTO questionDTO = questionService.getQuestion(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("page", page);
        return "complaint/question-detail";
    }

    // 접수중을 처리중으로 변경하는 메서드
    @PostMapping("/question/{id}/status")
    public String processQuestion(@PathVariable Long id, @RequestParam(defaultValue = "1") int page) {
        questionService.updateStatusToProcessing(id);
        return "redirect:/complaint/question/" + id + "?page=" + page;  // 상세 페이지로 리다이렉트
    }

    @PostMapping("/answer")
    public String submitAnswer(@ModelAttribute AnswerDTO answerDTO,
                               Authentication auth) {

        answerService.saveAnswer(answerDTO, auth);
        return "redirect:/complaint/question/" + answerDTO.getQuestionId();
    }


    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        QuestionDTO dto = questionService.getQuestion(id);
        model.addAttribute("question", dto);
        return "question/edit-form";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute QuestionDTO dto) {
        questionService.updateQuestion(dto);
        return "redirect:/myPage/editInfo";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/myPage/editInfo";
    }

}
