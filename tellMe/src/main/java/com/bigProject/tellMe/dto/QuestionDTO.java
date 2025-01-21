package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.entity.Question;
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
    private Integer id;
    private String title;
    private String content;
    private Reveal reveal;
    private Status status;
    private LocalDateTime createDate;
    private String file1;
    private String file2;
    private String file3;
    private Integer views;

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
