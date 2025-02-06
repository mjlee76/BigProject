package com.bigProject.tellMe.client.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionApiDTO {
    Long id;
    String title;
    String content;
}
