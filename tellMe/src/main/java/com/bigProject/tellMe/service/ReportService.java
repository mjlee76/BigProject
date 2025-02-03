package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.ReportDTO;
import com.bigProject.tellMe.entity.Report;
import com.bigProject.tellMe.mapper.ReportMapper;
import com.bigProject.tellMe.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportMapper reportMapper;
    private final ReportRepository reportRepository;

    // ✅ 모든 보고서 조회 (Entity → DTO 변환)
    public List<ReportDTO> findAll() {
        return reportRepository.findAll(Sort.by(Sort.Direction.DESC, "id")) // 정렬 추가
                .stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ 특정 보고서 조회
    public ReportDTO getReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 보고서를 찾을 수 없습니다."));
        return reportMapper.toDto(report);
    }

    // ✅ 보고서 목록을 페이지 단위로 조회
    public Page<ReportDTO> paging(Pageable pageable) {
        Page<Report> reports = reportRepository.findAll(PageRequest.of(
                Math.max(pageable.getPageNumber(), 0), // 0보다 작으면 0으로 설정
                10,
                Sort.by(Sort.Direction.DESC, "id")
        ));
        return reports.map(reportMapper::toDto);
    }

    // ✅ 검색 + 상태 필터 적용된 보고서 조회
    public List<ReportDTO> searchReports(String query, String status) {
        List<Report> reports;

        if (query != null && !query.isEmpty() && status != null && !status.equals("all")) {
            reports = reportRepository.findByReportContainingAndReportStatus(query, status);
        } else if (query != null && !query.isEmpty()) {
            reports = reportRepository.findByReportContaining(query);
        } else if (status != null && !status.equals("all")) {
            reports = reportRepository.findByReportStatus(status);
        } else {
            reports = reportRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }

        return reports.stream().map(reportMapper::toDto).collect(Collectors.toList());
    }
}
