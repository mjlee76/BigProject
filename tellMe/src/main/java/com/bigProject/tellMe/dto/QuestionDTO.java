package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.enumClass.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private LocalDateTime createDate;
    private String userName;
    private Long filteredId;
    private Reveal reveal;
    private Status status;
    private Integer views;
    private String category;
    private String file1;
    private String file2;
    private String file3;
    private AnswerDTO answer;

    public QuestionDTO(Long id, String title, LocalDateTime createDate, Integer views, String userName, Status status) {
        this.id = id;
        this.title = title;
        this.createDate = createDate;
        this.views = views;
        //this.userName = userName;
        this.status = status;
    }

//    public QuestionDTO(Long id, String title, String content) {
//        this.id = id;
//        this.title = title;
//        this.content = content;
//    }

    // 목록보여주기 위해 Entity를 DTO로 변환 메서드
    public static QuestionDTO toQuestionDTO (Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setUserId(question.getUser().getId()); // ?
        //questionDTO.setUserName(question.getUser().getUserName()); // userName 설정
        questionDTO.setTitle(question.getTitle());
        questionDTO.setContent(question.getContent());
        questionDTO.setCreateDate(question.getCreateDate());
        questionDTO.setReveal(question.getReveal());
        questionDTO.setStatus(question.getStatus());
        questionDTO.setViews(question.getViews());
        questionDTO.setCategory(question.getCategory());
        questionDTO.setFile1(questionDTO.getFile1());
        questionDTO.setFile2(questionDTO.getFile2());
        questionDTO.setFile3(questionDTO.getFile3());

//        if (question.getAnswer() != null) {
//            questionDTO.setAnswer(AnswerDTO.toAnswerDTO(question.getAnswer()));
//        }

        return questionDTO;
    }

    public void updateEntity(Question question) {
        question.updateTitleAndContent(this.title, this.content);
    }

//    public QuestionDTO(Question question) {
//        id = question.getId();
//        title = question.getTitle();
//        content = question.getContent();
//        userId = question.getUser().getId();
//        //answerId = question.getAnswer().getId();
//        //originId = question.getOrigin().getId();
//        createDate = question.getCreateDate();
//        reveal = question.getReveal();
//        status = question.getStatus();
//        views = question.getViews();
//        file1 = question.getFile1();
//        file2 = question.getFile2();
//        file3 = question.getFile3();
//    }

//    public Question toEntity() {
//        return Question.builder()
//                .title(title)                       // 제목 설정
//                .content(content)                   // 내용 설정
//                .createDate(LocalDateTime.now())    // 현재 시간 설정
//                .reveal(reveal)     // String을 Reveal Enum으로 변환
//                .status(Status.처리중)              // status 기본값 설정
//                .views(0)                           // 조회수 기본값 설정
//                .file1(null)                        // file1, file2, file3은 null로 설정
//                .file2(null)
//                .file3(null)
//                .build();
//    }
}
