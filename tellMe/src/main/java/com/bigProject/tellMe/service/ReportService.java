package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.ReportDTO;
import com.bigProject.tellMe.entity.Report;
import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.mapper.ReportMapper;
import com.bigProject.tellMe.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportMapper reportMapper;
    private final ReportRepository reportRepository;

    // ✅ 모든 보고서 조회 (페이지네이션 적용)
    public Page<ReportDTO> findAllPaged(Pageable pageable) {
        return reportRepository.findAll(pageable).map(reportMapper::toDto);
    }

    // ✅ 보고서 기본 경로 (환경변수 없이 하드코딩)
    private static final String REPORT_BASE_PATH = "C:/Users/User/BigProject/tellMe/tellMe-reports/";

    // ✅ 특정 보고서 조회 (경로 변경 반영)
    public ReportDTO getReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 보고서를 찾을 수 없습니다."));

        // ✅ 올바른 파일 경로 설정
        String fullPath = REPORT_BASE_PATH + report.getReport();
        report.setReport(fullPath);

        // ✅ 변경된 값이 적용된 DTO 반환
        ReportDTO reportDTO = reportMapper.toDto(report);
        reportDTO.setReport(fullPath);

        return reportDTO;
    }

    // ✅ 검색 + 상태 필터 적용된 보고서 조회 (페이지네이션 포함)
    public Page<ReportDTO> searchReportsPaged(String query, String status, Pageable pageable) {
        Page<Report> reports;


        if (query != null && !query.isEmpty() && status != null && !status.equals("all")) {
            ReportStatus reportStatus = ReportStatus.valueOf(status); // String → ENUM 변환
            reports = reportRepository.findByReportContainingAndReportStatus(query, reportStatus, pageable);
        } else if (query != null && !query.isEmpty()) {
            reports = reportRepository.findByReportContaining(query, pageable);
        } else if (status != null && !status.equals("all")) {
            ReportStatus reportStatus = ReportStatus.valueOf(status); // String → ENUM 변환
            reports = reportRepository.findByReportStatus(reportStatus, pageable);
        } else {
            reports = reportRepository.findAll(pageable);
        }

        return reports.map(reportMapper::toDto);
    }
    // ✅ 상태 변경

    public void updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 보고서를 찾을 수 없습니다."));

        // 상태를 확인 완료로 변경
        report.setReportStatus(status);
        reportRepository.save(report); // 상태 업데이트
    }

    // 보고서 상태별 카운트
    public long countReportsByStatus(ReportStatus reportStatus) {
        return reportRepository.countByReportStatus(reportStatus);
    }

}
