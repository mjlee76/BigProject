package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.StatisticsDTO;  // 통계 DTO
import com.bigProject.tellMe.enumClass.Category;
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

//        // 민원 카테고리별 통계 - 리턴값 넣기
//        long 모욕Count = questionService.countByCategory(Category.모욕);
//        long 협박Count = questionService.countByCategory(Category.협박);
//        long 욕설Count = questionService.countByCategory(Category.욕설);
//        long 인종Count = questionService.countByCategory(Category.인종차별);
//        long 성차Count = questionService.countByCategory(Category.성차별);
//        long 성희Count = questionService.countByCategory(Category.성희롱);
//        long 장애Count = questionService.countByCategory(Category.장애인차별);
//        long 혐오Count = questionService.countByCategory(Category.혐오발언);
//        long 종교Count = questionService.countByCategory(Category.종교차별);
//        long 외모Count = questionService.countByCategory(Category.외모차별);
//        long 지역Count = questionService.countByCategory(Category.지역차별);
//        long 나이Count = questionService.countByCategory(Category.나이차별);

        return new StatisticsDTO(접수중Count, 처리중Count, 답변완료Count, 미확인Count, 확인완료Count);
    }
}
