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
@EntityListeners(AuditingEntityListener.class)
public class Report {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(nullable = false)
    private String report;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @CreatedDate
    private LocalDateTime createDate;

}