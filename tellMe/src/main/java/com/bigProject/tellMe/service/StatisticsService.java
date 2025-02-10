package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.StatisticsDTO;  // 통계 DTO
import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.enumClass.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final QuestionService questionService;  // 민원 서비스
    private final ReportService reportService;  // 보고서 서비스

    // 민원과 보고서의 상태별 통계
    public StatisticsDTO getStatistics() {
        // 민원 상태별 통계
        long 접수중Count = questionService.countByStatus(Status.접수중);
        long 처리중Count = questionService.countByStatus(Status.처리중);
        long 답변완료Count = questionService.countByStatus(Status.답변완료);

        // 보고서 상태별 통계
        long 미확인Count = reportService.countReportsByStatus(ReportStatus.미확인);
        long 확인완료Count = reportService.countReportsByStatus(ReportStatus.확인완료);

        return new StatisticsDTO(접수중Count, 처리중Count, 답변완료Count, 미확인Count, 확인완료Count);
    }
}
