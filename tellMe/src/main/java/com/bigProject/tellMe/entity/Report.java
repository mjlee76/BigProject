package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private ReportStatus reportStatus;

    @Column(nullable = false)
    private LocalDateTime createDate;

}
