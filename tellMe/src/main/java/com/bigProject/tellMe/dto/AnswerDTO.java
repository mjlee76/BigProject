package com.bigProject.tellMe.dto;

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
    private LocalDateTime createDate;
    private String file1;
}
