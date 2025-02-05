package com.bigProject.tellMe.controller.manager;

import com.bigProject.tellMe.dto.ReportDTO;
import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
@Slf4j // ✅ Lombok의 Slf4j 추가
public class ManagerController {
    private final ReportService reportService;
    private static final String REPORT_BASE_PATH = "C:/Users/User/BigProject/tellMe/tellMe-reports/";

    // ✅ 보고서 목록 조회 (페이지네이션 적용)
    @GetMapping("/report")
    public String reportBoard(@RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "status", required = false, defaultValue = "all") String status,
                              @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                              @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<ReportDTO> reportPage;

        if ((query == null || query.isEmpty()) && (status == null || status.equals("all"))) {
            reportPage = reportService.findAllPaged(pageable);
        } else {
            reportPage = reportService.searchReportsPaged(query, status, pageable);
        }

        log.info("Fetching reports - Query: {}, Status: {}, Current Page: {}, Total Pages: {}, Total Elements: {}",
                query, status, page, reportPage.getTotalPages(), reportPage.getTotalElements());

        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("status", status);

        return "manager/report";
    }




    // ✅ 특정 보고서 열기
    @GetMapping("report/view/{id}")
    public String viewReport(@PathVariable Long id) throws UnsupportedEncodingException {
        ReportDTO report = reportService.getReport(id);

        if (report == null || report.getReport() == null) {
            return "redirect:/error";
        }

        Path filePath = Paths.get(report.getReport());
        String encodedFileName = URLEncoder.encode(filePath.getFileName().toString(), StandardCharsets.UTF_8.toString());

        return "redirect:/manager/reports/" + encodedFileName;
    }

    // ✅ 파일 확장자별 Content-Type 지정
    private MediaType getMediaType(String filePath) {
        if (filePath.endsWith(".docx")) {
            return MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else if (filePath.endsWith(".hwp")) {
            return MediaType.valueOf("application/x-hwp");
        } else if (filePath.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    // ✅ 로컬 폴더에서 보고서 파일 제공
    @GetMapping("/reports/{fileName}")
    public ResponseEntity<Resource> getReportFile(@PathVariable String fileName) throws MalformedURLException, UnsupportedEncodingException {
        Path filePath = Paths.get(REPORT_BASE_PATH, fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

        return ResponseEntity.ok()
                .contentType(getMediaType(fileName))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }

    @GetMapping("/statistics")
    public String statisticsBoard() {
        return "manager/statistics";
    }
}
