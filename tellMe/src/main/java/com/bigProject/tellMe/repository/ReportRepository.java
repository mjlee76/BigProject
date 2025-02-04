package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportContaining(String query);
    List<Report> findByReportStatus(String status);
    List<Report> findByReportContainingAndReportStatus(String query, String status);
}
