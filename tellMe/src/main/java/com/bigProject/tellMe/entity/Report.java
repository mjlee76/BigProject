package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder // notice
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Report {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(nullable = false)
    private String report;

    @Enumerated(EnumType.STRING) // ✅ ENUM을 문자열로 저장
    @Column(nullable = false)
    private Category category;//보고서 유형

    @Enumerated(EnumType.STRING) // ✅ ENUM을 문자열로 저장
    @Column(nullable = false)
    private ReportStatus reportStatus;

    @Column(nullable = false)
    private LocalDateTime createDate;

}