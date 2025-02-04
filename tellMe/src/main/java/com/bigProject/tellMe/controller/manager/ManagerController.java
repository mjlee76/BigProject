package com.bigProject.tellMe.controller.manager;

import com.bigProject.tellMe.dto.ReportDTO;
import com.bigProject.tellMe.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final ReportService reportService;
    // ✅ 보고서 기본 경로 (환경변수 없이 하드코딩)
    private static final String REPORT_BASE_PATH = "C:/Users/User/BigProject/tellMe/tellMe-reports/";


    // ✅ 보고서 목록 조회 (Thymeleaf 페이지 렌더링)
    @GetMapping("/report")
    public String reportBoard(@RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "status", required = false, defaultValue = "all") String status,
                              Model model) {
        List<ReportDTO> reports;

        if ((query == null || query.isEmpty()) && (status == null || status.equals("all"))) {
            reports = reportService.findAll(); // 전체 조회
        } else {
            reports = reportService.searchReports(query, status);
        }

        model.addAttribute("reports", reports);
        return "manager/report"; // Thymeleaf 템플릿 경로
    }


    // ✅ 특정 보고서 열기
    @GetMapping("report/view/{id}")
    public String viewReport(@PathVariable Long id) throws UnsupportedEncodingException {
        ReportDTO report = reportService.getReport(id);

        if (report == null || report.getReport() == null) {
            return "redirect:/error";
        }

        // ✅ 파일 경로를 tellMe-reports로 변경
        Path filePath = Paths.get(report.getReport());

        // ✅ 파일명 UTF-8 인코딩 (한글 지원)
        String encodedFileName = URLEncoder.encode(filePath.getFileName().toString(), StandardCharsets.UTF_8.toString());

        // ✅ 리다이렉트 시 변경된 URL 사용
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


    // ✅ 로컬 폴더에서 보고서 파일을 제공하는 엔드포인트
    @GetMapping("/reports/{fileName}")
    public ResponseEntity<Resource> getReportFile(@PathVariable String fileName) throws MalformedURLException, UnsupportedEncodingException {
        Path filePath = Paths.get(REPORT_BASE_PATH, fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // 파일명 UTF-8 인코딩
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