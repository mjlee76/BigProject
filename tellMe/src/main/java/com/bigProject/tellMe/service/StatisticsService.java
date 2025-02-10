package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.StatisticsDTO;  // 통계 DTO
import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.enumClass.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

        // 전체 민원 수 (오늘만의 민원 수)
        long 전체민원수 = questionService.countTodayQuestions();

        long yesterdayQuestionCount = questionService.countYesterdayQuestions();


        // 오늘의 악성 민원 수
        long 악성민원수 = questionService.countTodayCategoryNotNormal();

        // 일반 민원수 계산: 전체 민원수 - 악성 민원수
        long 일반민원수 = 전체민원수 - 악성민원수;

<<<<<<< HEAD
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
=======
        // 악성 민원 비율
        double 악성민원비율 = 전체민원수 == 0 ? 0 : (double) 악성민원수 / 전체민원수 * 100;

        // 평균 처리 시간 (예시로 1시간으로 설정)
        double 평균처리시간 = calculateAverageProcessingTime();

<<<<<<< HEAD
        return new StatisticsDTO(접수중Count, 처리중Count, 답변완료Count, 미확인Count, 확인완료Count, 전체민원수, 악성민원수, 악성민원비율, 평균처리시간);
>>>>>>> fe779d2 (실시간 민원 현황 - 모든 날짜 조회)
=======
        // StatisticsDTO 객체에 일반민원수 추가
<<<<<<< HEAD
        return new StatisticsDTO(접수중Count, 처리중Count, 답변완료Count, 미확인Count, 확인완료Count, 전체민원수, 악성민원수, 악성민원비율, 평균처리시간, 일반민원수);
>>>>>>> 13fde1d (실시간 민원 현황 상세페이지 기능 추가)
=======
        return new StatisticsDTO(접수중Count, 처리중Count, 답변완료Count, 미확인Count, 확인완료Count, 전체민원수, 악성민원수, 악성민원비율, 평균처리시간, 일반민원수,yesterdayQuestionCount);
>>>>>>> bc74830 (통계 추가)
    }


    private double calculateAverageProcessingTime() {
        // 평균 처리 시간 계산 로직 (예시로 1시간 반환)
        return 1.0; // 실제로는 데이터베이스에서 처리 시간을 계산하여 반환
    }

    public Map<String, List<Long>> getQuestionsAndMaliciousByHour() {
        LocalDate today = LocalDate.now();
        return questionService.countQuestionsAndMaliciousByHour(today);
    }

    // StatisticsService 클래스에 추가
    public double calculateDailyChangeRate() {
        long todayQuestions = questionService.countTodayQuestions();
        long yesterdayQuestions = questionService.countYesterdayQuestions();

        // 어제 민원 수가 0일 경우에는 0으로 처리
        if (yesterdayQuestions == 0) {
            return todayQuestions == 0 ? 0 : 100;  // 오늘 민원 수가 0이면 변화율 0%, 아니면 100%
        }

        return ((double) (todayQuestions - yesterdayQuestions) / yesterdayQuestions);
    }







}
