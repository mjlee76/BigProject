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

    // ✅ 보고서 목록 조회 (Thymeleaf 페이지 렌더링)
    @GetMapping("/report")
    public String reportBoard(Model model) {
        List<ReportDTO> reports = reportService.findAll();
        model.addAttribute("reports", reports);
        return "manager/report"; // Thymeleaf 템플릿 경로
    }

    // ✅ 특정 보고서 웹뷰어에서 열기
    @GetMapping("report/view/{id}")
    public String viewReport(@PathVariable Long id) throws UnsupportedEncodingException {
        ReportDTO report = reportService.getReport(id);

        if (report == null || report.getReport() == null) {
            return "redirect:/error";
        }

        // 파일명 UTF-8 인코딩
        String encodedFileName = URLEncoder.encode(new File(report.getReport()).getName(), StandardCharsets.UTF_8.toString());

        // 리다이렉트 시 인코딩된 URL 사용
        return "redirect:/reports/" + encodedFileName;
    }

    @GetMapping("/statistics")
    public String statisticsBoard() {
        return "manager/statistics";
    }

}