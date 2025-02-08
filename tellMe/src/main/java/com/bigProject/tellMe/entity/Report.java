package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder // notice
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Report {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    // ✅ 보고서 경로를 변경하는 setter 추가
    @Setter
    @Column(nullable = false)
    private String report;

    @Enumerated(EnumType.STRING) // ✅ ENUM을 문자열로 저장
    @Column(nullable = false)
    private Category category; // 보고서 유형

    // setter 추가
    @Setter
    @Enumerated(EnumType.STRING) // ✅ ENUM을 문자열로 저장
    @Column(nullable = false)
    private ReportStatus reportStatus;

    @Column(nullable = false)
    private LocalDateTime createDate;
}
