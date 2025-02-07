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
        // 전체 민원 수
        long 전체민원수 = questionService.countAllQuestions();
        // 악성 민원 수
        long 악성민원수 = questionService.countByCategoryNotNormal();

        // 악성 민원 비율
        double 악성민원비율 = 전체민원수 == 0 ? 0 : (double) 악성민원수 / 전체민원수 * 100;

        // 평균 처리 시간 (예시로 1시간으로 설정)
        double 평균처리시간 = calculateAverageProcessingTime();

        return new StatisticsDTO(접수중Count, 처리중Count, 답변완료Count, 미확인Count, 확인완료Count, 전체민원수, 악성민원수, 악성민원비율, 평균처리시간);
    }

    private double calculateAverageProcessingTime() {
        // 평균 처리 시간 계산 로직 (예시로 1시간 반환)
        return 1.0; // 실제로는 데이터베이스에서 처리 시간을 계산하여 반환
    }


}
