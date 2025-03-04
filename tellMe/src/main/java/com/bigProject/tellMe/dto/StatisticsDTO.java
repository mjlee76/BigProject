package com.bigProject.tellMe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsDTO {
    private long 접수중Count;
    private long 처리중Count;
    private long 답변완료Count;
    private long 미확인Count;
    private long 확인완료Count;
    private long 전체민원수;
    private long 악성민원수;
    private double 악성민원비율;
    private double 평균처리시간;
    private long 일반민원수;
    private long yesterdayQuestionCount; // 어제 민원 수 (추가)

}
