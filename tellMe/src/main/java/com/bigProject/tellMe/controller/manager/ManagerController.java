package com.bigProject.tellMe.controller.manager;

import com.bigProject.tellMe.dto.ReportDTO;
import com.bigProject.tellMe.dto.StatisticsDTO;
import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.service.ReportService;
import com.bigProject.tellMe.service.StatisticsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


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

        int totalPages = reportPage.getTotalPages();

        // 5페이지씩 그룹으로 나누기
        int groupSize = 5;
        int currentGroup = (page - 1) / groupSize;
        int startPage = currentGroup * groupSize + 1;
        int endPage = Math.min(startPage + groupSize - 1, totalPages);

        // 이전 그룹, 다음 그룹 링크를 위한 로직
        int prevGroup = (currentGroup > 0) ? currentGroup - 1 : 0;
        int nextGroup = (currentGroup + 1) * groupSize < totalPages ? currentGroup + 1 : currentGroup;

        log.info("Fetching reports - Query: {}, Status: {}, Current Page: {}, Total Pages: {}, Total Elements: {}",
                query, status, page, totalPages, reportPage.getTotalElements());

        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prevGroup", prevGroup);
        model.addAttribute("nextGroup", nextGroup);
        model.addAttribute("query", query);
        model.addAttribute("status", status);

        return "manager/report";
    }






    // ✅ 특정 보고서 열기
    @GetMapping("report/view/{id}")
    public String viewReport(@PathVariable Long id) throws UnsupportedEncodingException {
        // 보고서 상태를 확인 완료로 변경
        reportService.updateReportStatus(id, ReportStatus.확인완료);

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



    @PostMapping("/report/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id) {
        try {
            // 상태를 '확인완료'로 변경
            reportService.updateReportStatus(id, ReportStatus.확인완료);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (Exception e) {
            // 에러 발생 시 500 상태 코드와 함께 실패 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"success\": false}");
        }
    }

    private final StatisticsService statisticsService;

    @GetMapping("/statistics")
    public String statisticsBoard(Model model) {
        // 통계 데이터를 가져와서 뷰로 전달
        StatisticsDTO statisticsDTO = statisticsService.getStatistics();


        // 오늘의 민원 수와 악성 민원의 시간대별 데이터
        Map<String, List<Long>> questionsAndMaliciousByHour = statisticsService.getQuestionsAndMaliciousByHour(LocalDate.now());
        model.addAttribute("statistics", statisticsDTO);
        model.addAttribute("questionsAndMaliciousByHour", questionsAndMaliciousByHour);

        return "manager/statistics";  // 통계 페이지
    }

<<<<<<< HEAD
=======
    @GetMapping("/today-question")
    public String todayQuestion(Model model) {
        // 통계 데이터를 가져와서 뷰로 전달
        StatisticsDTO statisticsDTO = statisticsService.getStatistics();

        // 증감률 계산
        double dailyChangeRate = statisticsService.calculateDailyChangeRate();

        // 오늘의 민원 수와 악성 민원의 시간대별 데이터
        Map<String, List<Long>> questionsAndMaliciousByHour = statisticsService.getQuestionsAndMaliciousByHour(LocalDate.now());
        model.addAttribute("statistics", statisticsDTO);
        model.addAttribute("questionsAndMaliciousByHour", questionsAndMaliciousByHour);
        model.addAttribute("dailyChangeRate", dailyChangeRate);  // 증감률 추가



        return "manager/today-question";  // 통계 페이지
    }

    // ManagerController.java

    @GetMapping("/download-csv")
    public void downloadStatisticsCsv(HttpServletResponse response) throws IOException {
        // CSV 데이터 생성
        String csvData = statisticsService.generateStatisticsCsv();

        // 응답 설정
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=statistics.csv");

        // 데이터 쓰기
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(csvData.getBytes(StandardCharsets.UTF_8));
        }
    }






<<<<<<< HEAD
>>>>>>> 13fde1d (실시간 민원 현황 상세페이지 기능 추가)
=======


>>>>>>> bc74830 (통계 추가)
}
