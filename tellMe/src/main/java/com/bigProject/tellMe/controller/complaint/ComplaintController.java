package com.bigProject.tellMe.controller.complaint;

import com.bigProject.tellMe.config.FileUpLoadUtil;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.mapper.QuestionMapper;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/complaint")
@RequiredArgsConstructor
public class ComplaintController {
    private final QuestionMapper questionMapper;
    private final UserService userService;
    private final QuestionService questionService;

    @GetMapping("/new")
    public String newComplaintForm(Authentication auth, Model model) {
        UserDTO user = userService.findByUserId(auth.getName());
        QuestionDTO questionDto = new QuestionDTO();
        questionDto.setUserId(user.getId());
        questionDto.setReveal(Reveal.공개);
        model.addAttribute("question", questionDto);

        return "complaint/new";
    }

    @PostMapping("/create")
    public String createComplaint(QuestionDTO questionDTO, @RequestParam("files") List<MultipartFile> multipartFiles) throws IOException {
        Question question = questionService.save(questionDTO);
        if(!multipartFiles.isEmpty()) {
            questionDTO = questionMapper.quToQuDTO(question);
            String uploadDir = "tellMe/tellMe-uploadFile/question/" + questionDTO.getId();

            for (int i = 0; i < multipartFiles.size(); i++) {
                MultipartFile multipartFile = multipartFiles.get(i);
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                FileUpLoadUtil.saveFile(uploadDir, fileName, multipartFile);

                switch (i) {
                    case 0:
                        questionDTO.setFile1(fileName);
                        break;
                    case 1:
                        questionDTO.setFile2(fileName);
                        break;
                    case 2:
                        questionDTO.setFile3(fileName);
                        break;
                }
            }
            questionService.save(questionDTO);
        }


        return "redirect:/complaint/board";
    }

    @GetMapping("/board")
    public String complaintBoard() {
        return "complaint/board";
    }
}
