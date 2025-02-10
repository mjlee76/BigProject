package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String report;
    private String category;
    private ReportStatus reportStatus;
    private LocalDateTime createDate;
}
