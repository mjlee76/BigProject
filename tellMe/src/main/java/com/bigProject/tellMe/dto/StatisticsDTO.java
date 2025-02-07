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
}
