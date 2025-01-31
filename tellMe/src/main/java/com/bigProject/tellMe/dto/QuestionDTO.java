package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.enumClass.Status;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime createDate = LocalDateTime.now();
    private Reveal reveal;
    private Status status;
    private Integer views = 0;
    private Category category;
    private String file1 = null;
    private String file2 = null;
    private String file3 = null;

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
