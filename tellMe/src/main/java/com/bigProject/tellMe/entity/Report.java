package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.enumClass.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @Column(nullable = false)
    private String category; // 보고서 유형

    // setter 추가
    @Setter
    @Enumerated(EnumType.STRING) // ✅ ENUM을 문자열로 저장
    @Column(nullable = false)
    private ReportStatus reportStatus;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @PrePersist
    public void setDefaultValues() {
        if(this.reportStatus == null) {
            this.reportStatus = ReportStatus.미확인;  // DB에 저장되기 전에 기본값 설정
        }
    }
}
