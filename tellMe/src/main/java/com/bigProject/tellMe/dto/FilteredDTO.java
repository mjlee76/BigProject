package com.bigProject.tellMe.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FilteredDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createDate;
}
