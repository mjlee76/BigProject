package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.entity.Report;
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
    private Category category;
    private ReportStatus reportStatus;
    private LocalDateTime createDate;

    // Entity → DTO 변환 메서드
    public static ReportDTO toReportDTO(Report report) {
        return ReportDTO.builder()
                .id(report.getId())
                .report(report.getReport())
                .category(report.getCategory())
                .reportStatus(report.getReportStatus())
                .createDate(report.getCreateDate())
                .build();
    }
}
