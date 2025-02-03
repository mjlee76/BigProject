package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.entity.Answer;
import com.bigProject.tellMe.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private Long id;
    private String content;
    private User user;
    private String userName;
    private LocalDateTime createDate;
    private String file1;
    private Long questionId;


    public static AnswerDTO toAnswerDTO(Answer answer) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setId(answer.getId());
        answerDTO.setContent(answer.getContent());
        answerDTO.setCreateDate(answer.getCreateDate());
        answerDTO.setFile1(answer.getFile());
        answerDTO.setUserName(answer.getUser().getUserName());

        return answerDTO;
    }
}
