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
        return reportRepository.findAll()
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
        int page = pageable.getPageNumber();

        // 페이지 번호가 0보다 작으면 기본값 0으로 설정
        if (page < 0) {
            page = 0;
        }

        int pageLimit = 10; // 한 페이지에 10개 표시

        Page<Report> reports =
                reportRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        return reports.map(reportMapper::toDto);
    }


}
