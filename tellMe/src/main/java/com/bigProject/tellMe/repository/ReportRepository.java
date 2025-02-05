package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Report;
import com.bigProject.tellMe.enumClass.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByReportContaining(String query, Pageable pageable);
    Page<Report> findByReportStatus(String status, Pageable pageable);
    Page<Report> findByReportContainingAndReportStatus(String query, String status, Pageable pageable);
//    Page<Report> findByReportStatus(ReportStatus status, Pageable pageable);
}
